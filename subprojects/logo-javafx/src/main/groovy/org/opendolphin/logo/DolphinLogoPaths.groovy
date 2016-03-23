/*
 * Copyright 2012-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendolphin.logo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.LinkedList;
import java.util.List;

public class DolphinLogoPaths {

    public Path mainbody;
    public Path rip9;
    public Path rip8;
    public Path rip7;
    public Path rip6;
    public Path rip5;
    public Path rip4;
    public Path rip3;
    public Path rip2;
    public Path rip1;
    public Path lateralline;
    public Path eye;
    public Path sidefin1;
    public Path sidefin2;
    public Path belly;
    public Path backfin2;
    public Path backfin1;
    public Path tailfinstart;
    public Path tailfin1;
    public Path tailfin2;

    public def body  = { [mainbody,lateralline,eye,belly]                                             }
    public def rips  = { (1..9).collect{this."rip$it"}}
    public def fins  = { [sidefin1, sidefin2, backfin1, backfin2, tailfinstart, tailfin1, tailfin2]}
    public def paths = { [body(), rips(), fins()].flatten()}

    // ******************** Drawing related ***********************************
    public DolphinLogoPaths(final double WIDTH, final double HEIGHT) {
        mainbody = new Path();
        mainbody.setFillRule(FillRule.EVEN_ODD);
        mainbody.getElements().add(new MoveTo(0.2144638403990025 * WIDTH, 0.05058365758754864 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.1970074812967581 * WIDTH, 0.06614785992217899 * HEIGHT,
                                                    0.16209476309226933 * WIDTH, 0.10505836575875487 * HEIGHT,
                                                    0.14962593516209477 * WIDTH, 0.1245136186770428 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.14214463840399003 * WIDTH, 0.13229571984435798 * HEIGHT,
                                                    0.1172069825436409 * WIDTH, 0.15953307392996108 * HEIGHT,
                                                    0.10224438902743142 * WIDTH, 0.16342412451361868 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.08478802992518704 * WIDTH, 0.17120622568093385 * HEIGHT,
                                                    0.04239401496259352 * WIDTH, 0.17120622568093385 * HEIGHT,
                                                    0.0199501246882793 * WIDTH, 0.17898832684824903 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.017456359102244388 * WIDTH, 0.17898832684824903 * HEIGHT,
                                                    0.004987531172069825 * WIDTH, 0.19066147859922178 * HEIGHT,
                                                    0.0024937655860349127 * WIDTH, 0.19844357976653695 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.0, 0.20233463035019456 * HEIGHT,
                                                    -0.0024937655860349127 * WIDTH, 0.22957198443579765 * HEIGHT,
                                                    0.00997506234413965 * WIDTH, 0.2490272373540856 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.029925187032418952 * WIDTH, 0.2840466926070039 * HEIGHT,
                                                    0.04738154613466334 * WIDTH, 0.3035019455252918 * HEIGHT,
                                                    0.10473815461346633 * WIDTH, 0.3346303501945525 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.12967581047381546 * WIDTH, 0.35019455252918286 * HEIGHT,
                                                    0.18204488778054864 * WIDTH, 0.377431906614786 * HEIGHT,
                                                    0.2119700748129676 * WIDTH, 0.3968871595330739 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.22693266832917705 * WIDTH, 0.4046692607003891 * HEIGHT,
                                                    0.24438902743142144 * WIDTH, 0.41245136186770426 * HEIGHT,
                                                    0.2543640897755611 * WIDTH, 0.4280155642023346 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.2593516209476309 * WIDTH, 0.4357976653696498 * HEIGHT,
                                                    0.26683291770573564 * WIDTH, 0.44357976653696496 * HEIGHT,
                                                    0.27680798004987534 * WIDTH, 0.45136186770428016 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.2793017456359102 * WIDTH, 0.45525291828793774 * HEIGHT,
                                                    0.2817955112219451 * WIDTH, 0.44357976653696496 * HEIGHT,
                                                    0.27680798004987534 * WIDTH, 0.4357976653696498 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.26683291770573564 * WIDTH, 0.42023346303501946 * HEIGHT,
                                                    0.26184538653366585 * WIDTH, 0.41245136186770426 * HEIGHT,
                                                    0.2518703241895262 * WIDTH, 0.40077821011673154 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.24438902743142144 * WIDTH, 0.39299610894941633 * HEIGHT,
                                                    0.23940149625935161 * WIDTH, 0.38910505836575876 * HEIGHT,
                                                    0.2169576059850374 * WIDTH, 0.3735408560311284 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.19451371571072318 * WIDTH, 0.36186770428015563 * HEIGHT,
                                                    0.11221945137157108 * WIDTH, 0.3151750972762646 * HEIGHT,
                                                    0.09226932668329177 * WIDTH, 0.3035019455252918 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.04488778054862843 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                    0.03740648379052369 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                    0.02493765586034913 * WIDTH, 0.245136186770428 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.017456359102244388 * WIDTH, 0.23346303501945526 * HEIGHT,
                                                    0.014962593516209476 * WIDTH, 0.22178988326848248 * HEIGHT,
                                                    0.014962593516209476 * WIDTH, 0.21011673151750973 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.014962593516209476 * WIDTH, 0.20622568093385213 * HEIGHT,
                                                    0.0199501246882793 * WIDTH, 0.20233463035019456 * HEIGHT,
                                                    0.02493765586034913 * WIDTH, 0.20233463035019456 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.03740648379052369 * WIDTH, 0.19455252918287938 * HEIGHT,
                                                    0.08478802992518704 * WIDTH, 0.19066147859922178 * HEIGHT,
                                                    0.10224438902743142 * WIDTH, 0.1867704280155642 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.11221945137157108 * WIDTH, 0.1867704280155642 * HEIGHT,
                                                    0.12468827930174564 * WIDTH, 0.17898832684824903 * HEIGHT,
                                                    0.14962593516209477 * WIDTH, 0.15953307392996108 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.16957605985037408 * WIDTH, 0.14007782101167315 * HEIGHT,
                                                    0.18453865336658354 * WIDTH, 0.11673151750972763 * HEIGHT,
                                                    0.20947630922693267 * WIDTH, 0.09727626459143969 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.22194513715710723 * WIDTH, 0.08949416342412451 * HEIGHT,
                                                    0.256857855361596 * WIDTH, 0.07003891050583658 * HEIGHT,
                                                    0.30423940149625933 * WIDTH, 0.07003891050583658 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.30423940149625933 * WIDTH, 0.07003891050583658 * HEIGHT,
                                                    0.3765586034912718 * WIDTH, 0.07392996108949416 * HEIGHT,
                                                    0.39900249376558605 * WIDTH, 0.08171206225680934 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.4837905236907731 * WIDTH, 0.10894941634241245 * HEIGHT,
                                                    0.5511221945137157 * WIDTH, 0.1556420233463035 * HEIGHT,
                                                    0.5885286783042394 * WIDTH, 0.19066147859922178 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.5960099750623441 * WIDTH, 0.19844357976653695 * HEIGHT,
                                                    0.6783042394014963 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                    0.7381546134663342 * WIDTH, 0.41245136186770426 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.7880299251870324 * WIDTH, 0.5291828793774319 * HEIGHT,
                                                    0.7955112219451371 * WIDTH, 0.6070038910505836 * HEIGHT,
                                                    0.8054862842892768 * WIDTH, 0.6536964980544747 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.8054862842892768 * WIDTH, 0.6575875486381323 * HEIGHT,
                                                    0.800498753117207 * WIDTH, 0.603112840466926 * HEIGHT,
                                                    0.7955112219451371 * WIDTH, 0.5680933852140078 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.770573566084788 * WIDTH, 0.4474708171206226 * HEIGHT,
                                                    0.7531172069825436 * WIDTH, 0.38910505836575876 * HEIGHT,
                                                    0.7281795511221946 * WIDTH, 0.3346303501945525 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.6907730673316709 * WIDTH, 0.25680933852140075 * HEIGHT,
                                                    0.6384039900249376 * WIDTH, 0.16731517509727625 * HEIGHT,
                                                    0.5386533665835411 * WIDTH, 0.0933852140077821 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.5211970074812967 * WIDTH, 0.07782101167315175 * HEIGHT,
                                                    0.4239401496259352 * WIDTH, 0.019455252918287938 * HEIGHT,
                                                    0.341645885286783 * WIDTH, 0.019455252918287938 * HEIGHT));
        mainbody.getElements().add(new CubicCurveTo(0.3167082294264339 * WIDTH, 0.019455252918287938 * HEIGHT,
                                                    0.24688279301745636 * WIDTH, 0.027237354085603113 * HEIGHT,
                                                    0.2144638403990025 * WIDTH, 0.05058365758754864 * HEIGHT));
        mainbody.getElements().add(new ClosePath());

        final Paint MAINBODY_FILL = Color.BLACK;
        mainbody.setFill(MAINBODY_FILL);
        mainbody.setStroke(null);

        rip9 = new Path();
        rip9.setFillRule(FillRule.EVEN_ODD);
        rip9.getElements().add(new MoveTo(0.6932668329177057 * WIDTH, 0.4046692607003891 * HEIGHT));
        rip9.getElements().add(new CubicCurveTo(0.6907730673316709 * WIDTH, 0.3968871595330739 * HEIGHT,
                                                0.6932668329177057 * WIDTH, 0.39299610894941633 * HEIGHT,
                                                0.6907730673316709 * WIDTH, 0.38132295719844356 * HEIGHT));
        rip9.getElements().add(new CubicCurveTo(0.6882793017456359 * WIDTH, 0.3968871595330739 * HEIGHT,
                                                0.685785536159601 * WIDTH, 0.40077821011673154 * HEIGHT,
                                                0.6783042394014963 * WIDTH, 0.41245136186770426 * HEIGHT));
        rip9.getElements().add(new CubicCurveTo(0.685785536159601 * WIDTH, 0.4046692607003891 * HEIGHT,
                                                0.6882793017456359 * WIDTH, 0.40077821011673154 * HEIGHT,
                                                0.6932668329177057 * WIDTH, 0.4046692607003891 * HEIGHT));
        rip9.getElements().add(new ClosePath());
        //RIP9.getStyleClass().add("dolphin-rip9");
        final Paint RIP9_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip9.setFill(RIP9_FILL);
        rip9.setStroke(null);

        rip8 = new Path();
        rip8.setFillRule(FillRule.EVEN_ODD);
        rip8.getElements().add(new MoveTo(0.6608478802992519 * WIDTH, 0.3657587548638132 * HEIGHT));
        rip8.getElements().add(new CubicCurveTo(0.655860349127182 * WIDTH, 0.35797665369649806 * HEIGHT,
                                                0.6583541147132169 * WIDTH, 0.35019455252918286 * HEIGHT,
                                                0.6583541147132169 * WIDTH, 0.33852140077821014 * HEIGHT));
        rip8.getElements().add(new CubicCurveTo(0.655860349127182 * WIDTH, 0.3540856031128405 * HEIGHT,
                                                0.6533665835411472 * WIDTH, 0.35797665369649806 * HEIGHT,
                                                0.6408977556109726 * WIDTH, 0.377431906614786 * HEIGHT));
        rip8.getElements().add(new CubicCurveTo(0.6508728179551122 * WIDTH, 0.3657587548638132 * HEIGHT,
                                                0.655860349127182 * WIDTH, 0.36186770428015563 * HEIGHT,
                                                0.6608478802992519 * WIDTH, 0.3657587548638132 * HEIGHT));
        rip8.getElements().add(new ClosePath());
        //RIP8.getStyleClass().add("dolphin-rip8");
        final Paint RIP8_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip8.setFill(RIP8_FILL);
        rip8.setStroke(null);

        rip7 = new Path();
        rip7.setFillRule(FillRule.EVEN_ODD);
        rip7.getElements().add(new MoveTo(0.628428927680798 * WIDTH, 0.33073929961089493 * HEIGHT));
        rip7.getElements().add(new CubicCurveTo(0.6259351620947631 * WIDTH, 0.32684824902723736 * HEIGHT,
                                                0.6234413965087282 * WIDTH, 0.31906614785992216 * HEIGHT,
                                                0.6234413965087282 * WIDTH, 0.3035019455252918 * HEIGHT));
        rip7.getElements().add(new CubicCurveTo(0.6234413965087282 * WIDTH, 0.32684824902723736 * HEIGHT,
                                                0.6209476309226932 * WIDTH, 0.32684824902723736 * HEIGHT,
                                                0.6109725685785536 * WIDTH, 0.3463035019455253 * HEIGHT));
        rip7.getElements().add(new CubicCurveTo(0.6184538653366584 * WIDTH, 0.3346303501945525 * HEIGHT,
                                                0.6234413965087282 * WIDTH, 0.33073929961089493 * HEIGHT,
                                                0.628428927680798 * WIDTH, 0.33073929961089493 * HEIGHT));
        rip7.getElements().add(new ClosePath());
        //RIP7.getStyleClass().add("dolphin-rip7");
        final Paint RIP7_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip7.setFill(RIP7_FILL);
        rip7.setStroke(null);

        rip6 = new Path();
        rip6.setFillRule(FillRule.EVEN_ODD);
        rip6.getElements().add(new MoveTo(0.5960099750623441 * WIDTH, 0.3035019455252918 * HEIGHT));
        rip6.getElements().add(new CubicCurveTo(0.5910224438902744 * WIDTH, 0.29571984435797666 * HEIGHT,
                                                0.5910224438902744 * WIDTH, 0.2918287937743191 * HEIGHT,
                                                0.5935162094763092 * WIDTH, 0.2723735408560311 * HEIGHT));
        rip6.getElements().add(new CubicCurveTo(0.5910224438902744 * WIDTH, 0.2918287937743191 * HEIGHT,
                                                0.5885286783042394 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.5785536159600998 * WIDTH, 0.33073929961089493 * HEIGHT));
        rip6.getElements().add(new CubicCurveTo(0.5860349127182045 * WIDTH, 0.3151750972762646 * HEIGHT,
                                                0.5910224438902744 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                0.5960099750623441 * WIDTH, 0.3035019455252918 * HEIGHT));
        rip6.getElements().add(new ClosePath());
        //RIP6.getStyleClass().add("dolphin-rip6");
        final Paint RIP6_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip6.setFill(RIP6_FILL);
        rip6.setStroke(null);

        rip5 = new Path();
        rip5.setFillRule(FillRule.EVEN_ODD);
        rip5.getElements().add(new MoveTo(0.5311720698254364 * WIDTH, 0.3229571984435798 * HEIGHT));
        rip5.getElements().add(new CubicCurveTo(0.5486284289276808 * WIDTH, 0.29571984435797666 * HEIGHT,
                                                0.5461346633416458 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.5486284289276808 * WIDTH, 0.28793774319066145 * HEIGHT));
        rip5.getElements().add(new CubicCurveTo(0.5486284289276808 * WIDTH, 0.2840466926070039 * HEIGHT,
                                                0.5511221945137157 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.5536159600997507 * WIDTH, 0.28793774319066145 * HEIGHT));
        rip5.getElements().add(new CubicCurveTo(0.5486284289276808 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                0.5536159600997507 * WIDTH, 0.27626459143968873 * HEIGHT,
                                                0.5511221945137157 * WIDTH, 0.23346303501945526 * HEIGHT));
        rip5.getElements().add(new CubicCurveTo(0.5486284289276808 * WIDTH, 0.2607003891050584 * HEIGHT,
                                                0.5461346633416458 * WIDTH, 0.2840466926070039 * HEIGHT,
                                                0.5311720698254364 * WIDTH, 0.3229571984435798 * HEIGHT));
        rip5.getElements().add(new ClosePath());
        //RIP5.getStyleClass().add("dolphin-rip5");
        final Paint RIP5_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip5.setFill(RIP5_FILL);
        rip5.setStroke(null);

        rip4 = new Path();
        rip4.setFillRule(FillRule.EVEN_ODD);
        rip4.getElements().add(new MoveTo(0.5087281795511222 * WIDTH, 0.26848249027237353 * HEIGHT));
        rip4.getElements().add(new CubicCurveTo(0.49875311720698257 * WIDTH, 0.25680933852140075 * HEIGHT,
                                                0.5012468827930174 * WIDTH, 0.2490272373540856 * HEIGHT,
                                                0.5037406483790524 * WIDTH, 0.20233463035019456 * HEIGHT));
        rip4.getElements().add(new CubicCurveTo(0.49875311720698257 * WIDTH, 0.245136186770428 * HEIGHT,
                                                0.49875311720698257 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.4713216957605985 * WIDTH, 0.33073929961089493 * HEIGHT));
        rip4.getElements().add(new CubicCurveTo(0.4937655860349127 * WIDTH, 0.29571984435797666 * HEIGHT,
                                                0.4937655860349127 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                0.5087281795511222 * WIDTH, 0.26848249027237353 * HEIGHT));
        rip4.getElements().add(new ClosePath());
        //RIP4.getStyleClass().add("dolphin-rip4");
        final Paint RIP4_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip4.setFill(RIP4_FILL);
        rip4.setStroke(null);

        rip3 = new Path();
        rip3.setFillRule(FillRule.EVEN_ODD);
        rip3.getElements().add(new MoveTo(0.4688279301745636 * WIDTH, 0.25680933852140075 * HEIGHT));
        rip3.getElements().add(new CubicCurveTo(0.4513715710723192 * WIDTH, 0.245136186770428 * HEIGHT,
                                                0.4538653366583541 * WIDTH, 0.19844357976653695 * HEIGHT,
                                                0.4513715710723192 * WIDTH, 0.16342412451361868 * HEIGHT));
        rip3.getElements().add(new CubicCurveTo(0.4513715710723192 * WIDTH, 0.22568093385214008 * HEIGHT,
                                                0.4463840399002494 * WIDTH, 0.2490272373540856 * HEIGHT,
                                                0.44139650872817954 * WIDTH, 0.2723735408560311 * HEIGHT));
        rip3.getElements().add(new CubicCurveTo(0.42144638403990026 * WIDTH, 0.3229571984435798 * HEIGHT,
                                                0.4164588528678304 * WIDTH, 0.3229571984435798 * HEIGHT,
                                                0.38403990024937656 * WIDTH, 0.38910505836575876 * HEIGHT));
        rip3.getElements().add(new CubicCurveTo(0.4139650872817955 * WIDTH, 0.3463035019455253 * HEIGHT,
                                                0.4513715710723192 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.4688279301745636 * WIDTH, 0.25680933852140075 * HEIGHT));
        rip3.getElements().add(new ClosePath());
        //RIP3.getStyleClass().add("dolphin-rip3");
        final Paint RIP3_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip3.setFill(RIP3_FILL);
        rip3.setStroke(null);

        rip2 = new Path();
        rip2.setFillRule(FillRule.EVEN_ODD);
        rip2.getElements().add(new MoveTo(0.4139650872817955 * WIDTH, 0.245136186770428 * HEIGHT));
        rip2.getElements().add(new CubicCurveTo(0.39900249376558605 * WIDTH, 0.22568093385214008 * HEIGHT,
                                                0.4114713216957606 * WIDTH, 0.1867704280155642 * HEIGHT,
                                                0.4089775561097257 * WIDTH, 0.14007782101167315 * HEIGHT));
        rip2.getElements().add(new CubicCurveTo(0.4014962593516209 * WIDTH, 0.20233463035019456 * HEIGHT,
                                                0.39900249376558605 * WIDTH, 0.22568093385214008 * HEIGHT,
                                                0.3940149625935162 * WIDTH, 0.2490272373540856 * HEIGHT));
        rip2.getElements().add(new CubicCurveTo(0.38154613466334164 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.3765586034912718 * WIDTH, 0.3035019455252918 * HEIGHT,
                                                0.3266832917705736 * WIDTH, 0.39299610894941633 * HEIGHT));
        rip2.getElements().add(new CubicCurveTo(0.35910224438902744 * WIDTH, 0.3463035019455253 * HEIGHT,
                                                0.371571072319202 * WIDTH, 0.33073929961089493 * HEIGHT,
                                                0.38403990024937656 * WIDTH, 0.3035019455252918 * HEIGHT));
        rip2.getElements().add(new CubicCurveTo(0.39900249376558605 * WIDTH, 0.2723735408560311 * HEIGHT,
                                                0.40399002493765584 * WIDTH, 0.2529182879377432 * HEIGHT,
                                                0.4139650872817955 * WIDTH, 0.245136186770428 * HEIGHT));
        rip2.getElements().add(new ClosePath());
        //RIP2.getStyleClass().add("dolphin-rip2");
        final Paint RIP2_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip2.setFill(RIP2_FILL);
        rip2.setStroke(null);

        rip1 = new Path();
        rip1.setFillRule(FillRule.EVEN_ODD);
        rip1.getElements().add(new MoveTo(0.36658354114713215 * WIDTH, 0.23346303501945526 * HEIGHT));
        rip1.getElements().add(new CubicCurveTo(0.341645885286783 * WIDTH, 0.21011673151750973 * HEIGHT,
                                                0.3541147132169576 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                0.34663341645885287 * WIDTH, 0.11284046692607004 * HEIGHT));
        rip1.getElements().add(new CubicCurveTo(0.341645885286783 * WIDTH, 0.20622568093385213 * HEIGHT,
                                                0.32418952618453867 * WIDTH, 0.26848249027237353 * HEIGHT,
                                                0.32169576059850374 * WIDTH, 0.2801556420233463 * HEIGHT));
        rip1.getElements().add(new CubicCurveTo(0.3192019950124688 * WIDTH, 0.28793774319066145 * HEIGHT,
                                                0.30673316708229426 * WIDTH, 0.32684824902723736 * HEIGHT,
                                                0.30423940149625933 * WIDTH, 0.33852140077821014 * HEIGHT));
        rip1.getElements().add(new CubicCurveTo(0.30174563591022446 * WIDTH, 0.3424124513618677 * HEIGHT,
                                                0.2942643391521197 * WIDTH, 0.35797665369649806 * HEIGHT,
                                                0.2817955112219451 * WIDTH, 0.3852140077821012 * HEIGHT));
        rip1.getElements().add(new CubicCurveTo(0.3266832917705736 * WIDTH, 0.33852140077821014 * HEIGHT,
                                                0.32169576059850374 * WIDTH, 0.2490272373540856 * HEIGHT,
                                                0.36658354114713215 * WIDTH, 0.23346303501945526 * HEIGHT));
        rip1.getElements().add(new ClosePath());
        //RIP1.getStyleClass().add("dolphin-rip1");
        final Paint RIP1_FILL = Color.color(0.8117647059, 0, 0.2274509804, 1);
        rip1.setFill(RIP1_FILL);
        rip1.setStroke(null);

        lateralline = new Path();
        lateralline.setFillRule(FillRule.EVEN_ODD);
        lateralline.getElements().add(new MoveTo(0.7182044887780549 * WIDTH, 0.4046692607003891 * HEIGHT));
        lateralline.getElements().add(new CubicCurveTo(0.6882793017456359 * WIDTH, 0.3540856031128405 * HEIGHT,
                                                       0.655860349127182 * WIDTH, 0.30739299610894943 * HEIGHT,
                                                       0.6134663341645885 * WIDTH, 0.26459143968871596 * HEIGHT));
        lateralline.getElements().add(new CubicCurveTo(0.571072319201995 * WIDTH, 0.22178988326848248 * HEIGHT,
                                                       0.516209476309227 * WIDTH, 0.17898832684824903 * HEIGHT,
                                                       0.4613466334164589 * WIDTH, 0.14785992217898833 * HEIGHT));
        lateralline.getElements().add(new CubicCurveTo(0.4114713216957606 * WIDTH, 0.12062256809338522 * HEIGHT,
                                                       0.3516209476309227 * WIDTH, 0.10116731517509728 * HEIGHT,
                                                       0.3092269326683292 * WIDTH, 0.09727626459143969 * HEIGHT));
        lateralline.getElements().add(new CubicCurveTo(0.3765586034912718 * WIDTH, 0.07782101167315175 * HEIGHT,
                                                       0.4538653366583541 * WIDTH, 0.11284046692607004 * HEIGHT,
                                                       0.5062344139650873 * WIDTH, 0.14785992217898833 * HEIGHT));
        lateralline.getElements().add(new CubicCurveTo(0.5536159600997507 * WIDTH, 0.17898832684824903 * HEIGHT,
                                                       0.5935162094763092 * WIDTH, 0.2140077821011673 * HEIGHT,
                                                       0.628428927680798 * WIDTH, 0.25680933852140075 * HEIGHT));
        lateralline.getElements().add(new CubicCurveTo(0.6658354114713217 * WIDTH, 0.29961089494163423 * HEIGHT,
                                                       0.6982543640897756 * WIDTH, 0.3540856031128405 * HEIGHT,
                                                       0.7182044887780549 * WIDTH, 0.4046692607003891 * HEIGHT));
        lateralline.getElements().add(new ClosePath());
        //LATERALLINE.getStyleClass().add("dolphin-lateralline");
        final Paint LATERALLINE_FILL = Color.color(0.4, 0.6, 1, 1);
        lateralline.setFill(LATERALLINE_FILL);
        lateralline.setStroke(null);

        eye = new Path();
        eye.setFillRule(FillRule.EVEN_ODD);
        eye.getElements().add(new MoveTo(0.2194513715710723 * WIDTH, 0.22178988326848248 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.2119700748129676 * WIDTH, 0.22178988326848248 * HEIGHT,
                                               0.20947630922693267 * WIDTH, 0.22957198443579765 * HEIGHT,
                                               0.20947630922693267 * WIDTH, 0.23346303501945526 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.20947630922693267 * WIDTH, 0.2529182879377432 * HEIGHT,
                                               0.22693266832917705 * WIDTH, 0.2607003891050584 * HEIGHT,
                                               0.23192019950124687 * WIDTH, 0.2607003891050584 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.23940149625935161 * WIDTH, 0.2607003891050584 * HEIGHT,
                                               0.2518703241895262 * WIDTH, 0.2529182879377432 * HEIGHT,
                                               0.2518703241895262 * WIDTH, 0.24124513618677043 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.2518703241895262 * WIDTH, 0.22957198443579765 * HEIGHT,
                                               0.24937655860349128 * WIDTH, 0.22957198443579765 * HEIGHT,
                                               0.23940149625935161 * WIDTH, 0.22178988326848248 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.23940149625935161 * WIDTH, 0.22178988326848248 * HEIGHT,
                                               0.2344139650872818 * WIDTH, 0.22178988326848248 * HEIGHT,
                                               0.2344139650872818 * WIDTH, 0.22178988326848248 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.2344139650872818 * WIDTH, 0.22568093385214008 * HEIGHT,
                                               0.2344139650872818 * WIDTH, 0.22568093385214008 * HEIGHT,
                                               0.2344139650872818 * WIDTH, 0.22568093385214008 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.2344139650872818 * WIDTH, 0.23346303501945526 * HEIGHT,
                                               0.23192019950124687 * WIDTH, 0.23346303501945526 * HEIGHT,
                                               0.22942643391521197 * WIDTH, 0.23346303501945526 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.22942643391521197 * WIDTH, 0.23346303501945526 * HEIGHT,
                                               0.22443890274314215 * WIDTH, 0.22957198443579765 * HEIGHT,
                                               0.22443890274314215 * WIDTH, 0.22178988326848248 * HEIGHT));
        eye.getElements().add(new CubicCurveTo(0.22443890274314215 * WIDTH, 0.22178988326848248 * HEIGHT,
                                               0.2194513715710723 * WIDTH, 0.22178988326848248 * HEIGHT,
                                               0.2194513715710723 * WIDTH, 0.22178988326848248 * HEIGHT));
        eye.getElements().add(new ClosePath());
        //EYE.getStyleClass().add("dolphin-eye");
        final Paint EYE_FILL = Color.BLACK;
        eye.setFill(EYE_FILL);
        eye.setStroke(null);

        sidefin1 = new Path();
        sidefin1.setFillRule(FillRule.EVEN_ODD);
        sidefin1.getElements().add(new MoveTo(0.30174563591022446 * WIDTH, 0.45136186770428016 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.2942643391521197 * WIDTH, 0.48638132295719844 * HEIGHT,
                                                    0.2967581047381546 * WIDTH, 0.5136186770428015 * HEIGHT,
                                                    0.3117206982543641 * WIDTH, 0.5408560311284046 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.3117206982543641 * WIDTH, 0.5447470817120622 * HEIGHT,
                                                    0.32418952618453867 * WIDTH, 0.5642023346303502 * HEIGHT,
                                                    0.3316708229426434 * WIDTH, 0.5719844357976653 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.3566084788029925 * WIDTH, 0.603112840466926 * HEIGHT,
                                                    0.38902743142144636 * WIDTH, 0.622568093385214 * HEIGHT,
                                                    0.4164588528678304 * WIDTH, 0.6303501945525292 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.42643391521197005 * WIDTH, 0.6342412451361867 * HEIGHT,
                                                    0.4488778054862843 * WIDTH, 0.642023346303502 * HEIGHT,
                                                    0.4713216957605985 * WIDTH, 0.642023346303502 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.47880299251870323 * WIDTH, 0.642023346303502 * HEIGHT,
                                                    0.4937655860349127 * WIDTH, 0.6381322957198443 * HEIGHT,
                                                    0.5012468827930174 * WIDTH, 0.622568093385214 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.4912718204488778 * WIDTH, 0.622568093385214 * HEIGHT,
                                                    0.4713216957605985 * WIDTH, 0.6070038910505836 * HEIGHT,
                                                    0.4613466334164589 * WIDTH, 0.603112840466926 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.4314214463840399 * WIDTH, 0.5875486381322957 * HEIGHT,
                                                    0.38902743142144636 * WIDTH, 0.556420233463035 * HEIGHT,
                                                    0.36159600997506236 * WIDTH, 0.5136186770428015 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.33915211970074816 * WIDTH, 0.4785992217898833 * HEIGHT,
                                                    0.33915211970074816 * WIDTH, 0.46303501945525294 * HEIGHT,
                                                    0.33665835411471323 * WIDTH, 0.45136186770428016 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.3341645885286783 * WIDTH, 0.44357976653696496 * HEIGHT,
                                                    0.33665835411471323 * WIDTH, 0.4396887159533074 * HEIGHT,
                                                    0.341645885286783 * WIDTH, 0.43190661478599224 * HEIGHT));
        sidefin1.getElements().add(new CubicCurveTo(0.32917705735660846 * WIDTH, 0.42023346303501946 * HEIGHT,
                                                    0.30673316708229426 * WIDTH, 0.43190661478599224 * HEIGHT,
                                                    0.30174563591022446 * WIDTH, 0.45136186770428016 * HEIGHT));
        sidefin1.getElements().add(new ClosePath());
        //SIDEFIN1.getStyleClass().add("dolphin-sidefin1");
        final Paint SIDEFIN1_FILL = Color.BLACK;
        sidefin1.setFill(SIDEFIN1_FILL);
        sidefin1.setStroke(null);

        sidefin2 = new Path();
        sidefin2.setFillRule(FillRule.EVEN_ODD);
        sidefin2.getElements().add(new MoveTo(0.4114713216957606 * WIDTH, 0.44357976653696496 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.4139650872817955 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                    0.4239401496259352 * WIDTH, 0.47470817120622566 * HEIGHT,
                                                    0.4239401496259352 * WIDTH, 0.4785992217898833 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.4389027431421446 * WIDTH, 0.5097276264591439 * HEIGHT,
                                                    0.45885286783042395 * WIDTH, 0.5252918287937743 * HEIGHT,
                                                    0.47381546134663344 * WIDTH, 0.5408560311284046 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.4837905236907731 * WIDTH, 0.5525291828793775 * HEIGHT,
                                                    0.49625935162094764 * WIDTH, 0.5719844357976653 * HEIGHT,
                                                    0.5037406483790524 * WIDTH, 0.5914396887159533 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.5112219451371571 * WIDTH, 0.6070038910505836 * HEIGHT,
                                                    0.513715710723192 * WIDTH, 0.622568093385214 * HEIGHT,
                                                    0.5187032418952618 * WIDTH, 0.6381322957198443 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.5261845386533666 * WIDTH, 0.6342412451361867 * HEIGHT,
                                                    0.5261845386533666 * WIDTH, 0.6303501945525292 * HEIGHT,
                                                    0.5261845386533666 * WIDTH, 0.622568093385214 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.5261845386533666 * WIDTH, 0.6186770428015564 * HEIGHT,
                                                    0.5211970074812967 * WIDTH, 0.5914396887159533 * HEIGHT,
                                                    0.5187032418952618 * WIDTH, 0.5914396887159533 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.5012468827930174 * WIDTH, 0.5408560311284046 * HEIGHT,
                                                    0.4837905236907731 * WIDTH, 0.5214007782101168 * HEIGHT,
                                                    0.46633416458852867 * WIDTH, 0.5019455252918288 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.4488778054862843 * WIDTH, 0.48249027237354086 * HEIGHT,
                                                    0.4339152119700748 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                    0.42643391521197005 * WIDTH, 0.44357976653696496 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.41895261845386533 * WIDTH, 0.42023346303501946 * HEIGHT,
                                                    0.4139650872817955 * WIDTH, 0.4085603112840467 * HEIGHT,
                                                    0.4114713216957606 * WIDTH, 0.4085603112840467 * HEIGHT));
        sidefin2.getElements().add(new CubicCurveTo(0.40648379052369077 * WIDTH, 0.4163424124513619 * HEIGHT,
                                                    0.4089775561097257 * WIDTH, 0.4280155642023346 * HEIGHT,
                                                    0.4114713216957606 * WIDTH, 0.44357976653696496 * HEIGHT));
        sidefin2.getElements().add(new ClosePath());
        //SIDEFIN2.getStyleClass().add("dolphin-sidefin2");
        final Paint SIDEFIN2_FILL = Color.BLACK;
        sidefin2.setFill(SIDEFIN2_FILL);
        sidefin2.setStroke(null);

        belly = new Path();
        belly.setFillRule(FillRule.EVEN_ODD);
        belly.getElements().add(new MoveTo(0.48129675810473815 * WIDTH, 0.4708171206225681 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.48129675810473815 * WIDTH, 0.48249027237354086 * HEIGHT,
                                                 0.4837905236907731 * WIDTH, 0.490272373540856 * HEIGHT,
                                                 0.49875311720698257 * WIDTH, 0.5097276264591439 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.5037406483790524 * WIDTH, 0.5175097276264592 * HEIGHT,
                                                 0.5236907730673317 * WIDTH, 0.5330739299610895 * HEIGHT,
                                                 0.5311720698254364 * WIDTH, 0.5369649805447471 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.5361596009975063 * WIDTH, 0.5408560311284046 * HEIGHT,
                                                 0.5910224438902744 * WIDTH, 0.5642023346303502 * HEIGHT,
                                                 0.628428927680798 * WIDTH, 0.5914396887159533 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.6533665835411472 * WIDTH, 0.6108949416342413 * HEIGHT,
                                                 0.683291770573566 * WIDTH, 0.642023346303502 * HEIGHT,
                                                 0.7032418952618454 * WIDTH, 0.6731517509727627 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.7007481296758105 * WIDTH, 0.6342412451361867 * HEIGHT,
                                                 0.6907730673316709 * WIDTH, 0.6147859922178989 * HEIGHT,
                                                 0.6733167082294265 * WIDTH, 0.5875486381322957 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.6608478802992519 * WIDTH, 0.5680933852140078 * HEIGHT,
                                                 0.6384039900249376 * WIDTH, 0.5330739299610895 * HEIGHT,
                                                 0.6059850374064838 * WIDTH, 0.5097276264591439 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.5910224438902744 * WIDTH, 0.4980544747081712 * HEIGHT,
                                                 0.5511221945137157 * WIDTH, 0.4708171206225681 * HEIGHT,
                                                 0.5336658354114713 * WIDTH, 0.46303501945525294 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.5236907730673317 * WIDTH, 0.4591439688715953 * HEIGHT,
                                                 0.5087281795511222 * WIDTH, 0.45525291828793774 * HEIGHT,
                                                 0.49875311720698257 * WIDTH, 0.45525291828793774 * HEIGHT));
        belly.getElements().add(new CubicCurveTo(0.4937655860349127 * WIDTH, 0.45525291828793774 * HEIGHT,
                                                 0.48129675810473815 * WIDTH, 0.46303501945525294 * HEIGHT,
                                                 0.48129675810473815 * WIDTH, 0.4708171206225681 * HEIGHT));
        belly.getElements().add(new ClosePath());
        //BELLY.getStyleClass().add("dolphin-belly");
        final Paint BELLY_FILL = Color.BLACK;
        belly.setFill(BELLY_FILL);
        belly.setStroke(null);


        continueDolphin1(null, WIDTH, HEIGHT);

    }

    private void continueDolphin1(Group dolphin, final double WIDTH, final double HEIGHT) {
        backfin2 = new Path();
        backfin2.setFillRule(FillRule.EVEN_ODD);
        backfin2.getElements().add(new MoveTo(0.6982543640897756 * WIDTH, 0.07392996108949416 * HEIGHT));
        backfin2.getElements().add(new CubicCurveTo(0.6907730673316709 * WIDTH, 0.08171206225680934 * HEIGHT,
                                                    0.6458852867830424 * WIDTH, 0.10116731517509728 * HEIGHT,
                                                    0.6408977556109726 * WIDTH, 0.14785992217898833 * HEIGHT));
        backfin2.getElements().add(new CubicCurveTo(0.6408977556109726 * WIDTH, 0.1517509727626459 * HEIGHT,
                                                    0.6458852867830424 * WIDTH, 0.15953307392996108 * HEIGHT,
                                                    0.6483790523690773 * WIDTH, 0.16342412451361868 * HEIGHT));
        backfin2.getElements().add(new CubicCurveTo(0.6483790523690773 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                    0.6533665835411472 * WIDTH, 0.16342412451361868 * HEIGHT,
                                                    0.6533665835411472 * WIDTH, 0.16342412451361868 * HEIGHT));
        backfin2.getElements().add(new CubicCurveTo(0.6583541147132169 * WIDTH, 0.1245136186770428 * HEIGHT,
                                                    0.6882793017456359 * WIDTH, 0.09727626459143969 * HEIGHT,
                                                    0.7057356608478803 * WIDTH, 0.07392996108949416 * HEIGHT));
        backfin2.getElements().add(new CubicCurveTo(0.7057356608478803 * WIDTH, 0.07392996108949416 * HEIGHT,
                                                    0.6982543640897756 * WIDTH, 0.07392996108949416 * HEIGHT,
                                                    0.6982543640897756 * WIDTH, 0.07392996108949416 * HEIGHT));
        backfin2.getElements().add(new ClosePath());
        //BACKFIN2.getStyleClass().add("dolphin-backfin2");
        final Paint BACKFIN2_FILL = Color.BLACK;
        backfin2.setFill(BACKFIN2_FILL);
        backfin2.setStroke(null);

        backfin1 = new Path();
        backfin1.setFillRule(FillRule.EVEN_ODD);
        backfin1.getElements().add(new MoveTo(0.5286783042394015 * WIDTH, 0.019455252918287938 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.5261845386533666 * WIDTH, 0.023346303501945526 * HEIGHT,
                                                    0.5112219451371571 * WIDTH, 0.03501945525291829 * HEIGHT,
                                                    0.5112219451371571 * WIDTH, 0.04669260700389105 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.5112219451371571 * WIDTH, 0.054474708171206226 * HEIGHT,
                                                    0.5286783042394015 * WIDTH, 0.0622568093385214 * HEIGHT,
                                                    0.5311720698254364 * WIDTH, 0.0622568093385214 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.5336658354114713 * WIDTH, 0.0622568093385214 * HEIGHT,
                                                    0.5411471321695761 * WIDTH, 0.058365758754863814 * HEIGHT,
                                                    0.543640897755611 * WIDTH, 0.054474708171206226 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.5660847880299252 * WIDTH, 0.027237354085603113 * HEIGHT,
                                                    0.6059850374064838 * WIDTH, 0.0311284046692607 * HEIGHT,
                                                    0.628428927680798 * WIDTH, 0.0311284046692607 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.6658354114713217 * WIDTH, 0.03501945525291829 * HEIGHT,
                                                    0.71571072319202 * WIDTH, 0.05058365758754864 * HEIGHT,
                                                    0.7182044887780549 * WIDTH, 0.04669260700389105 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.7107231920199502 * WIDTH, 0.042801556420233464 * HEIGHT,
                                                    0.6708229426433915 * WIDTH, 0.019455252918287938 * HEIGHT,
                                                    0.6658354114713217 * WIDTH, 0.019455252918287938 * HEIGHT));
        backfin1.getElements().add(new CubicCurveTo(0.655860349127182 * WIDTH, 0.01556420233463035 * HEIGHT,
                                                    0.6209476309226932 * WIDTH, 0.0,
                                                    0.5985037406483791 * WIDTH, 0.0));
        backfin1.getElements().add(new CubicCurveTo(0.5910224438902744 * WIDTH, 0.0,
                                                    0.5561097256857855 * WIDTH, 0.0,
                                                    0.5286783042394015 * WIDTH, 0.019455252918287938 * HEIGHT));
        backfin1.getElements().add(new ClosePath());
        //BACKFIN1.getStyleClass().add("dolphin-backfin1");
        final Paint BACKFIN1_FILL = Color.BLACK;
        backfin1.setFill(BACKFIN1_FILL);
        backfin1.setStroke(null);

        tailfinstart = new Path();
        tailfinstart.setFillRule(FillRule.EVEN_ODD);
        tailfinstart.getElements().add(new MoveTo(0.770573566084788 * WIDTH, 0.7237354085603113 * HEIGHT));
        tailfinstart.getElements().add(new CubicCurveTo(0.7655860349127181 * WIDTH, 0.7276264591439688 * HEIGHT,
                                                        0.7406483790523691 * WIDTH, 0.7237354085603113 * HEIGHT,
                                                        0.7256857855361596 * WIDTH, 0.7003891050583657 * HEIGHT));
        tailfinstart.getElements().add(new CubicCurveTo(0.7406483790523691 * WIDTH, 0.7042801556420234 * HEIGHT,
                                                        0.7581047381546134 * WIDTH, 0.7042801556420234 * HEIGHT,
                                                        0.7630922693266833 * WIDTH, 0.7003891050583657 * HEIGHT));
        tailfinstart.getElements().add(new CubicCurveTo(0.773067331670823 * WIDTH, 0.6926070038910506 * HEIGHT,
                                                        0.7780548628428927 * WIDTH, 0.6848249027237354 * HEIGHT,
                                                        0.7905236907730673 * WIDTH, 0.6731517509727627 * HEIGHT));
        tailfinstart.getElements().add(new CubicCurveTo(0.7930174563591023 * WIDTH, 0.7003891050583657 * HEIGHT,
                                                        0.7805486284289277 * WIDTH, 0.7198443579766537 * HEIGHT,
                                                        0.770573566084788 * WIDTH, 0.7237354085603113 * HEIGHT));
        tailfinstart.getElements().add(new ClosePath());
        //TAILFINSTART.getStyleClass().add("dolphin-tailfinstart");
        final Paint TAILFINSTART_FILL = Color.color(0.4, 0.6, 1, 1);
        tailfinstart.setFill(TAILFINSTART_FILL);
        tailfinstart.setStroke(null);

        tailfin1 = new Path();
        tailfin1.setFillRule(FillRule.EVEN_ODD);
        tailfin1.getElements().add(new MoveTo(0.7281795511221946 * WIDTH, 0.77431906614786 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7281795511221946 * WIDTH, 0.77431906614786 * HEIGHT,
                                                    0.7331670822942643 * WIDTH, 0.8249027237354085 * HEIGHT,
                                                    0.7331670822942643 * WIDTH, 0.8287937743190662 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7331670822942643 * WIDTH, 0.8326848249027238 * HEIGHT,
                                                    0.7331670822942643 * WIDTH, 0.8482490272373541 * HEIGHT,
                                                    0.7331670822942643 * WIDTH, 0.8560311284046692 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7331670822942643 * WIDTH, 0.8599221789883269 * HEIGHT,
                                                    0.7306733167082294 * WIDTH, 0.8793774319066148 * HEIGHT,
                                                    0.7306733167082294 * WIDTH, 0.8910505836575876 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7206982543640897 * WIDTH, 0.9649805447470817 * HEIGHT,
                                                    0.6982543640897756 * WIDTH, 0.9844357976653697 * HEIGHT,
                                                    0.7007481296758105 * WIDTH, HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7032418952618454 * WIDTH, HEIGHT,
                                                    0.7107231920199502 * WIDTH, 0.9961089494163424 * HEIGHT,
                                                    0.713216957605985 * WIDTH, 0.9883268482490273 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.71571072319202 * WIDTH, 0.9883268482490273 * HEIGHT,
                                                    0.7306733167082294 * WIDTH, 0.9455252918287937 * HEIGHT,
                                                    0.7331670822942643 * WIDTH, 0.9455252918287937 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7381546134663342 * WIDTH, 0.9260700389105059 * HEIGHT,
                                                    0.743142144638404 * WIDTH, 0.9027237354085603 * HEIGHT,
                                                    0.7481296758104738 * WIDTH, 0.8832684824902723 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7481296758104738 * WIDTH, 0.8754863813229572 * HEIGHT,
                                                    0.7506234413965087 * WIDTH, 0.8638132295719845 * HEIGHT,
                                                    0.7506234413965087 * WIDTH, 0.8521400778210116 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7506234413965087 * WIDTH, 0.8482490272373541 * HEIGHT,
                                                    0.7481296758104738 * WIDTH, 0.8171206225680934 * HEIGHT,
                                                    0.7506234413965087 * WIDTH, 0.8132295719844358 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7506234413965087 * WIDTH, 0.8132295719844358 * HEIGHT,
                                                    0.7481296758104738 * WIDTH, 0.8132295719844358 * HEIGHT,
                                                    0.7481296758104738 * WIDTH, 0.8093385214007782 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7481296758104738 * WIDTH, 0.8054474708171206 * HEIGHT,
                                                    0.7456359102244389 * WIDTH, 0.7587548638132295 * HEIGHT,
                                                    0.7331670822942643 * WIDTH, 0.7587548638132295 * HEIGHT));
        tailfin1.getElements().add(new CubicCurveTo(0.7306733167082294 * WIDTH, 0.7587548638132295 * HEIGHT,
                                                    0.7281795511221946 * WIDTH, 0.7665369649805448 * HEIGHT,
                                                    0.7281795511221946 * WIDTH, 0.77431906614786 * HEIGHT));
        tailfin1.getElements().add(new ClosePath());
        //TAILFIN1.getStyleClass().add("dolphin-tailfin1");
        final Paint TAILFIN1_FILL = Color.BLACK;
        tailfin1.setFill(TAILFIN1_FILL);
        tailfin1.setStroke(null);

        tailfin2 = new Path();
        tailfin2.setFillRule(FillRule.EVEN_ODD);
        tailfin2.getElements().add(new MoveTo(0.8179551122194514 * WIDTH, 0.6964980544747081 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.8154613466334164 * WIDTH, 0.7003891050583657 * HEIGHT,
                                                    0.8079800498753117 * WIDTH, 0.708171206225681 * HEIGHT,
                                                    0.8079800498753117 * WIDTH, 0.7120622568093385 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.8079800498753117 * WIDTH, 0.7120622568093385 * HEIGHT,
                                                    0.8129675810473815 * WIDTH, 0.7120622568093385 * HEIGHT,
                                                    0.8129675810473815 * WIDTH, 0.7120622568093385 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.8179551122194514 * WIDTH, 0.7120622568093385 * HEIGHT,
                                                    0.8528678304239401 * WIDTH, 0.7198443579766537 * HEIGHT,
                                                    0.8703241895261845 * WIDTH, 0.7276264591439688 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.885286783042394 * WIDTH, 0.7315175097276264 * HEIGHT,
                                                    0.8977556109725686 * WIDTH, 0.7354085603112841 * HEIGHT,
                                                    0.9077306733167082 * WIDTH, 0.7431906614785992 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.912718204488778 * WIDTH, 0.7470817120622568 * HEIGHT,
                                                    0.9476309226932669 * WIDTH, 0.7665369649805448 * HEIGHT,
                                                    0.9650872817955112 * WIDTH, 0.7859922178988327 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.9675810473815462 * WIDTH, 0.7898832684824902 * HEIGHT,
                                                    0.9376558603491272 * WIDTH, 0.77431906614786 * HEIGHT,
                                                    0.9226932668329177 * WIDTH, 0.77431906614786 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.8927680798004988 * WIDTH, 0.77431906614786 * HEIGHT,
                                                    0.8653366583541147 * WIDTH, 0.77431906614786 * HEIGHT,
                                                    0.827930174563591 * WIDTH, 0.8015564202334631 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.7930174563591023 * WIDTH, 0.8404669260700389 * HEIGHT,
                                                    0.7830423940149626 * WIDTH, 0.867704280155642 * HEIGHT,
                                                    0.7780548628428927 * WIDTH, 0.8793774319066148 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.7680798004987531 * WIDTH, 0.9027237354085603 * HEIGHT,
                                                    0.7531172069825436 * WIDTH, 0.9571984435797666 * HEIGHT,
                                                    0.7531172069825436 * WIDTH, 0.9610894941634242 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.7531172069825436 * WIDTH, 0.9649805447470817 * HEIGHT,
                                                    0.7506234413965087 * WIDTH, 0.9766536964980544 * HEIGHT,
                                                    0.7556109725685786 * WIDTH, 0.980544747081712 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.7581047381546134 * WIDTH, 0.9844357976653697 * HEIGHT,
                                                    0.7605985037406484 * WIDTH, 0.9766536964980544 * HEIGHT,
                                                    0.7630922693266833 * WIDTH, 0.9766536964980544 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.7655860349127181 * WIDTH, 0.9688715953307393 * HEIGHT,
                                                    0.7680798004987531 * WIDTH, 0.9610894941634242 * HEIGHT,
                                                    0.770573566084788 * WIDTH, 0.953307392996109 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.7980049875311721 * WIDTH, 0.8715953307392996 * HEIGHT,
                                                    0.8254364089775561 * WIDTH, 0.8365758754863813 * HEIGHT,
                                                    0.8678304239401496 * WIDTH, 0.8132295719844358 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.885286783042394 * WIDTH, 0.8054474708171206 * HEIGHT,
                                                    0.9077306733167082 * WIDTH, 0.8015564202334631 * HEIGHT,
                                                    0.9476309226932669 * WIDTH, 0.8054474708171206 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.9476309226932669 * WIDTH, 0.8054474708171206 * HEIGHT,
                                                    WIDTH, 0.8249027237354085 * HEIGHT,
                                                    WIDTH, 0.8210116731517509 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(WIDTH, 0.8132295719844358 * HEIGHT,
                                                    0.9825436408977556 * WIDTH, 0.7821011673151751 * HEIGHT,
                                                    0.9600997506234414 * WIDTH, 0.7587548638132295 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.9276807980049875 * WIDTH, 0.7237354085603113 * HEIGHT,
                                                    0.8703241895261845 * WIDTH, 0.6964980544747081 * HEIGHT,
                                                    0.8354114713216958 * WIDTH, 0.6964980544747081 * HEIGHT));
        tailfin2.getElements().add(new CubicCurveTo(0.8329177057356608 * WIDTH, 0.6964980544747081 * HEIGHT,
                                                    0.8229426433915212 * WIDTH, 0.6964980544747081 * HEIGHT,
                                                    0.8179551122194514 * WIDTH, 0.6964980544747081 * HEIGHT));
        tailfin2.getElements().add(new ClosePath());
        //TAILFIN2.getStyleClass().add("dolphin-tailfin2");
        final Paint TAILFIN2_FILL = Color.BLACK;
        tailfin2.setFill(TAILFIN2_FILL);
        tailfin2.setStroke(null);

    }

}

