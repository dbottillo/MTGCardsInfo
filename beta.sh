#!/bin/bash

gradle check
gradle connectedCheck
gradle assembleProdBeta --no-daemon
cp -r MTGSearch/build/outputs/apk/MTGSearch-prod-beta.apk ~/Google\ Drive/MTGCardsInfo/MTGSearchBeta.apk