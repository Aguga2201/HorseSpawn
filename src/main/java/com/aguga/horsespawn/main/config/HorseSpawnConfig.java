package com.aguga.horsespawn.main.config;

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

    public enum HorseMarkingsConfig {
        DEFAULT, NONE, WHITE, WHITE_FIELD, WHITE_DOTS, BLACK_DOTS
    }

    public enum HorseVariantConfig {
        DEFAULT, WHITE, CREAMY, CHESTNUT, BROWN, BLACK, GRAY, DARK_BROWN
    }
}