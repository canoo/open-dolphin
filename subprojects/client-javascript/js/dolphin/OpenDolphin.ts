/// <reference path="DolphinBuilder.ts"/>

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

    // factory method to build an initialized dolphin
    export function makeDolphin():DolphinBuilder {
        return new DolphinBuilder();
    }

}
