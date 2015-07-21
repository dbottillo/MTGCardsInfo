#!/bin/bash

adb shell "run-as com.dbottillo.mtgsearchfree.debug chmod 666 /data/data/com.dbottillo.mtgsearchfree.debug/databases/cardsinfo.db"
adb shell "cp /data/data/com.dbottillo.mtgsearchfree.debug/databases/cardsinfo.db /sdcard/cardsinfo.sqlite"
adb pull /sdcard/cardsinfo.sqlite $1
adb shell "run-as com.dbottillo.mtgsearchfree.debug chmod 600 /data/data/com.dbottillo.mtgsearchfree.debug/databases/cardsinfo.db"