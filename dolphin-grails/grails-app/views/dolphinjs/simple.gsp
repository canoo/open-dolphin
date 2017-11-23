<%--
  A simple page that works together with opendolphin.js for showcasing the use of OpenDolphin in a
  single-page javascript application that is delivered from a grails GSP.
  The benefit here is that one can share references between the worlds of Java, Html, and JavaScript.
  The Java application code is in TutorialAction and we use its constants in JavaScript to
  relate to IDs, commands, and attribute names.
  We share references between Html and JavaScript by using the same ids for Html elements
  and JavaScript lookup code.
  author: dierk.koenig
--%>

<%@ page import="org.opendolphin.demo.TutorialAction" contentType="text/html;charset=UTF-8" %>

<%
  dolphinUrl      = createLink(controller: 'dolphin', absolute: true) - 'index'

  // page-wide constants to share as HTML ids and JS lookups
  textInputId     = "textInput"
  label           = "label"
  logActionButton = "logActionButton"
  addButton       = "addButton"
  list            = "list"
  showCode        = "showCode"
  dolphinCode     = "dolphinCode"
  range           = "range"
  rangeLabel      = "rangeLabel"
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta name="layout" content="demo"> <%-- links to opendolphin.js, require.js, and bootstrap --%>
  <title>Dolphin.js Simple Page from GSP</title>
</head>

<body>
<div class="container">
  <div class="row" style="margin-top: 4em;">
    <div class="offset2 span4">
      <h1>Dolphin.js</h1>
      <p class="lead">A simple page with bindings</p>
    </div>
    <div class="span3">
      <a href="http://open-dolphin.org">
        <img src="${resource(dir: 'img', file: 'open_dolphin_logo.png')}" alt="Open Dolphin">
      </a>
    </div>
  </div>

  <div class="row">
    <div class="offset1 span8">
    <fieldset>
      <legend>Step 1 - Some simple bindings</legend>
      <span class="help-block">
        Type some text in the input field to test the binding with the label.<br>
        Click the button to let it update with server-side content.
      </span>
      <input id="${textInputId}" type="text" placeholder="Type some text..."/>
      <p>Label: <span id="${label}"></span></p>
      <button id="${logActionButton}" class="btn btn-primary">Server Modification</button>

      <span class="help-block">
        Drag the slider to see the label being updated.
      </span>
      <input id="${range}" type="range" value="10" min="0" max="100">
      <p>Label: <span id="${rangeLabel}"></span></p>

      <span class="help-block">
        Click to get new content from the server side, bound to a list.
      </span>
      <button id="${addButton}" class="btn btn-primary">Add Server Data</button>
      <div id="${list}"></div>

    </fieldset>

    <hr>
    <a href="https://github.com/canoo/open-dolphin/blob/master/dolphin-grails/grails-app/views/dolphinjs/simple.gsp">source code</a>
    </div>

  </div>
</div>

<script>
  var dolphin = opendolphin.makeDolphin()
      .url("${dolphinUrl}")
      .strictMode(false)
      .build();

    dolphin.reset({
      onSuccess: function() {

    // create named PM with attribute on the client side
    var textAttribute  = dolphin.attribute("${TutorialAction.ATTR_ID}", null, '');
    var rangeAttribute = dolphin.attribute("${range}", null, '');

    dolphin.presentationModel(
      "${TutorialAction.PM_ID_MODEL}", undefined,
      textAttribute, rangeAttribute
    );

    // send echo command on button click
    var logActionButton = document.getElementById("${logActionButton}");
    logActionButton.addEventListener("click", function () {
      dolphin.send("${TutorialAction.CMD_ECHO}");
    });

    // bind text input field to pm textAttribute bidirectionally
    var textInput = document.getElementById("${textInputId}");
    textInput.addEventListener("input", function () {
      textAttribute.setValue(textInput.value);
    });
    textAttribute.onValueChange(function (data) {
      textInput.value = data.newValue;
    });

    // bind label to textAttribute
    var label = document.getElementById("${label}");
    textAttribute.onValueChange(function (data) {
      label.innerHTML = data.newValue;
    });

    // bind range input field to pm rangeAttribute and label to pm
    var rangeInput  = document.getElementById("${range}");
    var rangeOutput = document.getElementById("${rangeLabel}");
    rangeInput.addEventListener("input", function () {
      rangeAttribute.setValue(rangeInput.value);
    });
    rangeAttribute.onValueChange(function (data) {
      rangeOutput.innerHTML = data.newValue;
    });

    // send add command on button click and add a div for each received model
    var addButton = document.getElementById("${addButton}");
    var list      = document.getElementById("${list}");
    addButton.addEventListener("click", function () {
      dolphin.send("${TutorialAction.CMD_ADD}", {onFinished: function (models) {
        models.forEach(function (model) {
          // console.log(model); // enable for debugging purposes
          var element = document.createElement("div");
          element.innerHTML = model.presentationModelType + ": " +
                              model.attributes[0].value + " " +  // String value
                              model.attributes[1].value + " " +  // String value
                              model.attributes[2].value + " " +  // Integer value
                              model.attributes[3].value + " " +  // Long value
                              model.attributes[4].value + " " +  // Float value
                              model.attributes[5].value + " "    // Double value
          list.appendChild(element);
        })
      }});
    });

      } // onSuccess
    });

</script>

</body>
</html>