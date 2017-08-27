#!/bin/bash

./gradlew release --no-daemon
cp -r MTGSearch/build/outputs/apk/release/MTGSearch-release.apk ~/Google\ Drive/MTGCardsInfo/MTGSearch.apk
cp -r MTGSearch/build/outputs/apk/release/MTGSearch-release.apk MTGSearch/release/MTGSearch-release.apk