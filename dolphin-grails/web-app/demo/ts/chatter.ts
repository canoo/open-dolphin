/// <reference path="../../js/dolphin/OpenDolphin.ts"/>
/// <reference path="../../js/dolphin/ClientDolphin.ts"/>
/// <reference path="../../js/dolphin/Tag.ts"/>

// html elements
var nameElement     = <HTMLInputElement>    document.getElementById('name');
var postings        = <HTMLTableElement>    document.getElementById('postings');
var message         = <HTMLTextAreaElement> document.getElementById('message');
var postMessageText = <HTMLButtonElement>   document.getElementById('post-message');

// dolphin setup
var SERVER_URL      = window.location.protocol + "//" + window.location.host + "/dolphin-grails/dolphin/";
var dolphin         = <opendolphin.ClientDolphin> opendolphin.makeDolphin()
    .url(SERVER_URL)
    .slackMS(0)
    .strictMode(false)
    .build();

dolphin.reset({
    onSuccess: function() {

// main entry pm
var nameAtt         = dolphin.attribute("name",     null, '',  'VALUE');
var messageAtt      = dolphin.attribute("message",  null, '',  'VALUE');
var dateAtt         = dolphin.attribute("date",     null, '',  'VALUE');
var myChat          = dolphin.presentationModel("chatter.input", null, nameAtt, messageAtt, dateAtt);

// bind input form bidirectionally
nameElement.oninput = (event) => { nameAtt.setValue(nameElement.value);  };
message.oninput     = (event) => { messageAtt.setValue(message.value);  };

nameAtt.onValueChange(   (event) => nameElement.value = event.newValue);
messageAtt.onValueChange((event) => message.value     = event.newValue);

// bind collection of posts
function onPostAdded(pm) {
    var nameDt = document.createElement("dt");
    var msgDd  = document.createElement("dd");
    nameDt.id  = pm.getAt("name").getQualifier();
    msgDd.id   = pm.getAt("message").getQualifier();
    postings.appendChild(nameDt);
    postings.appendChild(msgDd);
    pm.getAt("name").onValueChange((evt)    => nameDt.innerHTML = evt.newValue + "<p class='date'>" + pm.getAt("date").getValue() + "</p>");
    pm.getAt("message").onValueChange((evt) => msgDd.innerHTML  = "<pre>"+evt.newValue+"</pre>");

    var postUserId = pm.getAt("name").getQualifier().split("-")[0];
    msgDd.onclick = (evt) => {
        var userId = myChat.getAt("name").getQualifier().split("-")[0];
        if (userId == postUserId) {  // our post, we can select
            myChat.syncWith(pm);
        }
        message.focus();
    }
}
function onPostRemoved(pm) {
    var holder = document.getElementById(pm.getAt("name").getQualifier());
    postings.removeChild(holder);
    holder = document.getElementById(pm.getAt("message").getQualifier());
    postings.removeChild(holder);
}

dolphin.getClientModelStore().onModelStoreChange((event) => {
    if (event.clientPresentationModel.presentationModelType != "chatter.type.post") return;
    if (event.eventType == opendolphin.Type.ADDED) {
        onPostAdded(event.clientPresentationModel);
    }
    if (event.eventType == opendolphin.Type.REMOVED) {
        onPostRemoved(event.clientPresentationModel);
    }
});

// handle the button click
postMessageText.onclick = (event) => {
    postMessageText.disabled = true; // double-click protection
    dolphin.send('chatter.post', { onFinished : () => postMessageText.disabled = false, onFinishedData : null });
    message.focus();
};

dolphin.send("chatter.init", {
    onFinished     : () => {
        console.log("starting the push listening");
        dolphin.startPushListening("chatter.on.push", "chatter.release");
    },
    onFinishedData : null
});
    } // onSuccess
});
