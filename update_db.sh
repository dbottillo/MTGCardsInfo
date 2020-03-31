#!/bin/bash

adb pull /storage/emulated/0/Android/data/com.dbottillo.mtgsearchfree.debug/files/MTGCardsInfo.db app/src/debug/assets/databases/mtgsearch.db
adb pull /storage/emulated/0/Android/data/com.dbottillo.mtgsearchfree.debug/files/MTGCardsInfo.db app/src/main/assets/databases/mtgsearch.db
