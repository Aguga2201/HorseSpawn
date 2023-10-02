package com.aguga.mixin;

import com.aguga.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDataSaverMixin implements IEntityDataSaver
{
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

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable info)
    {
        if(persistentData != null)
        {
            nbt.put("horse-spawn.data", persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void injectReadMethod(NbtCompound nbt, CallbackInfo info)
    {
        if(nbt.contains("horse-spawn.data", 10))
        {
            persistentData = nbt.getCompound("horse-spawn.data");
        }
    }
}
