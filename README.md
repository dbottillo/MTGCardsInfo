# MTG Cards Info for Android

Welcome to MTG Cards Info's open source Android app!

### Contributing
If you are interesting in contribute to the project, please check out CONTRIBUTING.md and CODE_OF_CONDUCT.md .

## How does it work
The application is written 100% Kotlin and it is a fairly standard android application: it has a quite clean architecture with different layers:
* UI layer, mostly activities and fragments
* presenters layer, the bridge between business logic and UI
* interactors layer, a wrapper around the business logic to handle how to get/pass the data (eg. background threading)
* repositories layer, the business logic of the app

## Libraries
The application makes use of several libraries:
* dagger for dependency injection
* leak canary to find memory leaks
* gson to parse json
* rx java for threading and reactive programming
* glide to load images
* crashlytics to log events and crashes
* espresso, mockito and robolectric for testing
* android support library

## Unit testing
All layers, except UI, have lots of unit tests to ensure that logic is working and will not get lost in the future releases.
If you are thinking to contribute, please have a look at the existing tests to have an idea on what to test.

## Dependencies
To check if there are new library dependencies available, please run:
```
sh dependencies.sh
```