Client javascript Dolphin tests runner 
======================================

Prerequisites
-------------
Install node globally(node version to build is specified at 'dolphin-client-javascript.gradle').

Before karma execution make sure to run

`> npm install`

This will install all dependencies specified in package.json into node_modules in your current working directory 

Run tests locally
----------------
 
`>./node-modules/karma/bin/karma start` 

 (Install command line interface karma-cli globally to run karma simply by karma in command line)
 
Running Karma with the karma-sauce-launcher plugin locally
----------------------------------------------------------
create sauce.json file with username and key ( see example at https://github.com/saucelabs/karma-sauce-example) 
or set 'SAUCE_USERNAME' and 'SAUCE_ACCESS_KEY' system environment variables.

Then run karma using karma.conf-ci.js file configuration


`>./node-modules/karma/bin/karma start karma.conf-ci.js`

(Install command line interface karma-cli globally to run karma simply by karma in command line)
