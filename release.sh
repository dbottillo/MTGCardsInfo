#!/bin/bash

sh gradle check
sh gradle assembleRelease --no-daemon
cp -r MTGSearch/build/outputs/apk/MTGSearch-release.apk ~/Google\ Drive/MTGCardsInfo/MTGSearch.apk
