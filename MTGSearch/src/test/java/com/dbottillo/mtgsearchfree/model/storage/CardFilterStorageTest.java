package com.dbottillo.mtgsearchfree.model.storage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardProperties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(RobolectricTestRunner.class)
public class CardFilterStorageTest {

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    static boolean w = true;
    static boolean u = false;
    static boolean b = false;
    static boolean r = false;
    static boolean g = true;

    static boolean l = true;
    static boolean e = true;
    static boolean a = false;

    static boolean c = false;
    static boolean uc = true;
    static boolean ra = true;
    static boolean my = false;

    @SuppressLint("CommitPrefEdits")
    @BeforeClass
    public static void setup() {
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);
        when(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(sharedPreferences.getBoolean(CardProperties.COLOR.WHITE.key, true)).thenReturn(w);
        when(sharedPreferences.getBoolean(CardProperties.COLOR.BLUE.key, true)).thenReturn(u);
        when(sharedPreferences.getBoolean(CardProperties.COLOR.BLACK.key, true)).thenReturn(b);
        when(sharedPreferences.getBoolean(CardProperties.COLOR.RED.key, true)).thenReturn(r);
        when(sharedPreferences.getBoolean(CardProperties.COLOR.GREEN.key, true)).thenReturn(g);

        when(sharedPreferences.getBoolean(CardProperties.TYPE.LAND.key, true)).thenReturn(l);
        when(sharedPreferences.getBoolean(CardProperties.TYPE.ARTIFACT.key, true)).thenReturn(a);
        when(sharedPreferences.getBoolean(CardProperties.TYPE.ELDRAZI.key, true)).thenReturn(e);

        when(sharedPreferences.getBoolean(CardProperties.RARITY.COMMON.key, true)).thenReturn(c);
        when(sharedPreferences.getBoolean(CardProperties.RARITY.UNCOMMON.key, true)).thenReturn(uc);
        when(sharedPreferences.getBoolean(CardProperties.RARITY.RARE.key, true)).thenReturn(ra);
        when(sharedPreferences.getBoolean(CardProperties.RARITY.MYTHIC.key, true)).thenReturn(my);
    }

    @Test
    public void testLoad() throws Exception {
        CardFilterStorage cardFilterStorage = new CardFilterStorage(sharedPreferences);
        CardFilter cardFilter = cardFilterStorage.load();
        assertThat(cardFilter.white, is(w));
        assertThat(cardFilter.blue, is(u));
        assertThat(cardFilter.red, is(r));
        assertThat(cardFilter.black, is(b));
        assertThat(cardFilter.green, is(g));

        assertThat(cardFilter.land, is(l));
        assertThat(cardFilter.artifact, is(a));
        assertThat(cardFilter.eldrazi, is(e));

        assertThat(cardFilter.common, is(c));
        assertThat(cardFilter.uncommon, is(uc));
        assertThat(cardFilter.rare, is(ra));
        assertThat(cardFilter.mythic, is(my));
    }

    @Test
    public void testSync() throws Exception {
        CardFilterStorage cardFilterStorage = new CardFilterStorage(sharedPreferences);
        CardFilter cardFilter = new CardFilter();
        cardFilter.white = w;
        cardFilter.blue = b;
        cardFilter.red = r;
        cardFilter.green = g;
        cardFilter.black = b;
        cardFilter.land = l;
        cardFilter.eldrazi = e;
        cardFilter.artifact = a;
        cardFilter.common = c;
        cardFilter.uncommon = uc;
        cardFilter.rare = ra;
        cardFilter.mythic = my;
        cardFilterStorage.sync(cardFilter);
        verify(sharedPreferences).edit();
        verify(editor).putBoolean(CardProperties.COLOR.WHITE.key, w);
        verify(editor).putBoolean(CardProperties.COLOR.BLUE.key, u);
        verify(editor).putBoolean(CardProperties.COLOR.BLACK.key, b);
        verify(editor).putBoolean(CardProperties.COLOR.RED.key, r);
        verify(editor).putBoolean(CardProperties.COLOR.GREEN.key, g);
        verify(editor).putBoolean(CardProperties.TYPE.ELDRAZI.key, e);
        verify(editor).putBoolean(CardProperties.TYPE.ARTIFACT.key, a);
        verify(editor).putBoolean(CardProperties.TYPE.LAND.key, l);
        verify(editor).putBoolean(CardProperties.RARITY.COMMON.key, c);
        verify(editor).putBoolean(CardProperties.RARITY.UNCOMMON.key, uc);
        verify(editor).putBoolean(CardProperties.RARITY.RARE.key, ra);
        verify(editor).putBoolean(CardProperties.RARITY.MYTHIC.key, my);
        verify(editor).apply();
    }
}