package com.dbottillo.mtgsearchfree.model.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.dbottillo.mtgsearchfree.model.Legality;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.BaseContextTest;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.StringUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardDataSourceIntegrationTest extends BaseContextTest {

    @Mock
    Cursor cursor;
    @Mock
    MTGCard card;

    private MTGCardDataSource mtgCardDataSource;
    private CardDataSource underTest;

    @Before
    public void setup() {
        underTest = new CardDataSource(cardsInfoDbHelper.getWritableDatabase(), new Gson());
        mtgCardDataSource = new MTGCardDataSource(mtgDatabaseHelper.getWritableDatabase(), underTest);
    }

    @Test
    public void test_generate_table_is_correct() {
        String query = CardDataSource.generateCreateTable();
        assertNotNull(query);
        assertThat(query, is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT,names TEXT,supertypes TEXT,flavor TEXT,artist TEXT,loyalty INTEGER,printings TEXT,legalities TEXT,originalText TEXT,mciNumber TEXT,colorIdentity TEXT)"));
        assertThat(CardDataSource.generateCreateTable(1), is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT)"));
        assertThat(CardDataSource.generateCreateTable(2), is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT)"));
        assertThat(CardDataSource.generateCreateTable(3), is("CREATE TABLE IF NOT EXISTS MTGCard (_id INTEGER PRIMARY KEY, name TEXT,type TEXT,types TEXT,subtypes TEXT,colors TEXT,cmc INTEGER,rarity TEXT,power TEXT,toughness TEXT,manaCost TEXT,text TEXT,multicolor INTEGER,land INTEGER,artifact INTEGER,multiVerseId INTEGER,setId INTEGER,setName TEXT,rulings TEXT,layout TEXT,setCode TEXT,number TEXT)"));
    }

    @Test
    public void test_card_can_be_saved_in_database() {
        MTGCard card = mtgCardDataSource.getRandomCard(1).get(0);
        long id = underTest.saveCard(card);
        Cursor cursor = cardsInfoDbHelper.getReadableDatabase().rawQuery("select * from " + CardDataSource.TABLE + " where rowid =?", new String[]{id + ""});
        assertNotNull(cursor);
        assertThat(cursor.getCount(), is(1));
        cursor.moveToFirst();
        MTGCard cardFromDb = underTest.fromCursor(cursor, true);
        assertNotNull(cardFromDb);
        assertThat(cardFromDb.getName(), is(card.getName()));
        assertThat(cardFromDb.getType(), is(card.getType()));
        assertThat(cardFromDb.getSubTypes().size(), is(card.getSubTypes().size()));
        for (int i = 0; i < cardFromDb.getSubTypes().size(); i++) {
            assertThat(cardFromDb.getSubTypes().get(i), is(card.getSubTypes().get(i)));
        }
        assertThat(cardFromDb.getColors().size(), is(card.getColors().size()));
        for (int i = 0; i < cardFromDb.getColors().size(); i++) {
            assertThat(cardFromDb.getColors().get(i), is(card.getColors().get(i)));
        }
        assertThat(cardFromDb.getCmc(), is(card.getCmc()));
        assertThat(cardFromDb.getRarity(), is(card.getRarity()));
        assertThat(cardFromDb.getPower(), is(card.getPower()));
        assertThat(cardFromDb.getToughness(), is(card.getToughness()));
        assertThat(cardFromDb.getManaCost(), is(card.getManaCost()));
        assertThat(cardFromDb.getText(), is(card.getText()));
        assertThat(cardFromDb.isMultiColor(), is(card.isMultiColor()));
        assertThat(cardFromDb.isLand(), is(card.isLand()));
        assertThat(cardFromDb.isArtifact(), is(card.isArtifact()));
        assertThat(cardFromDb.isEldrazi(), is(card.isEldrazi()));
        assertThat(cardFromDb.getSet(), is(card.getSet()));
        assertThat(cardFromDb.getLayout(), is(card.getLayout()));
        assertThat(cardFromDb.getNumber(), is(card.getNumber()));
        assertThat(cardFromDb.getRulings().size(), is(card.getRulings().size()));
        for (int i = 0; i < cardFromDb.getRulings().size(); i++) {
            assertThat(cardFromDb.getRulings().get(i), is(card.getRulings().get(i)));
        }

        assertThat(cardFromDb.getNames().size(), is(card.getNames().size()));
        for (int i = 0; i < cardFromDb.getNames().size(); i++) {
            assertThat(cardFromDb.getNames().get(i), is(card.getNames().get(i)));
        }

        assertThat(cardFromDb.getSuperTypes().size(), is(card.getSuperTypes().size()));
        for (int i = 0; i < cardFromDb.getSuperTypes().size(); i++) {
            assertThat(cardFromDb.getSuperTypes().get(i), is(card.getSuperTypes().get(i)));
        }
        assertThat(cardFromDb.getLoyalty(), is(card.getLoyalty()));
        assertThat(cardFromDb.getArtist(), is(card.getArtist()));
        assertThat(cardFromDb.getFlavor(), is(card.getFlavor()));

        assertThat(cardFromDb.getPrintings(), is(card.getPrintings()));
        for (int i = 0; i < cardFromDb.getPrintings().size(); i++) {
            assertThat(cardFromDb.getPrintings().get(i), is(card.getPrintings().get(i)));
        }
        assertThat(cardFromDb.getOriginalText(), is(card.getOriginalText()));

        assertThat(cardFromDb.getColorsIdentity(), is(card.getColorsIdentity()));
        assertThat(cardFromDb.getMciNumber(), is(card.getMciNumber()));

        assertThat(cardFromDb.getRulings().size(), is(card.getRulings().size()));
        for (int i = 0; i < cardFromDb.getRulings().size(); i++) {
            assertThat(cardFromDb.getRulings().get(i), is(card.getRulings().get(i)));
        }
        assertThat(cardFromDb.getLegalities().size(), is(card.getLegalities().size()));
        for (int i = 0; i < cardFromDb.getLegalities().size(); i++) {
            assertThat(cardFromDb.getLegalities().get(i), is(card.getLegalities().get(i)));
        }

        cursor.close();
    }

    @Test
    public void test_cards_can_be_saved_and_retrieved_from_database() {
        List<MTGCard> cardsToAdd = mtgCardDataSource.getRandomCard(5);
        for (MTGCard card : cardsToAdd) {
            underTest.saveCard(card);
        }

        List<MTGCard> cards = underTest.getCards();
        assertNotNull(cards);
        assertThat(cards.size(), is(cardsToAdd.size()));
        assertThat(cards, is(cardsToAdd));
    }

    @Test
    public void parsesCardFromCursor() {
        setupCursorCard();
        MTGCard card = underTest.fromCursor(cursor);

        assertThat(card.getId(), is(2));
        assertThat(card.getMultiVerseId(), is(1001));
        assertThat(card.getName(), is("name"));
        assertThat(card.getType(), is("type"));
        assertThat(card.getTypes(), is(Arrays.asList("Artifact", "Creature")));
        assertThat(card.getSubTypes(), is(Arrays.asList("Creature", "Artifact")));

        assertThat(card.getColors(), is(Arrays.asList(1, 2)));
        assertThat(card.getCmc(), is(1));
        assertThat(card.getRarity(), is("Rare"));
        assertThat(card.getPower(), is("2"));
        assertThat(card.getToughness(), is("3"));

        assertThat(card.getManaCost(), is("3{U}{B}"));
        assertThat(card.getText(), is("text"));

        assertFalse(card.isMultiColor());
        assertTrue(card.isLand());
        assertFalse(card.isArtifact());

        assertThat(card.getSet().getId(), is(10));
        assertThat(card.getSet().getName(), is("Commander 2016"));
        assertThat(card.getSet().getCode(), is("C16"));

        assertNotNull(card.getRulings());
        assertThat(card.getRulings().size(), is(1));
        assertThat(card.getRulings().get(0), is("If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times."));

        assertThat(card.getLayout(), is("layout"));
        assertThat(card.getNumber(), is("29"));

        assertNotNull(card.getNames());
        assertThat(card.getNames().size(), is(2));
        assertThat(card.getNames().get(0), is("Order"));
        assertThat(card.getNames().get(1), is("Chaos"));

        assertNotNull(card.getSuperTypes());
        assertThat(card.getSuperTypes().size(), is(2));
        assertThat(card.getSuperTypes().get(0), is("Creature"));
        assertThat(card.getSuperTypes().get(1), is("Artifact"));

        assertThat(card.getFlavor(), is("flavor"));
        assertThat(card.getArtist(), is("artist"));
        assertThat(card.getLoyalty(), is(4));

        assertNotNull(card.getPrintings());
        assertThat(card.getPrintings().size(), is(2));
        assertThat(card.getPrintings().get(0), is("C16"));
        assertThat(card.getPrintings().get(1), is("C17"));

        assertThat(card.getOriginalText(), is("original text"));

        assertThat(card.getMciNumber(), is("233"));
        assertNotNull(card.getColorsIdentity());
        assertThat(card.getColorsIdentity().size(), is(2));
        assertThat(card.getColorsIdentity().get(0), is("U"));
        assertThat(card.getColorsIdentity().get(1), is("W"));

        assertNotNull(card.getLegalities());
        assertThat(card.getLegalities().size(), is(3));
        assertThat(card.getLegalities().get(0), is(new Legality("Commander", "Legal")));
        assertThat(card.getLegalities().get(1), is(new Legality("Vintage", "Banned")));
        assertThat(card.getLegalities().get(2), is(new Legality("Standard", "Restricted")));
    }

    @Test
    public void createsContentValuesProperly() {
        MTGCard card = mtgCardDataSource.getRandomCard(1).get(0);
        ContentValues contentValues = underTest.createContentValue(card);

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAME.getName()), is(card.getName()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPE.getName()), is(card.getType()));

        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.SET_ID.getName()), is(card.getSet().getId()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SET_NAME.getName()), is(card.getSet().getName()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SET_CODE.getName()), is(card.getSet().getCode()));

        if (card.getColors().size() > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.COLORS.getName()), is(StringUtil.joinListOfColors(card.getColors(), ",")));
        }

        if (card.getTypes().size() > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TYPES.getName()), is(StringUtil.joinListOfStrings(card.getTypes(), ",")));
        }

        if (card.getSubTypes().size() > 0) {
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SUB_TYPES.getName()), is(StringUtil.joinListOfStrings(card.getSubTypes(), ",")));
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.MANA_COST.getName()), is(card.getManaCost()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.RARITY.getName()), is(card.getRarity()));
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.MULTIVERSE_ID.getName()), is(card.getMultiVerseId()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.POWER.getName()), is(card.getPower()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TOUGHNESS.getName()), is(card.getToughness()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.TEXT.getName()), is(card.getText()));
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.CMC.getName()), is(card.getCmc()));

        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.MULTICOLOR.getName()), is(card.isMultiColor()));
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.LAND.getName()), is(card.isLand()));
        assertThat(contentValues.getAsBoolean(CardDataSource.COLUMNS.ARTIFACT.getName()), is(card.isArtifact()));

        if (card.getRulings().size() > 0) {
            JSONArray rules = new JSONArray();
            for (String rule : card.getRulings()) {
                JSONObject rulJ = new JSONObject();
                try {
                    rulJ.put("text", rule);
                    rules.put(rulJ);
                } catch (JSONException e) {
                    LOG.e(e);
                }
            }
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.RULINGS.getName()), is(rules.toString()));
        }

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LAYOUT.getName()), is(card.getLayout()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NUMBER.getName()), is(card.getNumber()));

        Gson gson = new Gson();
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.NAMES.getName()), is(gson.toJson(card.getNames())));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.SUPER_TYPES.getName()), is(gson.toJson(card.getSuperTypes())));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.FLAVOR.getName()), is(card.getFlavor()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ARTIST.getName()), is(card.getArtist()));
        assertThat(contentValues.getAsInteger(CardDataSource.COLUMNS.LOYALTY.getName()), is(card.getLoyalty()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.PRINTINGS.getName()), is(gson.toJson(card.getPrintings())));

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.ORIGINAL_TEXT.getName()), is(card.getOriginalText()));

        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.MCI_NUMBER.getName()), is(card.getMciNumber()));
        assertThat(contentValues.getAsString(CardDataSource.COLUMNS.COLORS_IDENTITY.getName()), is(gson.toJson(card.getColorsIdentity())));

        if (card.getLegalities().size() > 0) {
            JSONArray legalities = new JSONArray();
            for (Legality legality : card.getLegalities()) {
                JSONObject legalityJ = new JSONObject();
                try {
                    legalityJ.put("format", legality.getFormat());
                    legalityJ.put("legality", legality.getLegality());
                    legalities.put(legalityJ);
                } catch (JSONException e) {
                    LOG.e(e);
                }
            }
            assertThat(contentValues.getAsString(CardDataSource.COLUMNS.LEGALITIES.getName()), is(legalities.toString()));
        }
    }

    private void setupCursorCard() {
        when(cursor.getColumnIndex("_id")).thenReturn(1);
        when(cursor.getInt(1)).thenReturn(2);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.MULTIVERSE_ID.getName())).thenReturn(2);
        when(cursor.getInt(2)).thenReturn(1001);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.NAME.getName())).thenReturn(3);
        when(cursor.getString(3)).thenReturn("name");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.TYPE.getName())).thenReturn(4);
        when(cursor.getString(4)).thenReturn("type");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.TYPES.getName())).thenReturn(5);
        when(cursor.getString(5)).thenReturn("Artifact,Creature");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.SUB_TYPES.getName())).thenReturn(6);
        when(cursor.getString(6)).thenReturn("Creature,Artifact");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.COLORS.getName())).thenReturn(7);
        when(cursor.getString(7)).thenReturn("Blue,Black");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.CMC.getName())).thenReturn(8);
        when(cursor.getInt(8)).thenReturn(1);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.RARITY.getName())).thenReturn(9);
        when(cursor.getString(9)).thenReturn("Rare");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.POWER.getName())).thenReturn(10);
        when(cursor.getString(10)).thenReturn("2");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.TOUGHNESS.getName())).thenReturn(11);
        when(cursor.getString(11)).thenReturn("3");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.MANA_COST.getName())).thenReturn(12);
        when(cursor.getString(12)).thenReturn("3{U}{B}");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.TEXT.getName())).thenReturn(13);
        when(cursor.getString(13)).thenReturn("text");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.MULTICOLOR.getName())).thenReturn(14);
        when(cursor.getInt(14)).thenReturn(0);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.LAND.getName())).thenReturn(15);
        when(cursor.getInt(15)).thenReturn(1);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.ARTIFACT.getName())).thenReturn(16);
        when(cursor.getInt(16)).thenReturn(0);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_ID.getName())).thenReturn(17);
        when(cursor.getInt(17)).thenReturn(10);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_NAME.getName())).thenReturn(18);
        when(cursor.getString(18)).thenReturn("Commander 2016");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.SET_CODE.getName())).thenReturn(19);
        when(cursor.getString(19)).thenReturn("C16");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.RULINGS.getName())).thenReturn(20);
        when(cursor.getString(20)).thenReturn("[{\"date\":\"2007-10-01\",\"text\":\"If a spell or ability has you draw multiple cards, Hoofprints of the Stag's ability triggers that many times.\"}]");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.LAYOUT.getName())).thenReturn(21);
        when(cursor.getString(21)).thenReturn("layout");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.NUMBER.getName())).thenReturn(22);
        when(cursor.getString(22)).thenReturn("29");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.NAMES.getName())).thenReturn(23);
        when(cursor.getString(23)).thenReturn("[\"Order\",\"Chaos\"]");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.SUPER_TYPES.getName())).thenReturn(24);
        when(cursor.getString(24)).thenReturn("[\"Creature\",\"Artifact\"]");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.FLAVOR.getName())).thenReturn(25);
        when(cursor.getString(25)).thenReturn("flavor");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.ARTIST.getName())).thenReturn(26);
        when(cursor.getString(26)).thenReturn("artist");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.LOYALTY.getName())).thenReturn(27);
        when(cursor.getInt(27)).thenReturn(4);

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.PRINTINGS.getName())).thenReturn(28);
        when(cursor.getString(28)).thenReturn("[\"C16\",\"C17\"]");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.LEGALITIES.getName())).thenReturn(29);
        when(cursor.getString(29)).thenReturn("[{\"format\":\"Legacy\", \"legality\" : \"Banned\" }, { \"format\" : \"Vintage\", \"legality\" : \"Restricted\" } ]");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.ORIGINAL_TEXT.getName())).thenReturn(30);
        when(cursor.getString(30)).thenReturn("original text");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.MCI_NUMBER.getName())).thenReturn(31);
        when(cursor.getString(31)).thenReturn("233");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.COLORS_IDENTITY.getName())).thenReturn(32);
        when(cursor.getString(32)).thenReturn("[\"U\",\"W\"]");

        when(cursor.getColumnIndex(CardDataSource.COLUMNS.LEGALITIES.getName())).thenReturn(33);
        when(cursor.getString(20)).thenReturn("[{\"format\":\"Commander\",\"legality\":\"Legal\",\"format\":\"Vintage\",\"legality\":\"Banned\",\"format\":\"Standard\",\"legality\":\"Restricted\"}]");

    }

}