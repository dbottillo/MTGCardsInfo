#!/bin/bash

./gradlew clean assembleRelease checkstyle pmd lintRelease detekt testReleaseUnitTest --no-daemon
cp -r app/build/outputs/apk/release/app-release.apk ~/Google\ Drive/MTGCardsInfo/MTGSearch.apk
cp -r app/build/outputs/apk/release/app-release.apk MTGSearch/release/MTGSearch-release.apk

