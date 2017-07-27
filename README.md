HLS Video Player
===================


This repo contains HLS sample video player implementation for Android platform. It is based on Google [ExoPlayer](https://google.github.io/ExoPlayer) library. It contains an extension on top of HttpDataSource, so that is easy to track http requests ExoPlayer is issuing, intercept response bytes etc.

----------


How to build the project?
-------------

The project provides support for Android 4.1 and newer. In order to build the project make sure you have Android SDK installed.

To build and install the application on a connected Android device run the following command from the root project directory:

```./gradlew installDebug```

To build, install and run tests on a connected Android device run the following command from the root project directory:
```./gradlew connectedAndroidTest```

If all tests pass - expected output should look like:
```BUILD SUCCESSFUL```

In any case xml file containing test results could be found in the following directory:
```app/build/outputs/androidTest-results/connected```
