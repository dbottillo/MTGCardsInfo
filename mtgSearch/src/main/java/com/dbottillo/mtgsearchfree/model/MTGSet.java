package com.dbottillo.mtgsearchfree.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class MTGSet implements Parcelable {

    public static final Parcelable.Creator<MTGSet> CREATOR = new Parcelable.Creator<MTGSet>() {
        @Override
        public MTGSet createFromParcel(Parcel source) {
            return new MTGSet(source);
        }

        @Override
        public MTGSet[] newArray(int size) {
            return new MTGSet[size];
        }
    };
    int id;
    String code;
    String name;

    public MTGSet(int id) {
        this.id = id;
    }

    public MTGSet(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public MTGSet(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MTGSet mtgSet = (MTGSet) o;
        if (code != null ? !code.equals(mtgSet.code) : mtgSet.code != null) {
            return false;
        }
        return name != null ? name.equals(mtgSet.name) : mtgSet.name == null;
    }

    @Override
    public String toString() {
        return "MTGSet{"
                + "id=" + id
                + ", code='" + code + '\''
                + ", name='" + name + '\''
                + '}';
    }

    String getMagicCardsInfoCode() {
        for (CARDSINFOMAP entry : CARDSINFOMAP.values()){
            if (entry.set.equalsIgnoreCase(code)){
                return entry.mapped;
            }
        }
        return code.toLowerCase(Locale.getDefault());
    }

    private enum CARDSINFOMAP {
        TORMENTO("tor", "tr"),
        DECKMASTER("dkm", "dm"),
        EXODUS("EXO", "ex"),
        UNGLUED("UGL", "ug"),
        URZA_SAGA("USG", "us"),
        ANTHOLOGIES("ATH", "at"),
        URZA_LEGACY("ULG", "ul"),
        SIXTH_EDITION("6ED", "6e"),
        PORTAL_THREE_KINGDOM("PTK", "p3k"),
        URZA_DESTINY("UDS", "ud"),
        STARTER_1999("S99", "st"),
        MERCADIAN_MASQUE("MMQ", "mm"),
        NEMESIS("NMS", "ne"),
        PROPHECY("PCY", "pr"),
        INVASION("INV", "in"),
        PLANESHIFT("PLS", "ps"),
        SEVENTH_EDITION("7ED", "7e"),
        APOCALYPSE("APC", "ap"),
        ODYSEEY("ODY", "od"),
        JUDGMENT("JUD", "ju"),
        ONSLAUGHT("ONS", "on"),
        LEGIONS("LGN", "le"),
        SCOURGE("SCG", "sc"),
        EIGTH_EDITION("8ED", "8e"),
        MIRRODIN("MRD", "mi"),
        DARKSTEEL("DST", "ds"),
        UNHINGED("UNH", "uh"),
        NINTH_EDITION("9ED", "9e"),
        GUILDPACT("GPT", "gp"),
        DISSENSION("DIS", "di"),
        COLDSNAP("CSP", "cs"),
        TIMESPIRAL("TSP", "ts"),
        TIMESPIRAL_SHIFTED("TSB", "tsts"),
        PLANAR_CHAOS("PLC", "pc"),
        LORWYN("LRW", "lw"),
        MORNINGTIDE("MOR", "mt"),
        FROM_THE_VAULT_DRAGONS("DRB", "fvd"),
        DUAL_DECKS_JACE_CHANDRA("DD2", "ddm"),
        CONFLUX("CON", "cfx"),
        DUAL_DECKS_DIVINE_DEMONIC("DDC", "ddadvd"),
        FROM_THE_VAULT_EXILED("V09", "fve"),
        PLANECHASE("HOP", "pch"),
        DUAL_DECKS_GARRUCK_LILIANA("DDD", "gvl"),
        PREMIUM_DECK_SERIES_SLIVERS("H09", "pds"),
        DUAL_DECKS_PHYREXIA_COALITION("DDE", "pvc"),
        FROM_THE_VAULT_RELICS("V10", "fvr"),
        FROM_THE_VAULT_LEGENDS("V11", "fvl"),
        COMMANDER_ARSENAL("CM1", "cma");

        private String set;
        private String mapped;

        CARDSINFOMAP(String set, String mapped) {
            this.set = set;
            this.mapped = mapped;
        }
    }
}
