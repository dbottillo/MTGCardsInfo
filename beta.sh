#!/bin/bash

gradle check
gradle connectedCheck
gradle assembleBeta --no-daemon
cp -r MTGSearch/build/outputs/apk/MTGSearch-beta.apk ~/Google\ Drive/MTGCardsInfo/MTGSearchBeta.apk
