package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MTGCardTest {

    private MTGCard card;

    @Before
    public void setup() {
        MTGSet set = new MTGSet(2, "Zendikar");
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
        card.setRarity(CardProperties.RARITY.MYTHIC.getKey());
        card.setText("text");
        card.setToughness("4");
        card.setType("Creature");
        card.setNames(Arrays.asList("one", "two"));
        card.setSuperTypes(Arrays.asList("legendary", "creature"));
        card.setFlavor("flavor");
        card.setArtist("artist");
        card.setLoyalty(2);
        card.setPrintings(Arrays.asList("C16", "C15"));
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
        other.addColor(CardProperties.COLOR.WHITE.getKey());
        assertTrue(other.getColors().contains(CardProperties.COLOR.WHITE.getValue()));
        other.addColor(CardProperties.COLOR.BLUE.getKey());
        assertTrue(other.getColors().contains(CardProperties.COLOR.BLUE.getValue()));
        other.addColor(CardProperties.COLOR.BLACK.getKey());
        assertTrue(other.getColors().contains(CardProperties.COLOR.BLACK.getValue()));
        other.addColor(CardProperties.COLOR.RED.getKey());
        assertTrue(other.getColors().contains(CardProperties.COLOR.RED.getValue()));
        other.addColor(CardProperties.COLOR.GREEN.getKey());
        assertTrue(other.getColors().contains(CardProperties.COLOR.GREEN.getValue()));
    }

    @Test
    public void card_willDetectEldrazi() {
        MTGCard other = new MTGCard(1);
        other.addColor(CardProperties.COLOR.WHITE.getKey());
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
        card.addColor(CardProperties.COLOR.WHITE.getKey());
        card.addColor(CardProperties.COLOR.BLUE.getKey());
        card.setMultiColor(true);
        assertThat(card.getSingleColor(), is(-1));
        card = new MTGCard(1);
        card.addColor(CardProperties.COLOR.BLUE.getKey());
        assertThat(card.getSingleColor(), is(CardProperties.COLOR.BLUE.getValue()));
    }

}