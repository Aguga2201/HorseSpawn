package com.aguga.horsespawn.main.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class HorseSpawnConfig {
    public boolean enableSaddle = true;
    public boolean enableChest = true;
    public boolean spawnInCreative = true;
    public boolean spawnOnce = true;
    public boolean overwriteStats = false;
    public float speed = 10.0f;
    public float jump = 3.0f;
    public int health = 20;
    public String spawnType = "HORSE";
    public HorseMarkingsConfig markings = HorseMarkingsConfig.DEFAULT;
    public HorseVariantConfig variant = HorseVariantConfig.DEFAULT;
    public HorseArmorConfig armor = HorseArmorConfig.NONE;

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
}