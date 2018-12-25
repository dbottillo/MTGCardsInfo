#!/bin/bash

adb pull /sdcard/MTGSearchDebug/MTGCardsInfo.db app/src/debug/assets/databases/mtgsearch.db
adb pull /sdcard/MTGSearchDebug/MTGCardsInfo.db app/src/main/assets/databases/mtgsearch.db
