/// <reference path="Command.ts"/>
/// <reference path="SignalCommand.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="Codec.ts"/>

module opendolphin {

    export class CorsHttpTransmitter implements Transmitter {

        http:XMLHttpRequest;
        sig:XMLHttpRequest; // for the signal command, which needs an extra connection

        xdhttp:XDomainRequest;
        xdSig:XDomainRequest;

        useXDomainRequest: boolean;
        codec:Codec;

        HttpCodes = {
            finished: 4,
            success : 200
        };
        constructor(public url: string, reset: boolean = true, public charset: string = "UTF-8") {
            //this.http = new XMLHttpRequest();
            //this.sig  = new XMLHttpRequest();

            this.makeNewCorsObjects();

            if (! this.useXDomainRequest) {
                this.http.withCredentials = true;
            }
            this.codec = new Codec();
            if (reset) {
                this.invalidate();
            }
        }

        // see http://www.nczonline.net/blog/2010/05/25/cross-domain-ajax-with-cross-origin-resource-sharing/
        private makeNewCorsObjects() {
            this.http = new XMLHttpRequest();
            if ("withCredentials" in this.http) { // browser supports CORS
                this.useXDomainRequest = false;
                this.http.withCredentials = true;
            } else if (typeof XDomainRequest != "undefined") { // IE 8, IE 9
                this.xdhttp = new XDomainRequest();
                this.xdSig = new XDomainRequest();
                this.useXDomainRequest = true;
            } else {
                // todo: throw exception?
                this.http = null; // browser does not support CORS
                this.xdhttp = null;
                this.xdSig = null;
                this.useXDomainRequest = false;
            }
        }

        transmit(commands:Command[], onDone:(result:Command[]) => void):void {

            if (this.useXDomainRequest) {
                this.xdhttp.onerror = (evt:ErrorEvent) => {
                    alert("could not fetch " + this.url + ", message: " + evt.message); // todo dk: make this injectable
                    onDone([]);
                };
                this.xdhttp.onload = (evt:ProgressEvent) => {
                    var responseText = this.xdhttp.responseText;
                    var responseCommands = this.codec.decode(responseText);
                    onDone(responseCommands);
                };

                this.xdhttp.open('POST', this.url);
                this.xdhttp.send(this.codec.encode(commands));
            } else {
                this.http.onerror = (evt:ErrorEvent) => {
                    alert("could not fetch " + this.url + ", message: " + evt.message); // todo dk: make this injectable
                    onDone([]);
                };

                this.http.onload = (evt:ProgressEvent) => {
                    var responseText = this.http.responseText;
                    var responseCommands = this.codec.decode(responseText);
                    onDone(responseCommands);
                };

                this.http.open('POST', this.url, true);
                this.http.overrideMimeType("application/json; charset=" + this.charset ); // todo make injectable
                this.http.send(this.codec.encode(commands));
            }

        }

        signal(command : SignalCommand) {
            if (this.useXDomainRequest) {
                this.xdSig.open('POST', this.url);
                this.xdSig.send(this.codec.encode([command]));
            } else {
                this.sig.open('POST', this.url, true);
                this.sig.send(this.codec.encode([command]));
            }

        }

        invalidate() {
            if (this.useXDomainRequest) {
                this.xdhttp.open('POST', this.url + 'invalidate?');
                this.xdhttp.send();
            } else {
                this.http.open('POST', this.url + 'invalidate?', false);
                this.http.send();
            }
        }

    }

}