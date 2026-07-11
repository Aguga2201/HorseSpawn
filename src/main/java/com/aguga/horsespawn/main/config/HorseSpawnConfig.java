package com.aguga.horsespawn.main.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class HorseSpawnConfig {
    public boolean enableSaddle = true;
    public boolean enableChest = true;
    public boolean giveLead = false;
    public boolean spawnInCreative = true;
    public boolean spawnOnce = true;
    public boolean overwriteStats = false;
    public float speed = 10.0f;
    public float jump = 3.0f;
    public int health = 20;
    public String spawnType = "HORSE";
    public HorseMarkingsConfig horseMarkings = HorseMarkingsConfig.DEFAULT;
    public HorseVariantConfig horseVariant = HorseVariantConfig.DEFAULT;
    public HorseArmorConfig armor = HorseArmorConfig.NONE;
    public LlamaCarpetConfig llamaCarpet = LlamaCarpetConfig.NONE;
    public LlamaVariantConfig llamaVariant = LlamaVariantConfig.DEFAULT;
    public boolean defaultName = true;
    public String customName = "";

    public enum HorseMarkingsConfig {
        DEFAULT, NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS
    }

    public enum HorseVariantConfig {
        DEFAULT, WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN
    }

    public enum HorseArmorConfig {
        NONE, LEATHER,
        //? if >=1.21.9 {
        COPPER,
        //?}
        IRON, GOLD, DIAMOND,
        //? if >=1.21.11 {
        NETHERITE
        //?}
        ;

        public Item toItem() {
            return switch (this) {
                case LEATHER -> Items.LEATHER_HORSE_ARMOR;
                //? if >=1.21.9 {
                case COPPER -> Items.COPPER_HORSE_ARMOR;
                //?}
                case IRON -> Items.IRON_HORSE_ARMOR;
                case GOLD -> Items.GOLDEN_HORSE_ARMOR;
                case DIAMOND -> Items.DIAMOND_HORSE_ARMOR;
                //? if >=1.21.11 {
                case NETHERITE -> Items.NETHERITE_HORSE_ARMOR;
                //?}
                default -> null;
            };
        }
    }

    public enum LlamaVariantConfig {
        DEFAULT, CREAMY, WHITE, BROWN, GRAY
    }

    public enum LlamaCarpetConfig {
        NONE, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN, RED, ORANGE, YELLOW, LIME, GREEN, CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK;

        public Item toItem() {
            return switch (this) {
                case WHITE -> Items.WHITE_CARPET;
                case LIGHT_GRAY -> Items.LIGHT_GRAY_CARPET;
                case GRAY -> Items.GRAY_CARPET;
                case BLACK -> Items.BLACK_CARPET;
                case BROWN -> Items.BROWN_CARPET;
                case RED -> Items.RED_CARPET;
                case ORANGE -> Items.ORANGE_CARPET;
                case YELLOW -> Items.YELLOW_CARPET;
                case LIME -> Items.LIME_CARPET;
                case GREEN -> Items.GREEN_CARPET;
                case CYAN -> Items.CYAN_CARPET;
                case LIGHT_BLUE -> Items.LIGHT_BLUE_CARPET;
                case BLUE -> Items.BLUE_CARPET;
                case PURPLE -> Items.PURPLE_CARPET;
                case MAGENTA -> Items.MAGENTA_CARPET;
                case PINK -> Items.PINK_CARPET;
                default -> null;
            };
        }
    }
}