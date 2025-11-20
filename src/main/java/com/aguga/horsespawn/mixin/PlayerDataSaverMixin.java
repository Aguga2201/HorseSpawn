package com.aguga.horsespawn.mixin;

import com.aguga.horsespawn.util.IPlayerDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
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

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void injectWriteMethod(WriteView view, CallbackInfo ci)
    {
        view.putBoolean("hasSpawnedHorse", hasSpawnedHorse);
    }

    @Inject(method = "readCustomData", at = @At("HEAD"))
    private void injectReadMethod(ReadView view, CallbackInfo ci)
    {
        hasSpawnedHorse = view.getBoolean("hasSpawnedHorse", false);
    }
}
