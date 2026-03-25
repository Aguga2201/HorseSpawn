package com.aguga.horsespawn.client;

//? if !=26.1 {
/*import com.aguga.horsespawn.main.HorseSpawn;
import com.aguga.horsespawn.main.config.ConfigLoader;
import com.aguga.horsespawn.main.config.HorseSpawnConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YaclConfigScreen {
    public static Screen create(Screen parent) {
        HorseSpawnConfig config = HorseSpawn.CONFIG;

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Horse Spawn Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("General Settings"))
                        .tooltip(Component.literal("General spawn configuration"))

                        // Equipment Options Group
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Equipment"))
                                .description(OptionDescription.of(Component.literal("Configure what equipment the spawned companion will have")))

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable Saddle"))
                                        .description(OptionDescription.of(Component.literal("Spawn the companion with a saddle equipped")))
                                        .binding(
                                                true,
                                                () -> config.enableSaddle,
                                                val -> config.enableSaddle = val
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable Chest"))
                                        .description(OptionDescription.of(Component.literal("Spawn the companion with a chest (for donkeys)")))
                                        .binding(
                                                true,
                                                () -> config.enableChest,
                                                val -> config.enableChest = val
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .build())

                        // Spawn Behavior Group
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Spawn Behavior"))
                                .description(OptionDescription.of(Component.literal("Configure when companions should spawn")))

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Spawn in Creative"))
                                        .description(OptionDescription.of(Component.literal("Spawn a companion in creative worlds")))
                                        .binding(
                                                true,
                                                () -> config.spawnInCreative,
                                                val -> config.spawnInCreative = val
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Spawn Once"))
                                        .description(OptionDescription.of(Component.literal("Only spawn a companion once per player")))
                                        .binding(
                                                true,
                                                () -> config.spawnOnce,
                                                val -> config.spawnOnce = val
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .build())

                        // Spawn Type Group
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Spawn Type"))
                                .description(OptionDescription.of(Component.literal("Choose which companion to spawn")))

                                .option(Option.<String>createBuilder()
                                        .name(Component.literal("Companion Type"))
                                        .description(OptionDescription.of(Component.literal("Select the type of companion to spawn for players")))
                                        .binding(
                                                "HORSE",
                                                () -> config.spawnType,
                                                val -> config.spawnType = val.toUpperCase()
                                        )
                                        .controller(opt -> CyclingListControllerBuilder.create(opt)
                                                .values(java.util.List.of("HORSE", "DONKEY", "MULE"))
                                                .formatValue(val -> Component.literal(val)))
                                        .build())

                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Stats"))
                        .tooltip(Component.literal("Configure companion statistics"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Mount Statistics"))
                                .description(OptionDescription.of(Component.literal("Customize the stats of spawned companions")))

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Overwrite Stats"))
                                        .description(OptionDescription.of(Component.literal("Override default companion stats with custom values below")))
                                        .binding(
                                                false,
                                                () -> config.overwriteStats,
                                                val -> config.overwriteStats = val
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .option(Option.<Float>createBuilder()
                                        .name(Component.literal("Speed"))
                                        .description(OptionDescription.of(Component.literal("Companion movement speed (blocks per second)\nAverage horse: ~9.5, Max vanilla: ~14.5")))
                                        .binding(
                                                10.0f,
                                                () -> config.speed,
                                                val -> config.speed = val
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(4.0f, 20.0f)
                                                .step(0.1f)
                                                .formatValue(val -> Component.literal(String.format("%.1f b/s", val))))
                                        .build())

                                .option(Option.<Float>createBuilder()
                                        .name(Component.literal("Jump Height"))
                                        .description(OptionDescription.of(Component.literal("Companion jump height (blocks)\nAverage horse: ~3, Max vanilla: ~5.9")))
                                        .binding(
                                                3.0f,
                                                () -> config.jump,
                                                val -> config.jump = val
                                        )
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(1.0f, 10.0f)
                                                .step(0.1f)
                                                .formatValue(val -> Component.literal(String.format("%.1f blocks", val))))
                                        .build())

                                .option(Option.<Integer>createBuilder()
                                        .name(Component.literal("Health"))
                                        .description(OptionDescription.of(Component.literal("Companion health (hearts)\nAverage horse: ~22.5 (11.25 ♥), Max vanilla: 30 (15 ♥)")))
                                        .binding(
                                                20,
                                                () -> config.health,
                                                val -> config.health = val
                                        )
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                                .range(10, 60)
                                                .step(2)
                                                .formatValue(val -> Component.literal(String.format("%d HP (%.1f ♥)", val, val / 2.0f))))
                                        .build())

                                .build())

                        .build())

                .save(() -> {
                    ConfigLoader.save(config, HorseSpawn.MOD_ID + ".json");
                    HorseSpawn.LOGGER.info("Horse Spawn config saved!");
                })
                .build()
                .generateScreen(parent);
    }
}
*///?}