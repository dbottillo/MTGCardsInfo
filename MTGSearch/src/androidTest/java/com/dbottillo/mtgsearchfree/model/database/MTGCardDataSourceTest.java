package com.dbottillo.mtgsearchfree.model.database;

import android.content.res.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.dbottillo.mtgsearchfree.model.IntParam;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.util.BaseContextTest;
import com.dbottillo.mtgsearchfree.util.FileHelper;
import com.dbottillo.mtgsearchfree.util.LOG;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MTGCardDataSourceTest extends BaseContextTest {

    private MTGCardDataSource cardDataSource;

    @Before
    public void setup() {
        cardDataSource = new MTGCardDataSource(mtgDatabaseHelper);
    }

    @Test
    public void fetchesAllSets() throws JSONException {
        ArrayList<MTGSet> setsJ = FileHelper.readSetListJSON(context);
        for (MTGSet set : setsJ) {
            //LOG.e("checking set: " + set.getId() + " - " + set.getName());
            try {
                ArrayList<MTGCard> cardsJ = FileHelper.readSingleSetFile(set, context);
                List<MTGCard> cards = cardDataSource.getSet(set);
                assertThat(cardsJ.size(), is(cards.size()));
                assertTrue(cards.containsAll(cardsJ));
            } catch (Resources.NotFoundException e) {
                LOG.e(set.getCode() + " file not found");
            }
        }
    }

    @Test
    public void getsRandomCards() {
        List<MTGCard> cards = cardDataSource.getRandomCard(10);
        assertThat(cards.size(), is(10));
    }

    @Test
    public void searchCardsByName() {
        SearchParams searchParams = new SearchParams();
        searchParams.setName("Dragon");
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        for (MTGCard card : cards) {
            assertTrue(card.getName().toLowerCase(Locale.getDefault()).contains("dragon"));
        }
    }

    @Test
    public void searchCardsByType() {
        SearchParams searchParams = new SearchParams();
        searchParams.setTypes("creature");
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getType().toLowerCase(Locale.getDefault()).contains("creature"));
        }
    }

    @Test
    public void searchCardsByText() {
        SearchParams searchParams = new SearchParams();
        searchParams.setText("lifelink");
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getText().toLowerCase(Locale.getDefault()).contains("lifelink"));
        }
    }


    @Test
    public void searchCardsByCMC() {
        SearchParams searchParams = new SearchParams();
        for (int i = 0; i < OPERATOR.values().length; i++) {
            OPERATOR operator = OPERATOR.values()[i];
            searchParams.setCmc(operator.generateParam());
            List<MTGCard> cards = cardDataSource.searchCards(searchParams);
            assertTrue(cards.size() > 0);
            for (MTGCard card : cards) {
                operator.assertOperator(card.getCmc());
            }
        }
    }

    @Test
    public void searchCardsByPower() {
        SearchParams searchParams = new SearchParams();
        for (int i = 0; i < OPERATOR.values().length; i++) {
            OPERATOR operator = OPERATOR.values()[i];
            searchParams.setPower(operator.generateParam());
            List<MTGCard> cards = cardDataSource.searchCards(searchParams);
            assertTrue(cards.size() > 0);
            for (MTGCard card : cards) {
                if (!card.getPower().equals("*")) {
                    operator.assertOperator(Integer.parseInt(card.getPower()));
                }
            }
        }
    }

    @Test
    public void searchCardsByToughness() {
        SearchParams searchParams = new SearchParams();
        for (int i = 0; i < OPERATOR.values().length; i++) {
            OPERATOR operator = OPERATOR.values()[i];
            searchParams.setTough(operator.generateParam());
            List<MTGCard> cards = cardDataSource.searchCards(searchParams);
            assertTrue(cards.size() > 0);
            for (MTGCard card : cards) {
                if (!card.getPower().equals("*")) {
                    operator.assertOperator(Integer.parseInt(card.getToughness()));
                }
            }
        }
    }

    @Test
    public void searchCardsByColor() {
        SearchParams searchParams = new SearchParams();
        searchParams.setWhite(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("W"));
        }
        searchParams = new SearchParams();
        searchParams.setBlue(true);
        cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("U"));
        }
        searchParams = new SearchParams();
        searchParams.setRed(true);
        cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("R"));
        }
        searchParams = new SearchParams();
        searchParams.setBlack(true);
        cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("B"));
        }
        searchParams = new SearchParams();
        searchParams.setGreen(true);
        cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("G"));
        }
    }

    @Test
    public void searchCardsWithTwoColors() {
        SearchParams searchParams = new SearchParams();
        searchParams.setWhite(true);
        searchParams.setBlue(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("W"));
            assertThat(card.getManaCost(), containsString("U"));
        }
    }

    @Test
    public void searchCardsWithTwoColorsOnlyMulticolor() {
        SearchParams searchParams = new SearchParams();
        searchParams.setWhite(true);
        searchParams.setBlue(true);
        searchParams.setOnlyMulti(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getManaCost(), containsString("W"));
            assertThat(card.getManaCost(), containsString("U"));
        }
    }

    @Test
    public void searchCardsWithTwoColorsNoMulticolor() {
        SearchParams searchParams = new SearchParams();
        searchParams.setWhite(true);
        searchParams.setBlue(true);
        searchParams.setNoMulti(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue((card.getManaCost().contains("W") && !card.getManaCost().contains("U"))
                    || (card.getManaCost().contains("U") && !card.getManaCost().contains("W")));
        }
    }

    @Test
    public void searchCommonCards() {
        SearchParams searchParams = new SearchParams();
        searchParams.setCommon(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getRarity().equalsIgnoreCase("Common"));
        }
    }

    @Test
    public void searchUncommonCards() {
        SearchParams searchParams = new SearchParams();
        searchParams.setUncommon(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getRarity().equalsIgnoreCase("Uncommon"));
        }
    }

    @Test
    public void searchRareCards() {
        SearchParams searchParams = new SearchParams();
        searchParams.setRare(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getRarity().equalsIgnoreCase("Rare"));
        }
    }

    @Test
    public void searchMythicCards() {
        SearchParams searchParams = new SearchParams();
        searchParams.setMythic(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getRarity().equalsIgnoreCase("Mythic Rare"));
        }
    }

    @Test
    public void searchRareAndMythicCards() {
        SearchParams searchParams = new SearchParams();
        searchParams.setRare(true);
        searchParams.setMythic(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getRarity().equalsIgnoreCase("Rare") || card.getRarity().equalsIgnoreCase("Mythic Rare"));
        }
    }

    @Test
    public void search_cards_by_multiple_types() {
        SearchParams searchParams = new SearchParams();
        searchParams.setTypes("creature angel");
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getType().toLowerCase(Locale.getDefault()).contains("creature") && card.getType().toLowerCase(Locale.getDefault()).contains("angel"));
        }
        searchParams.setTypes("creature angel ally");
        List<MTGCard> cards2 = cardDataSource.searchCards(searchParams);
        assertTrue(cards2.size() > 0);
        for (MTGCard card : cards2) {
            assertTrue(card.getType().toLowerCase(Locale.getDefault()).contains("creature") && card.getType().toLowerCase(Locale.getDefault()).contains("angel") && card.getType().toLowerCase(Locale.getDefault()).contains("ally"));
        }
    }

    @Test
    public void search_cards_by_set_id() {
        MTGSet set = mtgDatabaseHelper.getSets().get(0);
        SearchParams searchParams = new SearchParams();
        searchParams.setSetId(set.getId());
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(card.getSet(), is(set));
        }
    }

    @Test
    public void search_cards_by_standard() {
        SearchParams searchParams = new SearchParams();
        searchParams.setSetId(-2);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertThat(MTGCardDataSource.STANDARD.getSetNames(), hasItem(card.getSet().getName()));
        }
    }

    @Test
    public void search_cards_by_name_and_types() {
        SearchParams searchParams = new SearchParams();
        searchParams.setName("angel");
        searchParams.setTypes("creature angel");
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getName().toLowerCase(Locale.getDefault()).contains("angel"));
            assertTrue(card.getType().toLowerCase(Locale.getDefault()).contains("creature") && card.getType().toLowerCase(Locale.getDefault()).contains("angel"));
        }
    }

    @Test
    public void search_cards_with_multiple_params() {
        SearchParams searchParams = new SearchParams();
        searchParams.setName("angel");
        searchParams.setTypes("creature");
        searchParams.setWhite(true);
        searchParams.setNoMulti(true);
        searchParams.setRare(true);
        searchParams.setPower(new IntParam("=", 4));
        searchParams.setTough(new IntParam("=", 4));
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getName().toLowerCase(Locale.getDefault()).contains("angel"));
            assertTrue(card.getType().toLowerCase(Locale.getDefault()).contains("creature"));
            assertTrue(card.getManaCost().contains("W"));
            assertTrue(Integer.parseInt(card.getPower()) == 4);
            assertTrue(Integer.parseInt(card.getToughness()) == 4);
            assertTrue(card.getRarity().equalsIgnoreCase("Rare"));
        }
    }

    @Test
    public void MTGCardDataSource_searchCardByName() {
        String[] toTest = new String[]{"Wasteland", "Ulamog, the Ceaseless Hunger",
                "Urborg, Tomb of Yawgmoth", "Engineered Explosives"};
        MTGCard card;
        for (String name : toTest) {
            card = cardDataSource.searchCard(name);
            assertNotNull(card);
            assertThat(card.getName(), is(name));
        }
        card = cardDataSource.searchCard("Obama");
        assertNull(card);
    }

    @Test
    public void searchCardsByLands() {
        SearchParams searchParams = new SearchParams();
        searchParams.setName("island");
        searchParams.setLand(true);
        List<MTGCard> cards = cardDataSource.searchCards(searchParams);
        assertNotNull(cards);
        assertTrue(cards.size() > 0);
        for (MTGCard card : cards) {
            assertTrue(card.getName().toLowerCase(Locale.getDefault()).contains("island"));
            assertTrue(card.isLand());
        }
    }

    private static final int NUMBER = 5;

    private enum OPERATOR {
        EQUAL("=") {
            @Override
            public void assertOperator(int value) {
                assertThat(value, is(NUMBER));
            }
        },
        LESS("<") {
            @Override
            public void assertOperator(int value) {
                assertThat(value, lessThan(NUMBER));
            }
        },
        MORE(">") {
            @Override
            public void assertOperator(int value) {
                assertThat(value, greaterThan(NUMBER));
            }
        },
        EQUAL_LESS("<=") {
            @Override
            public void assertOperator(int value) {
                assertThat(value, lessThanOrEqualTo(NUMBER));
            }
        },
        EQUAL_MORE(">=") {
            @Override
            public void assertOperator(int value) {
                assertThat(value, greaterThanOrEqualTo(NUMBER));
            }
        };

        private String operator;


        OPERATOR(String s) {
            operator = s;
        }

        public abstract void assertOperator(int value);

        public IntParam generateParam() {
            return new IntParam(operator, NUMBER);
        }
    }

}