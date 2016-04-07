package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.CustomRobolectricRunner;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;

import org.fest.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SetsStorageTest extends BaseTest{

    @Test
    public void testLoad() throws Exception {
        MTGDatabaseHelper helper = mock(MTGDatabaseHelper.class);
        SetsStorage storage = new SetsStorage(helper);
        storage.load();
        verify(helper).getSets();
    }
}