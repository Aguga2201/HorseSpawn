package com.aguga.horsespawn.main;

import com.aguga.horsespawn.main.config.ConfigManager;
import com.aguga.horsespawn.main.config.HorseSpawnConfig;
import com.aguga.horsespawn.main.util.IPlayerDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
//? if >=26.1 {
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.*;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.*;
*///?}
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//? if < 1.21.10 {
/*import net.minecraft.sounds.SoundSource;
*///?}
import net.minecraft.world.entity.Entity;
//? if >=1.21.4 {
import net.minecraft.world.entity.EntitySpawnReason;
//?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
//? if <= 1.21.1 {
/*import net.minecraft.world.entity.MobSpawnType;
*///?}
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
//? if >= 1.21.1 {
import net.minecraft.world.level.chunk.status.ChunkStatus;
//?} else {
/*import net.minecraft.world.level.chunk.ChunkStatus;
*///?}
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
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

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            IPlayerDataSaver oldData = (IPlayerDataSaver) oldPlayer;
            IPlayerDataSaver newData = (IPlayerDataSaver) newPlayer;

            newData.setHasSpawnedHorse(oldData.getHasSpawnedHorse());
        });
    }

    private void spawnHorseForPlayer(ServerGamePacketListenerImpl serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        Player player = serverPlayNetworkHandler.getPlayer();
        ServerLevel serverWorld = (ServerLevel) player.level();

        if (!shouldSpawn(player, minecraftServer.getDefaultGameType())) {
            return;
        }

        //? if >=26.1 {
        Entity rawEntity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("minecraft", CONFIG.spawnType.toLowerCase())).create(serverWorld, EntitySpawnReason.EVENT);
        //?} else if >= 1.21.4 {
        /*Entity rawEntity = BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", CONFIG.spawnType.toLowerCase())).create(serverWorld, EntitySpawnReason.EVENT);
        *///?} else if >= 1.21.1 {
        /*Entity rawEntity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath("minecraft", CONFIG.spawnType.toLowerCase())).create(serverWorld);
        *///?} else {
        /*Entity rawEntity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryBuild("minecraft", CONFIG.spawnType.toLowerCase())).create(serverWorld);
        *///?}
        if (!(rawEntity instanceof LivingEntity entity)) {
            return;
        }

        setAttributes(entity);
        setEquipment(entity);
        setVisuals(entity, player);
        setTamed(entity);
        entity.setPos(getEntityCoordinates(player.getBlockX(), player.getBlockZ(), serverWorld));
        serverWorld.addFreshEntity(entity);

        if (CONFIG.giveLead) {
            player.addItem(new ItemStack(Items.LEAD));
        }
    }

    private boolean shouldSpawn(Player player, GameType gameMode) {
        if (!CONFIG.spawnInCreative && gameMode == GameType.CREATIVE) {
            return false;
        }

        IPlayerDataSaver playerDataSaver = (IPlayerDataSaver) player;
        if (CONFIG.spawnOnce && playerDataSaver.getHasSpawnedHorse()) {
            return false;
        }
        playerDataSaver.setHasSpawnedHorse(true);

        return true;
    }

    private void setEquipment(LivingEntity entity) {
        if (entity instanceof AbstractHorse horseEntity) {
            if (CONFIG.enableSaddle) {
                //? if >=1.21.10 {
                horseEntity.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
                //?} else if >= 1.21.1 {
                /*horseEntity.equipSaddle(new ItemStack(Items.SADDLE), SoundSource.NEUTRAL);
                 *///?} else {
                /*horseEntity.equipSaddle(SoundSource.NEUTRAL);
                 *///?}
            }
        }
        if (entity instanceof Horse horseEntity) {
            Item armorItem = CONFIG.armor.toItem();
            if (armorItem != null) {
                //? if >= 1.21.1 {
                horseEntity.setItemSlot(EquipmentSlot.BODY, new ItemStack(armorItem));
                //?} else {
                /*horseEntity.inventory.setItem(1, new ItemStack(armorItem));
                 *///?}
            }
        }
        if (entity instanceof Llama llamaEntity) {
            Item carpetItem = CONFIG.carpet.toItem();
            if(carpetItem != null) {
                //? if >= 1.21.1 {
                llamaEntity.setItemSlot(EquipmentSlot.BODY, new ItemStack(carpetItem));
                //?} else {
                /*llamaEntity.inventory.setItem(1, new ItemStack(carpetItem));
                 *///?}
            }
        }
        if (entity instanceof AbstractChestedHorse chestedEntity && CONFIG.enableChest) {
            chestedEntity.setChest(true);
            chestedEntity.createInventory();
        }
    }

    private void setAttributes(LivingEntity entity) {
        if (entity instanceof AbstractHorse horseEntity) {
            Level world = entity.level();
            if (world instanceof ServerLevel serverWorld) {
                horseEntity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(horseEntity.blockPosition()), /*? >=1.21.4 {*/EntitySpawnReason /*?}*/ /*? <1.21.4 {*//*MobSpawnType *//*?}*/ .MOB_SUMMONED, null /*? <=1.20.4 {*//*, null *//*?}*/);
            }
        }

        if (!CONFIG.overwriteStats) {
            return;
        }

        //? if >=1.21.4 {
        if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
        }
        if (entity.getAttribute(Attributes.JUMP_STRENGTH) != null) {
            entity.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
        }
        if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
            entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(CONFIG.health);
        }
        //?} else {
        /*if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(blocksPerSecToSpeed(CONFIG.speed));
        }
        if (entity.getAttribute(Attributes.JUMP_STRENGTH) != null) {
            entity.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpHeightToJumpStrength(CONFIG.jump));
        }
        if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
            entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(CONFIG.health);
        }
         *///?}
    }

    private void setVisuals(LivingEntity entity, Player player) {
        if (entity instanceof Horse horseEntity) {
            Variant variant = horseEntity.getVariant();
            if (CONFIG.variant != HorseSpawnConfig.HorseVariantConfig.DEFAULT) {
                variant = Variant.valueOf(CONFIG.variant.name());
            }

            Markings markings = horseEntity.getMarkings();
            if (CONFIG.markings != HorseSpawnConfig.HorseMarkingsConfig.DEFAULT) {
                markings = Markings.valueOf(CONFIG.markings.name());
            }

            horseEntity.setVariantAndMarkings(variant, markings);
        }

        if (CONFIG.defaultName && CONFIG.customName.isEmpty()) {
            //? if >= 26.1 {
            entity.setCustomName(Component.literal(player.getPlainTextName() + "'s " + entity.getName().getString()));
            //?} else {
            /*entity.setCustomName(Component.literal(player.getDisplayName().getString() + "'s " + entity.getName().getString()));
            *///?}
        }
        if (!CONFIG.customName.isEmpty()) {
            entity.setCustomName(Component.literal(CONFIG.customName));
        }
    }

    private void setTamed(LivingEntity entity) {
        if (entity instanceof AbstractHorse horseEntity) {
            horseEntity.setTamed(true);
        }
    }

    private double blocksPerSecToSpeed(double blocksPerSec) {
        return blocksPerSec / 42.157796;
    }

    private double jumpStrengthToJumpHeight(double strength) {
        double height = 0;
        while(strength > 0) {
            height += strength;
            strength = (strength - .08) * .98 * .98;
        }
        return height;
    }

    private double jumpHeightToJumpStrength(double height) {
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

    private Vec3 getEntityCoordinates(int playerX, int playerZ, Level world) {
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
        int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, newX, newZ);
        return new Vec3(newX, topY, newZ);
    }
}