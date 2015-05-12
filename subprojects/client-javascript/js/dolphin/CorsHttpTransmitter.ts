/// <reference path="Command.ts"/>
/// <reference path="SignalCommand.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="Codec.ts"/>

module opendolphin {

    export class CorsHttpTransmitter implements Transmitter {

        http:XMLHttpRequest;
        xdhttp:XDomainRequest;
        useXdHttp: boolean;
        codec:Codec;

        HttpCodes = {
            finished: 4,
            success : 200
        };
        constructor(public url: string, reset: boolean = true) {
            this.http = new XMLHttpRequest();
            this.makeNewCorsObject();

            if (! this.useXdHttp) {
                this.http.withCredentials = true;
            }
            this.codec = new Codec();
            if (reset) {
                this.invalidate();
            }
        }

        // see http://www.nczonline.net/blog/2010/05/25/cross-domain-ajax-with-cross-origin-resource-sharing/
        private makeNewCorsObject() {
            this.http = new XMLHttpRequest();
            if ("withCredentials" in this.http) { // browser supports CORS
                this.useXdHttp = false;
            } else if (typeof XDomainRequest != "undefined") { // IE 8, IE 9
                this.xdhttp = new XDomainRequest();
                this.useXdHttp = true;
            } else {
                // todo: throw exception?
                this.http = null; // browser does not support CORS
                this.xdhttp = null; // browser does not support CORS
                this.useXdHttp = false;
            }
        }

        transmit(commands:Command[], onDone:(result:Command[]) => void):void {

            if (this.useXdHttp) {
                this.transmitXd(commands, onDone);
                return;
            }

            this.http.onerror = (evt:ErrorEvent) => {
                alert("could not fetch " + this.url + ", message: " + evt.message); // todo dk: make this injectable
                onDone([]);
            };

            this.http.onreadystatechange= (evt:ProgressEvent) => {
                if (this.http.readyState == this.HttpCodes.finished){

                    if(this.http.status == this.HttpCodes.success)
                    {
                        var responseText = this.http.responseText;
                        var responseCommands = this.codec.decode(responseText);
                        onDone(responseCommands);
                    }
                    //todo ks: if status is not 200 then show error
                }
            };

            this.http.open('POST', this.url, true);
            this.http.send(this.codec.encode(commands));

        }

        transmitXd(commands:Command[], onDone:(result:Command[]) => void):void {
            this.xdhttp.onerror = (evt:ErrorEvent) => {
                alert("could not fetch " + this.url + ", message: " + evt.message); // todo dk: make this injectable
                onDone([]);
            };
            this.xdhttp.onload = (evt:ProgressEvent) => {
                var responseText = this.xdhttp.responseText;
                var responseCommands = this.codec.decode(responseText);
                onDone(responseCommands);
            };
            this.xdhttp.open('POST', this.url, true);
            this.xdhttp.send(this.codec.encode(commands));
        }

        signal(command : SignalCommand) {
            var sig = new XMLHttpRequest(); // the signal commands need an extra connection
            sig.open('POST', this.url, true);
            sig.send(this.codec.encode([command]));
        }

        invalidate() {
            this.http.open('POST', this.url + 'invalidate?', false);
            this.http.send();
        }

    }

}