#!/bin/bash

./gradlew clean spotlessCheck detekt bundleRelease app:lintRelease testReleaseUnitTest --no-daemon
cp -r app/build/outputs/bundle/release/app-release.aab ~/Google\ Drive/MTGCardsInfo/MTGSearch.aab