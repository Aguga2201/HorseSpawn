package com.aguga.horsespawn.mixin;

import com.aguga.horsespawn.util.IPlayerDataSaver;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.10 {
/*import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.entity.player.PlayerEntity;
*///?} else {
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

//? if >=1.21.10 {
/*@Mixin(PlayerEntity.class)
*///?} else {
@Mixin(Entity.class)
//?}
public abstract class PlayerDataSaverMixin implements IPlayerDataSaver
{
    private boolean hasSpawnedHorse;
    @Override
    public boolean getHasSpawnedHorse()
    {
        return hasSpawnedHorse;
    }

    @Override
    public void setHasSpawnedHorse(boolean value) {
        this.hasSpawnedHorse = value;
    }

    //? if >=1.21.10 {
    /*@Inject(method = "writeCustomData", at = @At("HEAD"))
    private void injectWriteMethod(WriteView view, CallbackInfo ci) {
        view.putBoolean("hasSpawnedHorse", hasSpawnedHorse);
    }

    @Inject(method = "readCustomData", at = @At("HEAD"))
    private void injectReadMethod(ReadView view, CallbackInfo ci) {
        hasSpawnedHorse = view.getBoolean("hasSpawnedHorse", false);
    }
    *///?} else {
    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable info)
    {
        nbt.putBoolean("has-spawned-horse", hasSpawnedHorse);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    private void injectReadMethod(NbtCompound nbt, CallbackInfo info)
    {
        hasSpawnedHorse = nbt.getBoolean("has-spawned-horse");
    }
    //?}
}