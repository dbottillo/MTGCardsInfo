package com.dbottillo.mtgsearchfree.releasenote

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.util.FileManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import kotlin.jvm.JvmField
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class ReleaseNoteStorageTest {

    @Rule @JvmField var mockitoRule = MockitoJUnit.rule()!!

    lateinit var underTest: ReleaseNoteStorage

    @Mock lateinit var fileManager: FileManager
    @Mock lateinit var gson: Gson
    @Mock lateinit var list: List<ReleaseNoteItem>

    @Before
    fun setUp() {
        underTest = ReleaseNoteStorage(fileManager, gson)
    }

    @Test
    fun `should throw an exception if file can't be loaded`() {
        whenever(fileManager.loadRaw(R.raw.release_note)).thenThrow(Resources.NotFoundException("error"))
        val testObserver = TestObserver<List<ReleaseNoteItem>>()

        underTest.load().subscribe(testObserver)

        testObserver.assertError(Throwable::class.java)
        verify(fileManager).loadRaw(R.raw.release_note)
        verifyNoMoreInteractions(fileManager, gson)
    }

    @Test
    fun `should parse file and return`() {
        whenever(fileManager.loadRaw(R.raw.release_note)).thenReturn("string")
        val gsonType = object : TypeToken<List<ReleaseNoteItem>>() {}.type
        whenever(gson.fromJson<List<ReleaseNoteItem>>("string", gsonType)).thenReturn(list)
        val testObserver = TestObserver<List<ReleaseNoteItem>>()

        underTest.load().subscribe(testObserver)

        testObserver.assertValue(list)
        verify(fileManager).loadRaw(R.raw.release_note)
        verify(gson).fromJson<List<ReleaseNoteItem>>("string", gsonType)
        verifyNoMoreInteractions(fileManager, gson)
    }
}