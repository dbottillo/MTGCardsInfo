package com.dbottillo.mtgsearchfree.model.storage

import android.content.Context
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper
import com.dbottillo.mtgsearchfree.resources.MTGSet

class SetsStorage(var context: Context) {

    fun load(): List<MTGSet> {
        var helper = MTGDatabaseHelper(context)
        return helper.sets
    }

}

