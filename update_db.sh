#!/bin/bash

adb pull /sdcard/MTGSearchDebug/MTGCardsInfo.db MTGSearch/src/debug/assets/databases/mtgsearch.db
adb pull /sdcard/MTGSearchDebug/MTGCardsInfo.db MTGSearch/src/main/assets/databases/mtgsearch.db
