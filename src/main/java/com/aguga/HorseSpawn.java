package com.aguga;

import com.aguga.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HorseSpawn implements ModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("horse-spawn");

	@Override
	public void onInitialize()
	{
		LOGGER.info("Successfully initialized Horse Spawn!");
		ServerPlayConnectionEvents.JOIN.register(this::spawnHorseForPlayer);
	}

	private void spawnHorseForPlayer(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer)
	{
		PlayerEntity player = serverPlayNetworkHandler.getPlayer();
		ServerWorld serverWorld = (ServerWorld) player.getWorld();
		HorseEntity horseEntity = EntityType.HORSE.create(serverWorld);

		IEntityDataSaver iPlayer = (IEntityDataSaver) player;
		NbtCompound nbt = iPlayer.getPersistentData();
		boolean isNotNew = nbt.getBoolean("isNotNew");

		if (horseEntity != null && !isNotNew)
		{
			horseEntity.setPosition(player.getX(), player.getY(), player.getZ());
			horseEntity.setTame(true);
			horseEntity.saddle(SoundCategory.NEUTRAL);
			horseEntity.setHealth(24);
			horseEntity.setVelocity(2, 0, 0);
			serverWorld.spawnEntity(horseEntity);

			isNotNew = true;
		}

		nbt.putBoolean("isNotNew", isNotNew);
	}
}