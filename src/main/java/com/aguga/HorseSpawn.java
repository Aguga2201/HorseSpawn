package com.aguga;

import com.aguga.config.ConfigModel;
import com.aguga.config.HorseSpawnConfig;
import com.aguga.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
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
	public static final HorseSpawnConfig CONFIG = HorseSpawnConfig.createAndLoad();
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

		IEntityDataSaver iPlayer = (IEntityDataSaver) player;
		NbtCompound nbt = iPlayer.getPersistentData();
		boolean isNotNew = nbt.getBoolean("isNotNew");

		if(CONFIG.Mob() == ConfigModel.Choices.HORSE)
		{
			HorseEntity horseEntity = EntityType.HORSE.create(serverWorld);
			if (horseEntity != null && !isNotNew)
			{
				horseEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null, null);

				horseEntity.setTame(true);
				if(CONFIG.saddle()) {horseEntity.saddle(SoundCategory.NEUTRAL);}
				horseEntity.setPosition(player.getX(), player.getY(), player.getZ());
				horseEntity.setVelocity(2, 0, 0);
				serverWorld.spawnEntity(horseEntity);

				isNotNew = true;
				nbt.putBoolean("isNotNew", isNotNew);
			}
		} else if (CONFIG.Mob() == ConfigModel.Choices.DONKEY)
		{
			DonkeyEntity donkeyEntity = EntityType.DONKEY.create(serverWorld);
			if (donkeyEntity != null && !isNotNew)
			{
				donkeyEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null, null);

				donkeyEntity.setTame(true);
				if(CONFIG.saddle()) {donkeyEntity.saddle(SoundCategory.NEUTRAL);}
				if(CONFIG.chest()) {donkeyEntity.setHasChest(true);}
				donkeyEntity.setPosition(player.getX(), player.getY(), player.getZ());
				donkeyEntity.setVelocity(2, 0, 0);
				serverWorld.spawnEntity(donkeyEntity);

				isNotNew = true;
				nbt.putBoolean("isNotNew", isNotNew);
			}
		} else if (CONFIG.Mob() == ConfigModel.Choices.DOLPHIN)
		{
			DolphinEntity dolphinEntity = EntityType.DOLPHIN.create(serverWorld);
			if (dolphinEntity != null && !isNotNew)
			{
				dolphinEntity.setPosition(player.getX(), player.getY(), player.getZ());
				serverWorld.spawnEntity(dolphinEntity);
			}
		}


	}
}