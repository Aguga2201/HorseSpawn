package com.aguga.horsespawn.config;

public class HorseSpawnConfig {
    public enum SpawnType {
        HORSE,
        DONKEY,
        DOLPHIN
    }

    public boolean enableSaddle = true;
    public boolean enableChest = true;
    public boolean spawnInCreative = true;
    public boolean spawnOnce = true;
    public boolean overwriteStats = false;
    public float speed = 10.0f;
    public float jump = 3.0f;
    public int health = 20;
    public String spawnType = "HORSE";
}
