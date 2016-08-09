package com.dbottillo.mtgsearchfree.model.database;

import com.dbottillo.mtgsearchfree.model.IntParam;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QueryComposerTest {

    @Before
    public void setup() {

    }

    @Test
    public void generateQueryFromConstructor() {
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        QueryComposer.Output output = queryComposer.build();
        assertNotNull(output.query);
        assertThat(output.query, is("SELECT * from TABLE"));
        assertTrue(output.selection.isEmpty());
    }

    @Test
    public void appendQueryManually() {
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.append("ORDER BY NAME LIMIT 400");
        QueryComposer.Output output = queryComposer.build();
        assertNotNull(output.query);
        assertThat(output.query, is("SELECT * from TABLE ORDER BY NAME LIMIT 400"));
        assertTrue(output.selection.isEmpty());
    }

    @Test
    public void generateQueryWithOneParameter(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("NAME", "=", "island");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE NAME = ?"));
        assertThat(output.selection.size(), is(1));
        assertThat(output.selection.get(0), is("island"));
    }


    @Test
    public void generateQueryWithIntParamParameter(){
        IntParam intParam = new IntParam(">", 3);
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("NAME", intParam);
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE (CAST(NAME as integer) > ? AND NAME != '')"));
        assertThat(output.selection.size(), is(1));
        assertThat(output.selection.get(0), is("3"));
    }

    @Test
    public void generateQueryWithLikeParameter(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addLikeParam("NAME", "island");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE NAME LIKE ?"));
        assertThat(output.selection.size(), is(1));
        assertThat(output.selection.get(0), is("%island%"));
    }

    @Test
    public void generateQueryWithTwoParameters(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("NAME", "=", "island");
        queryComposer.addParam("CMC", "<=", 0);
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE NAME = ? AND (CAST(CMC as integer) <= ? AND CMC != '')"));
        assertThat(output.selection.size(), is(2));
        assertThat(output.selection.get(0), is("island"));
        assertThat(output.selection.get(1), is("0"));
    }

    @Test
    public void generateQueryWithIntParamBeforeStringParam(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("CMC", ">", 0);
        queryComposer.addParam("NAME", "=", "island");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE (CAST(CMC as integer) > ? AND CMC != '') AND NAME = ?"));
        assertThat(output.selection.size(), is(2));
        assertThat(output.selection.get(0), is("0"));
        assertThat(output.selection.get(1), is("island"));
    }

    @Test
    public void generateQueryWithMultipleParamValues(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addMultipleParam("rarity", "=", "OR", "Uncommon", "Rare");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE (rarity = ? OR rarity = ?)"));
        assertThat(output.selection.size(), is(2));
        assertThat(output.selection.get(0), is("Uncommon"));
        assertThat(output.selection.get(1), is("Rare"));
    }

    @Test
    public void generateQueryWithMultipleLikeParamValues(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addMultipleParam("types", "LIKE", "OR", "Creature", "Dragon");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE (types LIKE ? OR types LIKE ?)"));
        assertThat(output.selection.size(), is(2));
        assertThat(output.selection.get(0), is("%Creature%"));
        assertThat(output.selection.get(1), is("%Dragon%"));
    }

    @Test
    public void ignoreEmptyOrNullColumnName(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam(null, "=", "island");
        queryComposer.addParam("", "=", "island");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE"));
        assertThat(output.selection.size(), is(0));
    }

    @Test
    public void ignoreEmptyParam(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("name", "=", "");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE"));
        assertThat(output.selection.size(), is(0));
    }

    @Test
    public void ignoreEmptyIntParam(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("name", null);
        queryComposer.addParam("name", new IntParam("",-1));
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE"));
        assertThat(output.selection.size(), is(0));
    }

    @Test
    public void ignoresEmptyMultipleParams(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addMultipleParam("rarity", "OR", "LIKE");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE"));
        assertThat(output.selection.size(), is(0));
    }

    @Test
    public void generateQueryWithNameAndMultipleParamValues(){
        QueryComposer queryComposer = new QueryComposer("SELECT * from TABLE");
        queryComposer.addParam("NAME", "LIKE", "island");
        queryComposer.addMultipleParam("rarity", "=", "OR", "Uncommon", "Rare");
        QueryComposer.Output output = queryComposer.build();
        assertThat(output.query, is("SELECT * from TABLE WHERE NAME LIKE ? AND (rarity = ? OR rarity = ?)"));
        assertThat(output.selection.size(), is(3));
        assertThat(output.selection.get(0), is("%island%"));
        assertThat(output.selection.get(1), is("Uncommon"));
        assertThat(output.selection.get(2), is("Rare"));
    }
}
