package com.aguga;

import com.aguga.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

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
			horseEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null, null);

			horseEntity.setTame(true);
			horseEntity.saddle(SoundCategory.NEUTRAL);
			horseEntity.setPosition(player.getX(), player.getY(), player.getZ());
			horseEntity.setVelocity(2, 0, 0);
			serverWorld.spawnEntity(horseEntity);

			isNotNew = true;
		}

		nbt.putBoolean("isNotNew", isNotNew);
	}
}