// Karma configuration
// Generated on Fri Aug 21 2015 16:37:20 GMT+0200 (Romance Daylight Time)
module.exports = function(config) {

  // Browsers to run on Sauce Labs
  var customLaunchers = {
    'SL_Chrome': {
      base: 'SauceLabs',
      browserName: 'chrome'
    },
    'SL_InternetExplorer': {
      base: 'SauceLabs',
      browserName: 'internet explorer'
    },
    'SL_FireFox': {
      base: 'SauceLabs',
      browserName: 'firefox'
    },
    'SL_FireFox_v26': {
      base: 'SauceLabs',
      browserName: 'firefox',
      version: 26

    },
    'SL_safari': {
      base: 'SauceLabs',
      browserName: 'safari'
    }
  };

  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: [],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      'AllTests.ts': ['typescript']
    },


    typescriptPreprocessor: {
      options: {
        sourceMap: true
      }
    },


    // list of files / patterns to load in the browser
    files: [
      'AllTests.ts',
      'tsUnitKarmaAdapter.js'
    ],


    // list of files to exclude
    exclude: [
    ],


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'saucelabs'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    sauceLabs: {
      testName: 'Karma and Sauce Labs'
    },


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,

    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 60000,


    customLaunchers: customLaunchers,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: Object.keys(customLaunchers),


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: true
  })
};
