package com.dbottillo.mtgsearchfree

import com.dbottillo.mtgsearchfree.dagger.DataModuleForTest

class TestMTGApp : MTGApp(){

    override fun generateDataModule() = DataModuleForTest()

    override fun isTesting() = true

}