package com.dbottillo.mtgsearchfree

import com.dbottillo.mtgsearchfree.dagger.DataModuleForAndroidTest

class AndroidTestMTGApp : MTGApp() {

    override fun generateDataModule() = DataModuleForAndroidTest()

    override fun isTesting() = true
}
