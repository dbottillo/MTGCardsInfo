package com.dbottillo.mtgsearchfree.model.storage

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteItem
import com.dbottillo.mtgsearchfree.util.FileLoader
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class ReleaseNoteStorageTest {

    @Rule
    @JvmField
    var mockitoRule = MockitoJUnit.rule()

    lateinit var underTest: ReleaseNoteStorage

    @Mock lateinit var fileLoader: FileLoader
    @Mock lateinit var gson: Gson
    @Mock lateinit var list: List<ReleaseNoteItem>

    @Before
    fun setUp() {
        underTest = ReleaseNoteStorage(fileLoader, gson)
    }

    @Test
    fun `should throw an exception if file can't be loaded`() {
        whenever(fileLoader.loadRaw(R.raw.release_note)).thenThrow(Resources.NotFoundException("error"))
        val testObserver = TestObserver<List<ReleaseNoteItem>>()

        underTest.load().subscribe(testObserver)

        testObserver.assertError(Throwable::class.java)
        verify(fileLoader).loadRaw(R.raw.release_note)
        verifyNoMoreInteractions(fileLoader, gson)
    }

    @Test
    fun `should parse file and return`() {
        whenever(fileLoader.loadRaw(R.raw.release_note)).thenReturn("string")
        whenever("string".fromJson<List<ReleaseNoteItem>>(gson)).thenReturn(list)
        val testObserver = TestObserver<List<ReleaseNoteItem>>()

        underTest.load().subscribe(testObserver)

        testObserver.assertValue(list)
        verify(fileLoader).loadRaw(R.raw.release_note)
        verifyNoMoreInteractions(fileLoader)
    }
}