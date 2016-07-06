package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.BaseTest;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SetsStorageTest extends BaseTest {

    @Test
    public void testLoad() throws Exception {
        MTGDatabaseHelper helper = mock(MTGDatabaseHelper.class);
        SetsStorage storage = new SetsStorage(helper);
        storage.load();
        verify(helper).getSets();
    }
}