/// <reference path="Command.ts"/>
/// <reference path="SignalCommand.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="Codec.ts"/>

module opendolphin {

    export class HttpTransmitter implements Transmitter {

        http:XMLHttpRequest;
        // sig:XMLHttpRequest; // for the signal command, which needs an extra connection
        codec:Codec;
        private errorHandler: (any) => void;
        supportCORS: boolean;


        HttpCodes = {
            finished: 4,
            success : 200
        };
        constructor(public url: string, reset: boolean = true, public charset: string = "UTF-8", errorHandler: (any) => void = null, supportCORS: boolean = false) {
            this.errorHandler = errorHandler;
            this.supportCORS = supportCORS;
            this.http = new XMLHttpRequest();
            // this.sig  = new XMLHttpRequest();
            if (this.supportCORS) {
                if ("withCredentials" in this.http) { // browser supports CORS
                    this.http.withCredentials = true; // NOTE: doing this for non CORS requests has no impact
                    // this.sig.withCredentials = true;
                }
                // NOTE: Browser might support CORS partially so we simply try to use 'this.http' for CORS requests instead of forbidding it
                // NOTE: XDomainRequest for IE 8, IE 9 not supported by dolphin because XDomainRequest does not support cookies in CORS requests (which are needed for the JSESSIONID cookie)
            }

            this.codec = new Codec();
            if (reset) {
                console.log('HttpTransmitter.invalidate() is deprecated. Use ClientDolphin.reset(OnSuccessHandler) instead');
                this.invalidate();
            }
        }

        transmit(commands:Command[], onDone:(result:Command[]) => void):void {

            this.http.onerror = (evt:ErrorEvent) => {
                this.handleError('onerror', "");
                onDone([]);
            };

            this.http.onreadystatechange= (evt:ProgressEvent) => {
                if (this.http.readyState == this.HttpCodes.finished){
                    if(this.http.status == this.HttpCodes.success)
                    {
                        var responseText = this.http.responseText;
                        if (responseText.trim().length > 0) {
                            try {
                                var responseCommands = this.codec.decode(responseText);
                                onDone(responseCommands);
                            }
                            catch (err) {
                                console.log("Error occurred parsing responseText: ", err);
                                console.log("Incorrect responseText: ", responseText);
                                this.handleError('application', "HttpTransmitter: Incorrect responseText: " + responseText);
                                onDone([]);
                            }
                        }
                        else {
                            this.handleError('application', "HttpTransmitter: empty responseText");
                            onDone([]);
                        }
                    }
                    else {
                        this.handleError('application', "HttpTransmitter: HTTP Status != 200");
                        onDone([]);
                    }
                }
            };

            this.http.open('POST', this.url, true);
            if ("overrideMimeType" in this.http) {
                this.http.overrideMimeType("application/json; charset=" + this.charset ); // todo make injectable
            }
            this.http.send(this.codec.encode(commands));

        }

        private handleError(kind:String, message:String) {
            var errorEvent:any = {kind: kind, url: this.url, httpStatus: this.http.status, message: message};
            if (this.errorHandler) {
                this.errorHandler(errorEvent);
            }
            else {
                console.log("Error occurred: ", errorEvent);
            }
        }

        signal(command : SignalCommand) {
            setTimeout( () => { // order this at the end of the current UI events - after any imminent value changes
                this.http.open('POST', this.url, true);
                this.http.send(this.codec.encode([command]));
            }, 1);
        }

        // Deprecated ! Use 'reset(OnSuccessHandler) instead
        invalidate() {
            this.http.open('POST', this.url + 'invalidate?', false);
            this.http.send();
        }
        reset(successHandler:OnSuccessHandler) {
            this.http.onreadystatechange = (evt:ProgressEvent) => {
                if (this.http.readyState == this.HttpCodes.finished) {
                    if (this.http.status == this.HttpCodes.success) {
                        successHandler.onSuccess();
                    }
                    else {
                        this.handleError('application', "HttpTransmitter.reset(): HTTP Status != 200");
                    }
                }
            };

            this.http.open('POST', this.url + 'invalidate?', true); // todo dk: remove all invalidation-related code.
            this.http.send();
        }

    }

}