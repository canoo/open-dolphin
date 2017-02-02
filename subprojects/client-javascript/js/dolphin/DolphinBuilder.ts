/// <reference path="ClientDolphin.ts"/>
/// <reference path="OpenDolphin.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="ClientModelStore.ts"/>
/// <reference path="NoTransmitter.ts"/>
/// <reference path="HttpTransmitter.ts"/>
/// <reference path="ClientAttribute.ts"/>

module opendolphin {
    export class DolphinBuilder {

        url_: string;
        reset_: boolean = false;
        strictMode_: boolean = true;
        slackMS_ :number = 300;
        maxBatchSize_ :number = 50;
        supportCORS_: boolean = false;
        errorHandler_:(any) => void;

        constructor(){
        }

        public url(url:string):DolphinBuilder {
            this.url_ = url;
            return this;
        }
        public reset(reset:boolean):DolphinBuilder {
            this.reset_ = reset;
            return this;
        }
        public strictMode(strictMode:boolean):DolphinBuilder {
            this.strictMode_ = strictMode;
            return this;
        }

        public slackMS(slackMS:number):DolphinBuilder {
            this.slackMS_ = slackMS;
            return this;
        }
        public maxBatchSize(maxBatchSize:number):DolphinBuilder {
            this.maxBatchSize_ = maxBatchSize;
            return this;
        }
        public supportCORS(supportCORS:boolean):DolphinBuilder {
            this.supportCORS_ = supportCORS;
            return this;
        }
        public errorHandler(errorHandler:(any) => void):DolphinBuilder {
            this.errorHandler_ = errorHandler;
            return this;
        }
        public build():ClientDolphin {
            console.log("OpenDolphin js found");
            var clientDolphin = new ClientDolphin();
            var transmitter;
            if (this.url_ != null && this.url_.length > 0) {
                transmitter = new HttpTransmitter(this.url_, this.reset_, "UTF-8", this.errorHandler_, this.supportCORS_);
            } else {
                transmitter = new NoTransmitter();
            }
            clientDolphin.setClientConnector(new ClientConnector(transmitter, clientDolphin, this.strictMode_, this.slackMS_, this.maxBatchSize_));
            clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
            console.log("ClientDolphin initialized");
            return clientDolphin;
        }
    }
}
