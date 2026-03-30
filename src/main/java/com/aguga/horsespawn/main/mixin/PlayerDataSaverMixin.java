package com.aguga.horsespawn.main.mixin;

import com.aguga.horsespawn.main.util.IPlayerDataSaver;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.10 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
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
@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
private void injectWriteMethod(ValueOutput view, CallbackInfo ci) {
    view.putBoolean("hasSpawnedHorse", hasSpawnedHorse);
}

@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
private void injectReadMethod(ValueInput view, CallbackInfo ci) {
    hasSpawnedHorse = view.getBooleanOr("hasSpawnedHorse", false);
}
//?} else {
    /*@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void injectWriteMethod(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("hasSpawnedHorse", hasSpawnedHorse);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void injectReadMethod(CompoundTag nbt, CallbackInfo ci) {
        hasSpawnedHorse = nbt.getBoolean("hasSpawnedHorse");
    }
    *///?}
}