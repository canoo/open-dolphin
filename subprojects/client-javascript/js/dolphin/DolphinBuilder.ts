/// <reference path="ClientDolphin.ts"/>
/// <reference path="OpenDolphin.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="ClientModelStore.ts"/>
/// <reference path="NoTransmitter.ts"/>
/// <reference path="HttpTransmitter.ts"/>
/// <reference path="CorsHttpTransmitter.ts"/>
/// <reference path="ClientAttribute.ts"/>

module opendolphin {

    export class DolphinBuilder {

        private url_: string;
        private reset_: boolean = false;
        private slackMS_ :number = 300;
        private cors_: boolean = false;

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
        public slackMS(slackMS:number):DolphinBuilder {
            this.slackMS_ = slackMS;
            return this;
        }
        public cors(cors:boolean):DolphinBuilder {
            this.cors_ = cors;
            return this;
        }
        public build():ClientDolphin {
            console.log("OpenDolphin js found");
            var clientDolphin = new ClientDolphin();
            var transmitter;
            if (this.url_ != null && this.url_.length > 0) {
                transmitter = this.cors_ ? new CorsHttpTransmitter(this.url_, this.reset_) : new HttpTransmitter(this.url_, this.reset_);
            } else {
                transmitter = new NoTransmitter();
            }
            clientDolphin.setClientConnector(new ClientConnector(transmitter, clientDolphin, this.slackMS_));
            clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
            console.log("ClientDolphin initialized");
            return clientDolphin;
        }
    }
}
