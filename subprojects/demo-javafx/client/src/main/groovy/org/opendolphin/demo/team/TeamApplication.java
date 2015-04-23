package org.opendolphin.demo.team;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ReflectionBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.binding.Converter;
import org.opendolphin.binding.JavaFxUtil;
import org.opendolphin.core.*;
import org.opendolphin.core.client.*;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.binding.JFXBinder.bind;
import static org.opendolphin.binding.JFXBinder.bindInfo;
import static org.opendolphin.demo.team.TeamMemberConstants.*;

public class TeamApplication extends Application {

    private final Button    ADD_BUTTON           = new Button("+");
    private final Button    SAVE                 = new Button("Save");
    private final Button    RESET                = new Button("Reset");
    private final Button    DELETE               = new Button("Delete");
    private final TextField FIELD_FIRST_NAME     = TextFieldBuilder.create().promptText("e.g. John").build();
    private final TextField FIELD_LAST_NAME      = TextFieldBuilder.create().promptText("e.g. Doe").build();
    private final CheckBox  CHECK_BOX_AVAILABLE  = new CheckBox();
    private final CheckBox  CHECK_BOX_CONTRACTOR = new CheckBox();
    private final Slider    SLIDER_WORKLOAD      = SliderBuilder.create().min(0).max(100).value(0).build();
    private final ComboBox  COMBO_BOX_FUNCTION   = ComboBoxBuilder.create()
        .items(FXCollections.<Object>observableArrayList(DefaultGroovyMethods.toList(FUNCTION_NAMES)))
        .prefWidth(213) // for the moment static. Bind later to col width
        .build();

    private Map<String, Image> lazyImageCache = new HashMap<String, Image>(10);

    private final ImageView IMAGE_FUNCTION = new ImageView(getImage(""));
    private final ImageView IMAGE_ANIM     = new ImageView(getImage(""));

    private TableView<Object> table;

    final ObservableList<PresentationModel> teamMembers = FXCollections.observableArrayList();

    GridPane form;

    static ClientDolphin clientDolphin;
    private ClientPresentationModel teamMemberMold;
    private ClientPresentationModel blankMold;
    private ClientAttribute selectedPmId;

    public TeamApplication() {
        teamMemberMold = clientDolphin.presentationModel(PM_ID_MOLD, (String) null,
                clientDolphin.createAttribute(ATT_FIRSTNAME, ""),
                clientDolphin.createAttribute(ATT_LASTNAME, ""),
                clientDolphin.createAttribute(ATT_FUNCTION, ""),
                clientDolphin.createAttribute(ATT_AVAILABLE, false),
                clientDolphin.createAttribute(ATT_CONTRACTOR, false),
                clientDolphin.createAttribute(ATT_WORKLOAD, 0));

        blankMold = clientDolphin.copy(teamMemberMold);

        selectedPmId = clientDolphin.createAttribute(ATT_SEL_PM_ID, null, QUAL_SEL_PM_ID, null); /* null for no selection*/
        clientDolphin.presentationModel(PM_ID_SELECTED, (String) null, selectedPmId);

    }

    // caching the graphic nodes that are used for rendering the table to avoid
    // creating too many nodes and excessive binding
    private final Map<String, Node> qualifier2graphics = new HashMap<String, Node>(200);

    TableColumn makeTableColumn(String header, final String attributeName) {
        // make sure attribute value changes lead to cell value changes
        final TableColumn column = JavaFxUtil.value(attributeName, new TableColumn(header));
        // display the cell value with the appropriate control and update dirty
        column.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(final TableColumn tableColumn) {
                return new TableCell<ClientAttributeWrapper, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        // re-rendering is triggered whenever an attribute fires change or the table thinks it is needed (which is quite often)
                        if (empty) return;
                        final TableRow tableRow = getTableRow();
                        if (null == tableRow) return;
                        final PresentationModel pm = (PresentationModel) tableRow.getItem();
                        if (null == pm) return;
                        final Attribute attribute = pm.getAt(attributeName);
                        // dirty handling is the same for all cell types
                        if (attribute.isDirty()) {
                            getStyleClass().add("cell-dirty");
                        } else {
                            getStyleClass().removeAll("cell-dirty");
                        }
                        if (attributeName.equals(ATT_FIRSTNAME) ||
                            attributeName.equals(ATT_LASTNAME) ||
                            attributeName.equals(ATT_FUNCTION)) {
                            setText(item.toString());
                            return;
                        }
                        setText(null);
                        // if item is not a string, then we need a graphical representation (node)
                        setAlignment(Pos.CENTER); // all graphics are centered
                        Node candidate = qualifier2graphics.get(attribute.getQualifier());
                        if (null != candidate) {
                            setGraphic(candidate);
                            return;
                        }

                        Node graphic = null;
                        if (item instanceof Boolean) {
                            graphic = makeBoundCircle(pm, attributeName);
                        }
                        if (item instanceof Number || item instanceof String) { // numbers may come as strings
                            graphic = makeBoundProgressBar(pm, attributeName);
                        }
                        qualifier2graphics.put(attribute.getQualifier(), graphic);
                        setGraphic(graphic);
                    }
                };
            }
        });
        return column;
    }

    private Circle makeBoundCircle(final PresentationModel pm, final String attributeName) {
        final Circle circle = CircleBuilder.create().radius(10).build();
        bind(attributeName).of(pm).using(new Converter() {
            @Override public Object convert(Object value) {
                ScaleTransitionBuilder.create().duration(Duration.millis(400)).node(circle).toY(((Boolean) value) ? 1 : 0.3).build().play();
                circle.getStyleClass().clear();
                circle.getStyleClass().add(((Boolean) value) ? "dot-selected" : "dot-unselected");
                return circle.getStyle();
            }
        }).to("style").of(circle);
        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                pm.getAt(attributeName).setValue(!(Boolean) pm.getAt(attributeName).getValue());
            }
        });
        return circle;
    }

    private ProgressBar makeBoundProgressBar(final PresentationModel pm, final String attributeName) {
        final ProgressBar bar = new ProgressBar();
        final Converter percentage = new Converter() {
            @Override public Object convert(Object value) {
                return Double.valueOf(value.toString()) / 100;
            }
        };
        bind(attributeName).of(pm).using(percentage).to("progress").of(bar);
        return bar;
    }

    @Override
    public void start(final Stage stage) throws Exception {

        final Font font = Font.loadFont(this.getClass().getResourceAsStream("/Eurostile-Demi.ttf"), 18);
        if (null == font) System.out.println("could not load font");

        table = TableViewBuilder.create()
            .columns(
                makeTableColumn("First Name", ATT_FIRSTNAME),
                makeTableColumn("Last Name", ATT_LASTNAME),
                makeTableColumn("Function", ATT_FUNCTION),
                makeTableColumn("Available", ATT_AVAILABLE),
                makeTableColumn("Contractor", ATT_CONTRACTOR),
                makeTableColumn("Workload", ATT_WORKLOAD))
            .items((ObservableList) teamMembers)
            .columnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
            .placeholder(TextBuilder.create().text("Please add a team member").id("no-content").build())
            .build();

        ADD_BUTTON.setTranslateX(5); // quick hack for alignment
        BorderPane root = BorderPaneBuilder.create()
            .center( // table view in the center, the one to grow and shrink
                VBoxBuilder.create().id("master").children(
                    ADD_BUTTON,
                    table
                ).build()
            )
            .right( // detail view to the right
                form = GridPaneBuilder.create()
                    .disable(true)
                    .effect(new BoxBlur())
                    .id("detail")
                    .vgap(5).hgap(10)
                    .columnConstraints(
                        ColumnConstraintsBuilder.create().halignment(HPos.RIGHT).build(),
                        ColumnConstraintsBuilder.create().halignment(HPos.LEFT).build()
                    )
                    .build()
            ).build();

        final Pane imagePane = PaneBuilder.create().children(IMAGE_ANIM, IMAGE_FUNCTION).build();
        imagePane.setEffect(ReflectionBuilder.create().fraction(0.2).topOpacity(0.15).build());
        VBox functionView = VBoxBuilder.create().children(COMBO_BOX_FUNCTION, imagePane).build();

        int row = 0;
        form.add(new Label("First Name"), 0, row);
        form.add(FIELD_FIRST_NAME, 1, row++);
        form.add(new Label("Last  Name"), 0, row);
        form.add(FIELD_LAST_NAME, 1, row++);
        form.add(new Label("Function"), 0, row);
        form.add(functionView, 1, row++);
        form.add(new Label("Available"), 0, row);
        form.add(CHECK_BOX_AVAILABLE, 1, row++);
        form.add(new Label("Contractor"), 0, row);
        form.add(CHECK_BOX_CONTRACTOR, 1, row++);
        form.add(new Label("Workload"), 0, row);
        form.add(SLIDER_WORKLOAD, 1, row++);

        HBox buttonBar = HBoxBuilder.create().spacing(5).children(SAVE, RESET, DELETE).build();
        form.add(buttonBar, 1, row);

        setupBinding();
        addClientSideAction();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Team Members in JavaFX");
        scene.getStylesheets().add("/team.css");

        // preload the images
        for (String name : FUNCTION_NAMES) {
            getImage(name);
        }
        clientDolphin.send(CMD_INIT, new OnFinishedHandlerAdapter() {
            @Override public void onFinished(List<ClientPresentationModel> presentationModels) {
                stage.show();
                clientDolphin.startPushListening(ACTION_ON_PUSH, CMD_RELEASE);
            }
        });
    }

    private Image getImage(String function) {
        if ("".equals(function)) function = "Unselected";
        if (!lazyImageCache.containsKey(function)) {
            lazyImageCache.put(function, new Image("http://people.canoo.com/mittie/rolePics/" + function + ".jpg"));
        }
        return lazyImageCache.get(function);
    }

    final   Duration  imageTransitionDuration = Duration.millis(200);

    private ParallelTransition rollDown = ParallelTransitionBuilder.create().children(
        TranslateTransitionBuilder.create().duration(imageTransitionDuration).node(IMAGE_FUNCTION).fromY(-150).toY(0).build(),
        ScaleTransitionBuilder.create().duration(imageTransitionDuration).node(IMAGE_FUNCTION).fromY(0).toY(1).build()
    ).onFinished(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            IMAGE_ANIM.setImage(IMAGE_FUNCTION.getImage());
        }
    }).build();

    private void setupBinding() {

        // detail binding

        bind("text").of(FIELD_FIRST_NAME).to(ATT_FIRSTNAME).of(teamMemberMold);
        bind(ATT_FIRSTNAME).of(teamMemberMold).to("text").of(FIELD_FIRST_NAME);

        bind("text").of(FIELD_LAST_NAME).to(ATT_LASTNAME).of(teamMemberMold);
        bind(ATT_LASTNAME).of(teamMemberMold).to("text").of(FIELD_LAST_NAME);

        COMBO_BOX_FUNCTION.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                teamMemberMold.getAt(ATT_FUNCTION).setValue(COMBO_BOX_FUNCTION.getValue());
            }
        });
        teamMemberMold.getAt(ATT_FUNCTION).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                COMBO_BOX_FUNCTION.setValue(evt.getNewValue().toString());
                IMAGE_FUNCTION.setImage(getImage(evt.getNewValue().toString()));
                rollDown.play();
            }
        });

        bind("selected").of(CHECK_BOX_AVAILABLE).to(ATT_AVAILABLE).of(teamMemberMold);
        bind(ATT_AVAILABLE).of(teamMemberMold).to("selected").of(CHECK_BOX_AVAILABLE);

        bind("selected").of(CHECK_BOX_CONTRACTOR).to(ATT_CONTRACTOR).of(teamMemberMold);
        bind(ATT_CONTRACTOR).of(teamMemberMold).to("selected").of(CHECK_BOX_CONTRACTOR);

        bind("value").of(SLIDER_WORKLOAD).to(ATT_WORKLOAD).of(teamMemberMold, new Converter() {
            @Override
            public Object convert(Object value) {
                return ((Double) value).intValue();
            } // round to int
        });
        bind(ATT_WORKLOAD).of(teamMemberMold).to("value").of(SLIDER_WORKLOAD, new Converter() {
            @Override
            public Object convert(Object value) {
                if (SLIDER_WORKLOAD.isPressed()) return SLIDER_WORKLOAD.getValue(); // do not update while we work
                if (value instanceof String) return Integer.valueOf((String) value);
                return ((Number) value).intValue();
            } // round to int
        });

        // dirty status in detail view

        // todo: could go into dolphin
        teamMemberMold.addPropertyChangeListener("dirty", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ((Boolean) evt.getNewValue()) {
                    form.getStyleClass().add("detail-dirty");
                } else {
                    form.getStyleClass().removeAll("detail-dirty");
                }
            }
        });

        // todo: could go into dolphin
        final Converter inverter = new Converter() {
            @Override
            public Object convert(Object value) {
                return (!(Boolean) value);
            }
        };

        bindInfo("dirty").of(teamMemberMold).using(inverter).to("disable").of(SAVE);
        bindInfo("dirty").of(teamMemberMold).using(inverter).to("disable").of(RESET);

        // list binding

        clientDolphin.addModelStoreListener(TYPE_TEAM_MEMBER, new ModelStoreListener() {
            @Override
            public void modelStoreChanged(ModelStoreEvent event) {
                final PresentationModel pm = event.getPresentationModel();
                if (event.getType() == ModelStoreEvent.Type.ADDED) {
                    teamMembers.add(pm);
                    // selection comes from server side. nothing to do here
                }
                if (event.getType() == ModelStoreEvent.Type.REMOVED) {
                    teamMembers.remove(pm);
                    if (pm.getId().equals(selectedPmId.getValue())) { // we may not have a selection any more
                        final PresentationModel nextPm = clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).get(0);
                        selectedPmId.setValue(nextPm == null ? null : nextPm.getId());
                    }
                }
            }
        });

        // selection Binding

        // keep selectedPmId in sync with table selection
        selectedPmId.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (null == evt.getNewValue()) {
                    table.getSelectionModel().clearSelection();
                    return;
                }
                for (PresentationModel model : teamMembers) {
                    if (model.getId().equals(evt.getNewValue())) {
                        table.getSelectionModel().select(model);
                        break;
                    }
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object old, Object newSelection) {
                if (null != newSelection) {
                    GClientPresentationModel selectedPm = (GClientPresentationModel) newSelection;
                    selectedPmId.setValue(selectedPm.getId());
                    clientDolphin.apply(selectedPm).to(teamMemberMold);
                    form.setDisable(false);
                    form.setEffect(null);
                } else {
                    selectedPmId.setValue(null);
                    clientDolphin.apply(blankMold).to(teamMemberMold);
                    form.setDisable(true);
                    form.setEffect(new BoxBlur());
                }
            }
        });
    }

    private void addClientSideAction() {

        ADD_BUTTON.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ADD_BUTTON.setDisable(true);
                clientDolphin.send(CMD_ADD, new OnFinishedHandlerAdapter() {
                    @Override
                    public void onFinished(List<ClientPresentationModel> presentationModels) {
                        ADD_BUTTON.setDisable(false);
                    }
                });
            }
        });

        SAVE.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                SAVE.setDisable(true);
                clientDolphin.send(CMD_SAVE);
            }
        });

        RESET.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                teamMemberMold.reset();
            }
        });

        DELETE.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DELETE.setDisable(true);
                clientDolphin.send(CMD_REMOVE, new OnFinishedHandlerAdapter() {
                    @Override
                    public void onFinished(List<ClientPresentationModel> presentationModels) {
                        DELETE.setDisable(false);
                    }
                });
            }
        });
    }
}