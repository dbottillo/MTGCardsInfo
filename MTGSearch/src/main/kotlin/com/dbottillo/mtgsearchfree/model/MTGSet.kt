package com.dbottillo.mtgsearchfree.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Locale

@Suppress("EqualsOrHashCode")
@Parcelize
data class MTGSet(
    var id: Int,
    var code: String? = null,
    var name: String
) : Parcelable {

    val magicCardsInfoCode: String
        get() {
            for (entry in CARDSINFOMAP.values()) {
                if (entry.set.equals(code, ignoreCase = true)) {
                    return entry.mapped
                }
            }
            return code?.toLowerCase(Locale.getDefault()) ?: ""
        }

    /**
     * equals is manually overridden because
     * the property id shouldn't be taken in consideration
     */
    override fun equals(other: Any?): Boolean {
        if (this !== other) {
            if (other is MTGSet) {
                if (this.code == other.code && this.name == other.name) {
                    return true
                }
            }
            return false
        } else {
            return true
        }
    }
}

private enum class CARDSINFOMAP(val set: String, val mapped: String) {
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
    DUAL_DECKS_JACE_CHANDRA("DD2", "ddajvc"),
    CONFLUX("CON", "cfx"),
    DUAL_DECKS_DIVINE_DEMONIC("DDC", "ddadvd"),
    FROM_THE_VAULT_EXILED("V09", "fve"),
    PLANECHASE("HOP", "pch"),
    DUAL_DECKS_GARRUCK_LILIANA("DDD", "gvl"),
    PREMIUM_DECK_SERIES_SLIVERS("H09", "pds"),
    DUAL_DECKS_PHYREXIA_COALITION("DDE", "pvc"),
    FROM_THE_VAULT_RELICS("V10", "fvr"),
    FROM_THE_VAULT_LEGENDS("V11", "fvl"),
    COMMANDER_ARSENAL("CM1", "cma"),
    KALADESH_INVENTIONS("MPS", "mpskld"),
    AMONKHET_INVOCATIONS("MPS_AKH", "mpsakh")
}
