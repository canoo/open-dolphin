/// <reference path="ClientPresentationModel.ts" />
/// <reference path="Command.ts" />
/// <reference path="CommandBatcher.ts" />
/// <reference path="Codec.ts" />
/// <reference path="CallNamedActionCommand.ts" />
/// <reference path="ClientDolphin.ts" />
/// <reference path="AttributeMetadataChangedCommand.ts" />
/// <reference path="ClientAttribute.ts" />
/// <reference path="PresentationModelResetedCommand.ts" />
/// <reference path="SavedPresentationModelNotification.ts" />
/// <reference path="InitializeAttributeCommand.ts" />
/// <reference path="SwitchPresentationModelCommand.ts" />
/// <reference path="ValueChangedCommand.ts" />
/// <reference path="DeleteAllPresentationModelsOfTypeCommand.ts" />
/// <reference path="DeleteAllPresentationModelsOfTypeCommand.ts" />
/// <reference path="DeletePresentationModelCommand.ts" />
/// <reference path="CreatePresentationModelCommand.ts" />
/// <reference path="DataCommand.ts" />
/// <reference path="NamedCommand.ts" />
/// <reference path="SignalCommand.ts" />
/// <reference path="Tag.ts" />

module opendolphin {

    export interface OnSuccessHandler {
        onSuccess():void
    }

    export interface OnFinishedHandler {
        onFinished(models: ClientPresentationModel[]):void
        onFinishedData(listOfData:any[]):void
    }

    export interface CommandAndHandler {
        command :  Command;
        handler : OnFinishedHandler;
    }

    export interface Transmitter {
        transmit(commands: Command[], onDone:(result: Command[]) => void) : void ;
        signal(command: SignalCommand) : void;
        reset(successHandler:OnSuccessHandler): void;
    }

    export class ClientConnector {

        private commandQueue :      CommandAndHandler[] = [];
        private currentlySending :  boolean = false;
        private slackMS:            number; // slack milliseconds for rendering and batching
        private strictMode:         boolean = true;
        private skipCommands:       boolean = false;
        private transmitter :       Transmitter;
        private codec :             Codec;
        private clientDolphin :     ClientDolphin;
        private commandBatcher:     CommandBatcher;

        /////// push support state  ///////
        private pushListener:       NamedCommand;
        private releaseCommand:     SignalCommand;
        private pushEnabled:        boolean = false;
        private waiting:            boolean = false;


        constructor(
                transmitter:   Transmitter,
                clientDolphin: ClientDolphin,
                strictMode:    boolean = true,
                slackMS:       number = 0,
                maxBatchSize : number = 50
            ) {
            this.transmitter    = transmitter;
            this.clientDolphin  = clientDolphin;
            this.strictMode     = strictMode;
            this.slackMS        = slackMS;
            this.codec          = new  Codec();
            this.commandBatcher = new BlindCommandBatcher(true, maxBatchSize);
        }

        setCommandBatcher(newBatcher:  CommandBatcher) {
            this.commandBatcher = newBatcher;
        }
        setPushEnabled(enabled:boolean) {
            this.pushEnabled = enabled;
        }
        setPushListener(newListener:  NamedCommand) {
            this.pushListener = newListener
        }
        setReleaseCommand(newCommand:  SignalCommand) {
            this.releaseCommand = newCommand
        }

        reset(successHandler:OnSuccessHandler) {
            this.transmitter.reset(successHandler);
        }

        send(command: Command, onFinished:OnFinishedHandler) {
            if (this.skipCommands) {
                // console.log("--- lenient mode. Skipped command ", command) // uncomment for debugging
                return;
            }
            this.commandQueue.push({command: command, handler: onFinished });
            if (this.currentlySending) {
                if(command != this.pushListener) this.release(); // there is not point in releasing if we do not send atm
                return;
            }
            this.doSendNext();
        }

        private doSendNext() {
            if (this.commandQueue.length < 1) {
                this.currentlySending = false;
                return;
            }
            this.currentlySending = true;

            var cmdsAndHandlers = this.commandBatcher.batch(this.commandQueue);
            var callback = cmdsAndHandlers[cmdsAndHandlers.length-1].handler;
            var commands = cmdsAndHandlers.map( cah => { return cah.command });
            this.transmitter.transmit(commands, (response: Command[]) => {

                //console.log("server response: [" + response.map(it => it.id).join(", ") + "] ");

                var touchedPMs :  ClientPresentationModel[] = []
                response.forEach((command: Command) => {
                    if (this.strictMode == false) {
                        this.skipCommands = true;
                    }
                    var touched = null;
                    try {
                        touched = this.handle(command);
                    } catch (exception) {
                        console.error("error while handling command", command, exception)
                    }
                    this.skipCommands = false;
                    if (touched) touchedPMs.push(touched);
                });

                if (callback) {
                    callback.onFinished(touchedPMs); // todo: make them unique?
                    // todo dk: handling of data from datacommand
                }

                // recursive call: fetch the next in line but allow a bit of slack such that
                // document events can fire, rendering is done and commands can batch up
                setTimeout( () => this.doSendNext() , this.slackMS );
            });
        }



        handle(command: Command):  ClientPresentationModel{
            if(command.id == "Data"){
                return this.handleDataCommand(< DataCommand>command);
            }else if(command.id == "DeletePresentationModel"){
                return this.handleDeletePresentationModelCommand(< DeletePresentationModelCommand>command);
            }else if(command.id == "DeleteAllPresentationModelsOfType"){
                return this.handleDeleteAllPresentationModelOfTypeCommand(< DeleteAllPresentationModelsOfTypeCommand>command);
            }else if(command.id == "CreatePresentationModel"){
                return this.handleCreatePresentationModelCommand(< CreatePresentationModelCommand>command);
            }else if(command.id == "ValueChanged"){
                return this.handleValueChangedCommand(< ValueChangedCommand>command);
            }else if(command.id == "SwitchPresentationModel"){
                return this.handleSwitchPresentationModelCommand(< SwitchPresentationModelCommand>command);
            }else if(command.id == "InitializeAttribute"){
                return this.handleInitializeAttributeCommand(< InitializeAttributeCommand>command);
            }else if(command.id == "SavedPresentationModel"){
                return this.handleSavedPresentationModelNotification(< SavedPresentationModelNotification>command);
            }else if(command.id == "PresentationModelReseted"){
                return this.handlePresentationModelResetedCommand(< PresentationModelResetedCommand>command);
            }else if(command.id == "AttributeMetadataChanged"){
                return this.handleAttributeMetadataChangedCommand(< AttributeMetadataChangedCommand>command);
            }else if(command.id == "CallNamedAction"){
                return this.handleCallNamedActionCommand(< CallNamedActionCommand>command);
            }else{
                console.log("Cannot handle, unknown command "+command);
            }

            return null;
        }
        private handleDataCommand(serverCommand:  DataCommand): any{
            return serverCommand.data;
        }
        private handleDeletePresentationModelCommand(serverCommand: DeletePresentationModelCommand): ClientPresentationModel{
            var model: ClientPresentationModel =  this.clientDolphin.findPresentationModelById(serverCommand.pmId);
            if(!model) return null;
            this.clientDolphin.getClientModelStore().deletePresentationModel(model, true);
            return model;
        }
        private handleDeleteAllPresentationModelOfTypeCommand(serverCommand: DeleteAllPresentationModelsOfTypeCommand){
            this.clientDolphin.deleteAllPresentationModelOfType(serverCommand.pmType);
            return null;
        }
        private handleCreatePresentationModelCommand(serverCommand: CreatePresentationModelCommand): ClientPresentationModel{
            if(this.clientDolphin.getClientModelStore().containsPresentationModel(serverCommand.pmId)){
                throw new Error("There already is a presentation model with id "+serverCommand.pmId+"  known to the client.");
            }
            var attributes: ClientAttribute[] = [];
            serverCommand.attributes.forEach((attr) =>{
                var clientAttribute = this.clientDolphin.attribute(attr.propertyName,attr.qualifier,attr.value, attr.tag ? attr.tag :  Tag.value());
                clientAttribute.setBaseValue(attr.baseValue);
                if(attr.id && attr.id.match(".*S$")) {
                    clientAttribute.id = attr.id;
                }
                attributes.push(clientAttribute);
            });
            var clientPm = new  ClientPresentationModel(serverCommand.pmId, serverCommand.pmType);
            clientPm.addAttributes(attributes);
            if(serverCommand.clientSideOnly){
                clientPm.clientSideOnly = true;
            }
            this.clientDolphin.getClientModelStore().add(clientPm);
            this.clientDolphin.updatePresentationModelQualifier(clientPm);
            clientPm.updateAttributeDirtyness();
            clientPm.updateDirty();
            return clientPm;
        }
        private handleValueChangedCommand(serverCommand: ValueChangedCommand): ClientPresentationModel{
            var clientAttribute:  ClientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute){
                console.log("attribute with id "+serverCommand.attributeId+" not found, cannot update old value "+serverCommand.oldValue+" to new value "+serverCommand.newValue);
                return null;
            }
            if (clientAttribute.getValue() == serverCommand.newValue) {
                //console.log("nothing to do. new value == old value");
                return null;
            }
            // Below was the code that would enforce that value changes only appear when the proper oldValue is given.
            // While that seemed appropriate at first, there are actually valid command sequences where the oldValue is not properly set.
            // We leave the commented code in the codebase to allow for logging/debugging such cases.
//            if(clientAttribute.getValue() != serverCommand.oldValue) {
//                console.log("attribute with id "+serverCommand.attributeId+" and value " + clientAttribute.getValue() +
//                            " was set to value " + serverCommand.newValue + " even though the change was based on an outdated old value of " + serverCommand.oldValue);
//            }
            clientAttribute.setValue(serverCommand.newValue);
            return null;
        }
        private handleSwitchPresentationModelCommand(serverCommand: SwitchPresentationModelCommand): ClientPresentationModel{
            var switchPm: ClientPresentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            if(!switchPm){
                console.log("switch model with id "+serverCommand.pmId+" not found, cannot switch.");
                return null;
            }
            var sourcePm = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.sourcePmId);
            if(!sourcePm){
                console.log("source model with id "+serverCommand.sourcePmId+" not found, cannot switch.");
                return null;
            }
            switchPm.syncWith(sourcePm);
            return switchPm;
        }
        private handleInitializeAttributeCommand(serverCommand:  InitializeAttributeCommand): ClientPresentationModel{
            var attribute = new  ClientAttribute(serverCommand.propertyName,serverCommand.qualifier,serverCommand.newValue, serverCommand.tag);
            if(serverCommand.qualifier){
                var attributesCopy: ClientAttribute[]= this.clientDolphin.getClientModelStore().findAllAttributesByQualifier(serverCommand.qualifier);
                if(attributesCopy){
                    if(!serverCommand.newValue){
                        var attr = attributesCopy.shift();
                        if(attr){
                            attribute.setValue(attr.getValue());
                        }
                    }else{
                        attributesCopy.forEach(attr =>{
                            attr.setValue(attribute.getValue());
                        });
                    }
                }
            }
            var presentationModel:  ClientPresentationModel;
            if(serverCommand.pmId){
                presentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            }
            if(!presentationModel){
                presentationModel = new  ClientPresentationModel(serverCommand.pmId,serverCommand.pmType);
                this.clientDolphin.getClientModelStore().add(presentationModel);
            }
            this.clientDolphin.addAttributeToModel(presentationModel,attribute);
            this.clientDolphin.updatePresentationModelQualifier(presentationModel);
            return presentationModel;
        }
        private handleSavedPresentationModelNotification(serverCommand:  SavedPresentationModelNotification){
            if(!serverCommand.pmId) return null;
            var model: ClientPresentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            if(!model){
                console.log("model with id "+serverCommand.pmId+" not found, cannot rebase.");
                return null;
            }
            model.rebase();
            return model;
        }
        private handlePresentationModelResetedCommand(serverCommand:  PresentationModelResetedCommand):  ClientPresentationModel{
            if(!serverCommand.pmId) return null;
            var model: ClientPresentationModel = this.clientDolphin.getClientModelStore().findPresentationModelById(serverCommand.pmId);
            if(!model){
                console.log("model with id "+serverCommand.pmId+" not found, cannot reset.");
                return null;
            }
            model.reset();
            return model;
        }
        private handleAttributeMetadataChangedCommand(serverCommand:  AttributeMetadataChangedCommand):  ClientPresentationModel{
            var clientAttribute = this.clientDolphin.getClientModelStore().findAttributeById(serverCommand.attributeId);
            if(!clientAttribute) return null;
            clientAttribute[serverCommand.metadataName] = serverCommand.value
            return null;
        }
        private handleCallNamedActionCommand(serverCommand:  CallNamedActionCommand):  ClientPresentationModel{
            this.clientDolphin.send(serverCommand.actionName,null);
            return null;
        }


        ///////////// push support ///////////////

        listen() : void {
            if (! this.pushEnabled) return;
            if (this.waiting) return;
            // todo: how to issue a warning if no pushListener is set?
            this.waiting = true;
            var me = this; // oh, boy, this took some time to find...
            this.send(this.pushListener, { onFinished: function(models) {
                me.waiting = false;
                me.listen();
            }, onFinishedData: null})
        }

        release() : void {
            if (! this.waiting) return;
            this.waiting = false;
            // todo: how to issue a warning if no releaseCommand is set?
            this.transmitter.signal(this.releaseCommand);
        }

    }
}