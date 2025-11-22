package com.aguga.horsespawn.main.config;

import com.aguga.horsespawn.main.HorseSpawn;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    private static File getFile(String filename) {
        return FabricLoader.getInstance().getConfigDir().resolve(filename).toFile();
    }

    public static <T> @NotNull T load(Class<T> clazz, String filename) {
        Optional.ofNullable(HorseSpawn.LOGGER)
                .ifPresent(l -> l.trace("loading config {}", filename));

        var file = getFile(filename);
        T config = null;

        if (file.exists()) {
            try (var reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file)
                    )
            )) {
                config = GSON.fromJson(reader, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (config == null) {
            try {
                config = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        ConfigLoader.save(config, filename);
        return config;
    }

    public static <T> void save(T config, String filename) {
        Optional.ofNullable(HorseSpawn.LOGGER)
                .ifPresent(l -> l.trace("saving config {}", filename));

        File file = getFile(filename);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                System.err.println("Failed to create a directory for " + file);
            }
        }

        try (var writer = new OutputStreamWriter(
                new FileOutputStream(file)
        )) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
