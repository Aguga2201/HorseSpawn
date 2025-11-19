package com.aguga.util;

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver
{
    boolean getPersistentData();
    void setHasSpawnedHorse(boolean value);
}
