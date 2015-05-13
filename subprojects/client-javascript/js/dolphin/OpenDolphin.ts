/// <reference path="ClientDolphin.ts"/>
/// <reference path="ClientDolphin.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="ClientModelStore.ts"/>
/// <reference path="NoTransmitter.ts"/>
/// <reference path="HttpTransmitter.ts"/>
/// <reference path="CorsHttpTransmitter.ts"/>
/// <reference path="ClientAttribute.ts"/>

/**
 * JS-friendly facade to avoid too many dependencies in plain JS code.
 * The name of this file is also used for the initial lookup of the
 * one javascript file that contains all the dolphin code.
 * Changing the name requires the build support and all users
 * to be updated as well.
 * Dierk Koenig
 */

module opendolphin {
    // factory method for the initialized dolphin
    // Deprecated ! Use 'makeDolphin() instead
    export function dolphin(url:string, reset:boolean, slackMS:number = 300):ClientDolphin {
        return makeDolphin().url(url).reset(reset).slackMS(slackMS).build();
    }

    export function makeDolphin():DolphinBuilder {
        return new DolphinBuilder();
    }

    export function dolphinCors(url:string, reset:boolean, slackMS:number = 300):ClientDolphin {
        console.log("OpenDolphin js found");
        var clientDolphin = new ClientDolphin();
        var transmitter;
        if (url != null && url.length > 0) {
            transmitter = new CorsHttpTransmitter(url, reset);
        } else {
            transmitter = new NoTransmitter();
        }
        clientDolphin.setClientConnector(new ClientConnector(transmitter, clientDolphin, slackMS));
        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
        console.log("ClientDolphin initialized");
        return clientDolphin;
    }
}
