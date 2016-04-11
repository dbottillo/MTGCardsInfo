package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@SmallTest
public class MTGCardTest extends BaseTest {

    MTGCard card;
    MTGSet set;

    @Before
    public void setup() {
        set = new MTGSet(2, "Zendikar");
        set.setCode("ZEN");
        card = new MTGCard(1);
        card.setSideboard(true);
        card.setMultiVerseId(200);
        card.setAsALand(false);
        card.setAsArtifact(true);
        card.setCardName("Name");
        card.setCmc(2);
        card.setColors(Arrays.asList(1,3));
        card.belongsTo(set);
        card.setLayout("layout");
        card.setManaCost("3UW");
        card.setMultiColor(true);
        card.setNumber("23");
        card.setPower("2");
        card.setQuantity(23);
        card.setRarity(CardProperties.RARITY_MYHTIC);
        card.setText("text");
        card.setToughness("4");
        card.setType("Creature");
    }

    @Test
    public void card_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        card.writeToParcel(parcel, card.describeContents());
        parcel.setDataPosition(0);

        MTGCard createdFromParcel = MTGCard.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel, is(card));
    }

    @Test
    public void card_willParseColor(){
        MTGCard other = new MTGCard(2);
        other.addColor(CardProperties.COLOR.WHITE.getColor());
        assertTrue(other.getColors().contains(CardProperties.COLOR.WHITE.getNumber()));
        other.addColor(CardProperties.COLOR.BLUE.getColor());
        assertTrue(other.getColors().contains(CardProperties.COLOR.BLUE.getNumber()));
        other.addColor(CardProperties.COLOR.BLACK.getColor());
        assertTrue(other.getColors().contains(CardProperties.COLOR.BLACK.getNumber()));
        other.addColor(CardProperties.COLOR.RED.getColor());
        assertTrue(other.getColors().contains(CardProperties.COLOR.RED.getNumber()));
        other.addColor(CardProperties.COLOR.GREEN.getColor());
        assertTrue(other.getColors().contains(CardProperties.COLOR.GREEN.getNumber()));
    }

}