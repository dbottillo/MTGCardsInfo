package com.dbottillo.mtgsearchfree.model;

public class CardProperties {

    public enum COLOR{
        WHITE(0, "White"),
        BLUE(1, "Blue"),
        BLACK(2, "Black"),
        RED(3, "Red"),
        GREEN(4, "Green");

        private int number;
        private String color;

        COLOR(int number, String color) {
            this.number = number;
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public int getNumber() {
            return number;
        }

        public static int getNumberFromString(String color) {
            for (COLOR c : COLOR.values()){
                if (c.getColor().equalsIgnoreCase(color)){
                    return c.getNumber();
                }
            }
            return -1;
        }

        public static String getStringFromNumber(int number) {
            for (COLOR c : COLOR.values()){
                if (c.getNumber() == number){
                    return c.getColor();
                }
            }
            return null;
        }
    }

    public static final String TYPE_ARTIFACT = "Artifact";
    public static final String TYPE_LAND = "Land";
    public static final String TYPE_ELDRAZI = "Eldrazi";

    public static final String RARITY_COMMON = "Common";
    public static final String RARITY_UNCOMMON = "Uncommon";
    public static final String RARITY_RARE = "Rare";
    public static final String RARITY_MYHTIC = "Mythic Rare";
}
