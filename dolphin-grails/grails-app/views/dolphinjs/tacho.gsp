<%--
  A simple page that works together with dolphin.js for showcasing the use of OpenDolphin in a
  single-page javascript application.
  author: dierk.koenig
--%>

<%@ page import="org.opendolphin.demo.TutorialAction" contentType="text/html;charset=UTF-8" %>

<%
  dolphinUrl      = createLink(controller: 'dolphin', absolute: true) - 'index'

  // page-wide constants to share as HTML ids and JS lookups
  gauge           = "gauge"
  startButton     = "startButton"
  pmId            = "Train"
  showCode        = "showCode"
  dolphinCode     = "dolphinCode"
  range           = "range"
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta name="layout" content="demo">
  <title>Dolphin.js Simple Page</title>
</head>

<body>
<div class="container">
  <div class="row" style="margin-top: 4em;">
    <div class="offset2 span4">
      <h1>Dolphin.js</h1>
      <p class="lead">Cross-channel instant updates</p>
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
      <legend>Step 2 - Instant updates across channels</legend>
      <span class="help-block">
        Change the velocity in a JavaFX view, on a mobile client with device orientation, or <br>
        through <a href="velocity">the slider in a second browser</a> and see it updated instantly<br>
        across various channels like in this web page on your desktop or mobile device.
      </span>

      <canvas height="200" width="200" id="${gauge}"></canvas>

      <button id="${startButton}" class="btn btn-primary">Start</button>

    </fieldset>
    <hr>
    <a href="https://github.com/canoo/open-dolphin/blob/master/dolphin-grails/grails-app/views/dolphinjs/tacho.gsp">source code</a>
    </div>
  </div>
</div>

<script src="${resource(dir: 'libs', file: 'steelseries-min.js')}"></script>
<script src="${resource(dir: 'libs', file: 'tween-min.js')}"></script>
<script>
    document.gauge = new steelseries.Radial('gauge', {
        pointerType: steelseries.PointerType.TYPE4,
        titleString: "km/h",
        threshold: 50,
        lcdVisible: true
    });
</script>

<script>
    var dolphin = opendolphin.makeDolphin()
        .url("${dolphinUrl}")
        .slackMS(50) // almost no slack for read-only views
        .strictMode(false)
        .build();

    dolphin.reset({
        onSuccess: function() {

        var speedAttr = dolphin.attribute("speed", "train.speed", 0); // todo dk: put in constants
        dolphin.presentationModel("${pmId}", undefined, speedAttr);

        // bind speed of pm to value of gauge
        speedAttr.onValueChange(function (event) {
              document.gauge.setValue(Number(event.newValue));
        });

        // on start button pressed, start the continuous update
        var startButton = document.getElementById("${startButton}")
        startButton.addEventListener("click", function () {
            function longPoll() {
                dolphin.send("poll.train.speed", {onFinished: longPoll}); // todo dk: put command name in constants
            }
            longPoll();
        });

        } // onSuccess
    });
</script>


</body>
</html>