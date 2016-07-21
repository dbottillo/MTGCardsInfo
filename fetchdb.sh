#!/bin/bash

rm -rf MTGSearch/src/debug/assets/databases/mtgsearch.db
rm -rf MTGSearch/src/main/assets/databases/mtgsearch.db
adb pull /sdcard/MTGSearchDebug/MTGCardsInfo.db MTGSearch/src/debug/assets/databases/mtgsearch.db
adb pull /sdcard/MTGSearchDebug/MTGCardsInfo.db MTGSearch/src/main/assets/databases/mtgsearch.db