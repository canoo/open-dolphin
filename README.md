<!--- the build is currently failing because of jcenter authorization issues
[![Build Status](https://travis-ci.org/canoo/open-dolphin.png?branch=master)](https://travis-ci.org/canoo/open-dolphin)
-->
The Dolphin Project
===================

Website: http://open-dolphin.org
see also the user guide, which is linked from the website.

Team: Dieter Holz, Andres Almiray, Dierk Koenig

Mailinglist: dolphin@lists.canoo.com, please subscribe at http://lists.canoo.com/mailman/listinfo/dolphin .

JIRA: http://www.canoo.com/jira/browse/DOL

Twitter: @OpenDolphin

Prerequisites
-------------
If you use Java 7u6 or a later update, you are all set.
Building with Java 8 is currently not supported.
Otherwise, JAVAFX_HOME must be set to a 2.1.0 version. (Version 2.2 also works fine.)

Running an initial "Push" demo
-----------------------
> gradlew PushDemo

To see a choice of demos use:
> gradlew listDemos

How to build
------------
> gradlew clean build

Server integration
------------------
Running the push demo with the grails server integration
- gradlew build
- cd dolphin-grails
- grails run-app &
- cd ..
- gradlew GrailsClientPushDemo

IDE integration
---------------
You can create an IDEA project for the full dolphin code by running
- gradlew idea

or for eclipse via
- gradlew eclipse

Purpose
-------
Bridging the world of Java Enterprise and Java Desktop.
- visualization code resides on the client (View)
- domain models and controllers remain on the server
- presentation models are shared between client and server
- a server may "push" commands to the client by means of a long poll

A video introduction is at http://people.canoo.com/mittie/dolphin.mov.
The API has meanwhile improved and the privacy disclaimer is no longer applicable.

Dolphin introduction in the 2012 JavaOne strategy keynote (around minute 18):
http://medianetwork.oracle.com/video/player/1871687106001

Dolphin technical session at JavaOne 2012 by Arvinder Brar (Navis), Jasper Potts (Oracle) and Dierk Koenig (Canoo):
https://oracleus.activeevents.com/connect/sessionDetail.ww?SESSION_ID=4853 (slides and screencast capture)

Dolphin technical session at W-JAX 2012, Munich, Nov 6th:
http://entwickler.com/konferenzen/ext_scripts/v2/php/sessions-popup.php?module=wjax2012&id=24020 (German)

Project layout
--------------
The multi-project build consists of these subprojects

- shared (code that is needed on both client and server)
- client (visualization)
- server (domain model and control logic)
- combined (for testing the client-server combination)
- demo-javafx   (again divided in client - server - shared - combined)
                                                        
Demos
-----
A good place to start are the demos.
See https://github.com/canoo/open-dolphin/tree/master/subprojects/demo-javafx
where "combined" contains starter and configuration. From there have a look
into the referenced client and server classes.

General approach
----------------
Presentation models (PMs) are used on both, client and server.
Presentation models consist of Attributes that capture only simple data types and meta information.

On the client side, the visualization code (e.g JavaFX) binds
against the Presentation model, i.e. its Attributes.

Commands are sent between client and server. They are 
concerned with create, read, update, and delete actions
for PMs. 

PresentationModels are created via
>    dolphin.presentationModel 'myPmId', name:'Dolphin'

Presentation models may synchronize for example to capture
the selection in a master-detail scenario. This is done by "applying"
the sourcePM to the targetPm like so
>    dolphin.apply sourcePm to targetPm
    
There are both Java-friendly and Groovy-friendly API methods in the "Dolphin" facade.

Dirty state
-----------
Attributes have a dirty flag that can be used to detect when the attribute
contains a value different that its original one. This flag is observable trough
standard POJO bindings (i.e, PropertyChangeListener).

PresentationModels also have a dirty flag which is dependent on the dirty status
of any of the attributes the model holds. This flag too is observable trough
standard POJO bindings (i.e, PropertyChangeListener).

Thread Safety
-------------
Dolphin makes the assumption that changes to PresentationModel and Attributes will
occur inside the UI thread when in the client side of the application. Changes on
the server must occur in the same thread without enforcing one in particular.

Programming mode
----------------
The client side runs under an JavaSE event model, whereas the server side follows
a JavaEE request-response model. An important part of dolphin is to bridge these
two worlds.

The communication between these worlds happens solely through command objects
that are sent between them.

ClientModelStore (CMS) and ServerModelStore (SMS) are automatically kept in sync.
Changes to the CMS automatically trigger a respective command being sent to the
server, which in turn updates the SMS accordingly.

The server does never change the SMS on its own. Instead, the server may send
commands to the client, which make the client update the CMS (which in turn automatically
syncs with the server as mentioned above).
The reason for this approach is the communication delay between client and server.
We need to make sure that one side is the master and always ahead of the other
side and we have chosen the client to be that master.

Design decisions
----------------
- Any PresentationModel id must be unique inside the ModelStore.
- Any Attribute id must be unique inside the ModelStore.
- All commands from client to server are sent asynchronously.
- Commands are always sent in strict sequence such that we can rely on all
  value changes being synced to the SMS before a command is processed on the
  server that depends on these values.
