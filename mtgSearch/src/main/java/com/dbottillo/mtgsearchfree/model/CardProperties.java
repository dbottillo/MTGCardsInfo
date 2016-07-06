package com.dbottillo.mtgsearchfree.model;

public class CardProperties {

    public enum COLOR {
        WHITE(0, "White"),
        BLUE(1, "Blue"),
        BLACK(2, "Black"),
        RED(3, "Red"),
        GREEN(4, "Green");

        public int value;
        public String key;

        COLOR(int value, String key) {
            this.value = value;
            this.key = key;
        }

        public static int getNumberFromString(String color) {
            for (COLOR c : COLOR.values()) {
                if (c.key.equalsIgnoreCase(color)) {
                    return c.value;
                }
            }
            return -1;
        }

        public static String getStringFromNumber(int number) {
            for (COLOR c : COLOR.values()) {
                if (c.value == number) {
                    return c.key;
                }
            }
            return null;
        }
    }

    public enum TYPE {
        ARTIFACT("Artifact"),
        LAND("Land"),
        ELDRAZI("Eldrazi");

        public String key;

        TYPE(String key) {
            this.key = key;
        }
    }

    public enum RARITY {
        COMMON("Common"),
        UNCOMMON("Uncommon"),
        RARE("Rare"),
        MYTHIC("Mythic Rare");

        public String key;

        RARITY(String key) {
            this.key = key;
        }
    }

}
