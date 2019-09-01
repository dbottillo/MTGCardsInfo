#!/bin/bash

./gradlew clean bundleRelease checkstyle pmd lintRelease detekt testReleaseUnitTest --no-daemon
cp -r app/build/outputs/bundle/release/app-release.aab ~/Google\ Drive/MTGCardsInfo/MTGSearch.aab