#!/bin/bash

./gradlew clean detektAll bundleRelease app:lintRelease testReleaseUnitTest --no-daemon
cp -r app/build/outputs/bundle/release/app-release.aab ~/Google\ Drive/My\ Drive/MTGCardsInfo/MTGSearch.aab