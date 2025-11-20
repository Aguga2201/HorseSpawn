package com.aguga.config;

public class HorseSpawnConfig {
    public enum SpawnType {
        HORSE,
        DONKEY,
        DOLPHIN
    }

    public boolean enableSaddle = true;
    public boolean enableChest = false;
    public boolean spawnInCreative = true;
    public boolean enableRandomAttributes = true;
    public boolean spawnOnce = true;
    public float speed = 10.0f;
    public float jump = 3.0f;
    public int health = 20;
    public SpawnType spawnType = SpawnType.HORSE;
}
