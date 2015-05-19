#!/bin/bash

sh gradlew assembleRelease --no-daemon
cp -r MTGSearch/build/outputs/apk/MTGSearch-release.apk ~/Google\ Drive/MTGCardsInfo/MTGSearch.apk
