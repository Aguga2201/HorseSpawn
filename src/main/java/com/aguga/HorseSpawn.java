package com.aguga;

import com.aguga.config.ConfigManager;
import com.aguga.config.HorseSpawnConfig;
import com.aguga.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class HorseSpawn implements ModInitializer {
    public static final String MOD_ID = "horse-spawn";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static HorseSpawnConfig CONFIG = ConfigManager.register(
            HorseSpawnConfig.class,
            MOD_ID + ".json",
            newConf -> CONFIG = newConf
    );

	@Override
	public void onInitialize() {
		LOGGER.info("Successfully initialized Horse Spawn!");
		ServerPlayConnectionEvents.JOIN.register(this::spawnHorseForPlayer);

	}

	private void spawnHorseForPlayer(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        PlayerEntity player = serverPlayNetworkHandler.getPlayer();
		ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();

		IEntityDataSaver iPlayer = (IEntityDataSaver) player;
		boolean hasSpawnedHorse = iPlayer.getPersistentData();

		if(!CONFIG.spawnInCreative && player.getGameMode() == GameMode.CREATIVE || CONFIG.spawnOnce && hasSpawnedHorse) {
            return;
        }

		if(CONFIG.spawnType == HorseSpawnConfig.SpawnType.HORSE) {

			HorseEntity horseEntity = EntityType.HORSE.create(serverWorld, SpawnReason.EVENT);

			if(horseEntity == null) {
                return;
            }

			horseEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
			if(CONFIG.overwriteStats) {
				horseEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
				horseEntity.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
				horseEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(CONFIG.health);
                LOGGER.info(jumpHeightToJumpStrength(CONFIG.jump) + "");
			}

            horseEntity.setTame(true);

			int[] entityCoordinates = getEntityCoordinates(player.getBlockX(), player.getBlockZ(), serverWorld);
			horseEntity.setPosition(entityCoordinates[0], entityCoordinates[1], entityCoordinates[2]);
			serverWorld.spawnEntity(horseEntity);

            if(CONFIG.enableSaddle) {
                horseEntity.equipStack(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            }

			hasSpawnedHorse = true;
		}
		else if (CONFIG.spawnType == HorseSpawnConfig.SpawnType.DONKEY) {
			DonkeyEntity donkeyEntity = EntityType.DONKEY.create(serverWorld, SpawnReason.EVENT);

			if (donkeyEntity == null) {
                return;
            }

			donkeyEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
			if(CONFIG.overwriteStats) {
				donkeyEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
				donkeyEntity.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
				donkeyEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(CONFIG.health);
			}

			donkeyEntity.setTame(true);

			if(CONFIG.enableChest) {
                donkeyEntity.setHasChest(true);
            }
			int[] entityCoordinates = getEntityCoordinates(player.getBlockX(), player.getBlockZ(), serverWorld);
			donkeyEntity.setPosition(entityCoordinates[0], entityCoordinates[1], entityCoordinates[2]);
			serverWorld.spawnEntity(donkeyEntity);

            if(CONFIG.enableSaddle) {
                donkeyEntity.equipStack(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            }

			hasSpawnedHorse = true;
		} else if (CONFIG.spawnType == HorseSpawnConfig.SpawnType.DOLPHIN) {
			DolphinEntity dolphinEntity = EntityType.DOLPHIN.create(serverWorld, SpawnReason.EVENT);

			if (dolphinEntity == null) {
                return;
            }

			dolphinEntity.setPosition(player.getX(), player.getY(), player.getZ());
			serverWorld.spawnEntity(dolphinEntity);
		}

        iPlayer.setHasSpawnedHorse(hasSpawnedHorse);
	}

	public double blocksPerSecToSpeed(double blocksPerSec) {
		return blocksPerSec / 42.157796;
	}

	public double jumpStrengthToJumpHeight(double strength) {
		double height = 0;
		while(strength > 0) {
			height += strength;
			strength = (strength - .08) * .98 * .98;
		}
		return height;
	}

	public double jumpHeightToJumpStrength(double height) {
		double guess = 0.35;
		double deviation = height - jumpStrengthToJumpHeight(guess);

		while(deviation > 0.005) {
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

	public int[] getEntityCoordinates(int playerX, int playerZ, World world) {
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