package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseContextTest;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlayerTest extends BaseContextTest {

    private Player player;

    @Before
    public void setup() {
        player = new Player(2, "Jayce");
        player.setLife(13);
        player.setPoisonCount(7);
        player.setDiceResult(11);
    }

    @Test
    public void deck_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        player.writeToParcel(parcel, player.describeContents());
        parcel.setDataPosition(0);

        Player createdFromParcel = Player.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(player));
        assertThat(createdFromParcel.getLife(), is(13));
        assertThat(createdFromParcel.getPoisonCount(), is(7));
        assertThat(createdFromParcel.getDiceResult(), is(11));
    }

}