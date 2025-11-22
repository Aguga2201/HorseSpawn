package com.aguga.horsespawn.main;

import com.aguga.horsespawn.main.config.ConfigManager;
import com.aguga.horsespawn.main.config.HorseSpawnConfig;
import com.aguga.horsespawn.main.util.IPlayerDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.entity.EquipmentSlot;

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

        if (!shouldSpawn(player, minecraftServer.getDefaultGameMode())) {
            return;
        }

        Entity rawEntity = Registries.ENTITY_TYPE.get(Identifier.of("minecraft", CONFIG.spawnType.toLowerCase())).create(serverWorld /*? >=1.21.4 {*/, SpawnReason.EVENT /*?}*/);
        if (!(rawEntity instanceof LivingEntity entity)) {
            return;
        }

        applyAttributes(entity);
        equipSaddle(entity);
        setTamed(entity);
        entity.setPosition(getEntityCoordinates(player.getBlockX(), player.getBlockZ(), serverWorld));
        serverWorld.spawnEntity(entity);
	}

    private boolean shouldSpawn(PlayerEntity player, GameMode gameMode) {
        if (!CONFIG.spawnInCreative && gameMode == GameMode.CREATIVE) {
            return false;
        }

        IPlayerDataSaver playerDataSaver = (IPlayerDataSaver) player;
        if (CONFIG.spawnOnce && playerDataSaver.getHasSpawnedHorse()) {
            return false;
        }
        playerDataSaver.setHasSpawnedHorse(true);

        return true;
    }

    private void equipSaddle(LivingEntity entity) {
        if (entity instanceof AbstractHorseEntity horseEntity && CONFIG.enableSaddle) {
            //? if >=1.21.10 {
            horseEntity.equipStack(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            //?} else if >= 1.21.1 {
            /*horseEntity.saddle(new ItemStack(Items.SADDLE), SoundCategory.NEUTRAL);
            *///?} else {
            /*horseEntity.saddle(SoundCategory.NEUTRAL);
             *///?}
        }
        if (entity instanceof AbstractDonkeyEntity donkeyEntity && CONFIG.enableChest) {
            donkeyEntity.setHasChest(true);
        }
    }

    private void applyAttributes(LivingEntity entity) {
        if (entity instanceof AbstractHorseEntity horseEntity) {
            World world = entity.getEntityWorld();
            if (world instanceof ServerWorld serverWorld) {
                horseEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(horseEntity.getBlockPos()), SpawnReason.MOB_SUMMONED, null /*? <=1.20.4 {*//*, null *//*?}*/);
            }
        }

        if (!CONFIG.overwriteStats) {
            return;
        }

        //? if >=1.21.4 {
        if (entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED) != null) {
            entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
        }
        if (entity.getAttributeInstance(EntityAttributes.JUMP_STRENGTH) != null) {
            entity.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
        }
        if (entity.getAttributeInstance(EntityAttributes.MAX_HEALTH) != null) {
            entity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(CONFIG.health);
        }
        //?} else if >= 1.21.1 {
        /*if (entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) != null) {
            entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
        }
        if (entity.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH) != null) {
            entity.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
        }
        if (entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH) != null) {
            entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(CONFIG.health);
        }
        *///?} else {
        
        /*if (entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) != null) {
            entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
        }
        if (entity.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH) != null) {
            entity.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
        }
        if (entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH) != null) {
            entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(CONFIG.health);
        }
         *///?}
    }

    private void setTamed(LivingEntity entity) {
        if (entity instanceof AbstractHorseEntity horseEntity) {
            horseEntity.setTame(true);
        }
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
        double tol = 0.0001;
        double low = 0.0;
        double high = 1.0;

        int expandLimit = 50;
        int expands = 0;
        while (jumpStrengthToJumpHeight(high) < height && expands++ < expandLimit) {
            high *= 2;
        }

        for (int i = 0; i < 20; i++) {
            double mid = (low + high) / 2.0;
            double midHeight = jumpStrengthToJumpHeight(mid);
            if (Math.abs(midHeight - height) <= tol) {
                return mid;
            }
            if (midHeight < height) {
                low = mid;
            } else {
                high = mid;
            }
        }

        return (low + high) / 2.0;
    }

	public Vec3d getEntityCoordinates(int playerX, int playerZ, World world) {
		Random random = new Random();
		int[][] offsets = {
				{8, 0}, {6, 6}, {0, 8}, {-6, 6},
				{-8, 0}, {-6, -6}, {0, -8}, {6, -6}
		};
		int[] selectedOffset = offsets[random.nextInt(offsets.length)];
		int newX = playerX + selectedOffset[0];
		int newZ = playerZ + selectedOffset[1];
        int chunkX = newX >> 4;
        int chunkZ = newZ >> 4;
        world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
		int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, newX, newZ);
		return new Vec3d(newX, topY, newZ);
	}
}