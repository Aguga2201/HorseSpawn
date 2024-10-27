package com.aguga;

import com.aguga.config.ConfigModel;
import com.aguga.config.HorseSpawnConfig;
import com.aguga.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

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
		GameMode defaultGameMode = minecraftServer.getDefaultGameMode();

		IEntityDataSaver iPlayer = (IEntityDataSaver) player;
		NbtCompound nbt = iPlayer.getPersistentData();
		boolean isNotNew = nbt.getBoolean("isNotNew");

		if(!CONFIG.spawnInCreative() && defaultGameMode == GameMode.CREATIVE)
			return;

		if(CONFIG.Mob() == ConfigModel.Choices.HORSE)
		{
			HorseEntity horseEntity = EntityType.HORSE.create(serverWorld, SpawnReason.EVENT);

			if(horseEntity == null || isNotNew)
				return;

			horseEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
			if(!CONFIG.randomAttributes())
			{
				horseEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed()));
				horseEntity.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump()));
				horseEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(CONFIG.health());
			}

			horseEntity.setTame(true);
			if(CONFIG.saddle())
				horseEntity.saddle(new ItemStack(Items.SADDLE), SoundCategory.NEUTRAL);
			int[] horseCoordinates = getEntityCoordinates(player.getBlockX(), player.getBlockZ(), serverWorld);
			LOGGER.info("Horse coordinates: " + horseCoordinates[0] + ", " + horseCoordinates[1] + ", " + horseCoordinates[2]);
			horseEntity.setPosition(horseCoordinates[0], horseCoordinates[1], horseCoordinates[2]);
			horseEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, Integer.MAX_VALUE, 0, false, false));
			serverWorld.spawnEntity(horseEntity);

			isNotNew = true;
			nbt.putBoolean("isNotNew", isNotNew);
		}
		else if (CONFIG.Mob() == ConfigModel.Choices.DONKEY)
		{
			DonkeyEntity donkeyEntity = EntityType.DONKEY.create(serverWorld, SpawnReason.EVENT);

			if (donkeyEntity == null || isNotNew)
				return;

			donkeyEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
			if(!CONFIG.randomAttributes())
			{
				donkeyEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed()));
				donkeyEntity.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump()));
				donkeyEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(CONFIG.health());
			}

			donkeyEntity.setTame(true);
			if(CONFIG.saddle())
				donkeyEntity.saddle(new ItemStack(Items.SADDLE), SoundCategory.NEUTRAL);
			if(CONFIG.chest())
				donkeyEntity.setHasChest(true);
			donkeyEntity.setPosition(player.getX(), player.getY(), player.getZ());
			donkeyEntity.setVelocity(2, 0, 0);
			serverWorld.spawnEntity(donkeyEntity);

			isNotNew = true;
			nbt.putBoolean("isNotNew", isNotNew);
		} else if (CONFIG.Mob() == ConfigModel.Choices.DOLPHIN)
		{
			DolphinEntity dolphinEntity = EntityType.DOLPHIN.create(serverWorld, SpawnReason.EVENT);

			if (dolphinEntity == null || isNotNew)
				return;

			dolphinEntity.setPosition(player.getX(), player.getY(), player.getZ());
			serverWorld.spawnEntity(dolphinEntity);
		}
	}

	public double blocksPerSecToSpeed(double blocksPerSec)
	{
		return blocksPerSec / 42.157796;
	}

	public double jumpStrengthToJumpHeight(double strength)
	{
		double height = 0;
		while(strength > 0) {
			height += strength;
			strength = (strength - .08) * .98 * .98;
		}
		return height;
	}

	public double jumpHeightToJumpStrength(double height)
	{
		double guess = 0.35;
		double deviation = height - jumpStrengthToJumpHeight(guess);

		while(deviation > 0.005)
		{
			if(deviation > 10)
				guess += 0.8;
			else if(deviation > 5)
				guess += 0.4;
			else if(deviation > 1)
				guess += 0.02;
			else if(deviation > 0.2)
				guess += 0.01;
			else if(deviation > 0)
				guess += 0.0005;
			deviation = height - jumpStrengthToJumpHeight(guess);
		}

		return guess;
	}

	public int[] getEntityCoordinates(int playerX, int playerZ, World world)
	{
		Random random = new Random();
		int[][] offsets = {
				{8, 0}, {6, 6}, {0, 8}, {-6, 6},
				{-8, 0}, {-6, -6}, {0, -8}, {6, -6}
		};
		int[] selectedOffset = offsets[random.nextInt(offsets.length)];
		int newX = playerX + selectedOffset[0];
		int newZ = playerZ + selectedOffset[1];
		int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, newX, newZ);
		return new int[]{newX, topY, newZ};
	}
}