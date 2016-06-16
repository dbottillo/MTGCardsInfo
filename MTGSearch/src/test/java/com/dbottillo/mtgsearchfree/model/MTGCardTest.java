package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.dbottillo.mtgsearchfree.BaseTest;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        card.setColors(Arrays.asList(1, 3));
        card.belongsTo(set);
        card.setLayout("layout");
        card.setManaCost("3UW");
        card.setMultiColor(true);
        card.setNumber("23");
        card.setPower("2");
        card.setQuantity(23);
        card.setRarity(CardProperties.RARITY.MYTHIC.key);
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
    public void card_willParseColor() {
        MTGCard other = new MTGCard(2);
        other.addColor(CardProperties.COLOR.WHITE.key);
        assertTrue(other.getColors().contains(CardProperties.COLOR.WHITE.value));
        other.addColor(CardProperties.COLOR.BLUE.key);
        assertTrue(other.getColors().contains(CardProperties.COLOR.BLUE.value));
        other.addColor(CardProperties.COLOR.BLACK.key);
        assertTrue(other.getColors().contains(CardProperties.COLOR.BLACK.value));
        other.addColor(CardProperties.COLOR.RED.key);
        assertTrue(other.getColors().contains(CardProperties.COLOR.RED.value));
        other.addColor(CardProperties.COLOR.GREEN.key);
        assertTrue(other.getColors().contains(CardProperties.COLOR.GREEN.value));
    }

    @Test
    public void card_willDetectEldrazi() {
        MTGCard other = new MTGCard(1);
        other.addColor(CardProperties.COLOR.WHITE.key);
        assertFalse(other.isEldrazi());
        other = new MTGCard(1);
        other.setMultiColor(true);
        assertFalse(other.isEldrazi());
        other = new MTGCard(1);
        other.setAsALand(true);
        assertFalse(other.isEldrazi());
        other = new MTGCard(1);
        other.setAsArtifact(true);
        assertFalse(other.isEldrazi());
        other = new MTGCard(1);
        assertTrue(other.isEldrazi());
    }

    @Test
    public void card_willRetrieveSingleColor() {
        MTGCard card = new MTGCard(1);
        card.addColor(CardProperties.COLOR.WHITE.key);
        card.addColor(CardProperties.COLOR.BLUE.key);
        card.setMultiColor(true);
        assertThat(card.getSingleColor(), is(-1));
        card = new MTGCard(1);
        card.addColor(CardProperties.COLOR.BLUE.key);
        assertThat(card.getSingleColor(), is(CardProperties.COLOR.BLUE.value));
    }

}