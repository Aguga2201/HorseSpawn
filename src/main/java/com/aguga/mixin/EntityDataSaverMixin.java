package com.aguga.mixin;

import com.aguga.util.IEntityDataSaver;
import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDataSaverMixin implements IEntityDataSaver
{
    private boolean hasSpawnedHorse;
    @Override
    public boolean getPersistentData()
    {
        return hasSpawnedHorse;
    }

    @Override
    public void setHasSpawnedHorse(boolean value) {
        this.hasSpawnedHorse = value;
    }

    /*
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData()
    {
        if(this.persistentData == null)
        {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

     */

    @Inject(method = "writeData", at = @At("HEAD"))
    private void injectWriteMethod(WriteView view, CallbackInfo ci)
    {
        view.putBoolean("hasSpawnedHorse", hasSpawnedHorse);
        /*
        if(persistentData != null)
        {
            nbt.put("horse-spawn.data", persistentData);
        }
         */
    }

    @Inject(method = "readData", at = @At("HEAD"))
    private void injectReadMethod(ReadView view, CallbackInfo ci)
    {
        hasSpawnedHorse = view.getBoolean("hasSpawnedHorse", false);
        /*
        if(nbt.contains("horse-spawn.data", 10))
        {
            persistentData = nbt.getCompound("horse-spawn.data");
        }

         */
    }
}
