package com.aguga.horsespawn.main.config;

import com.aguga.horsespawn.main.HorseSpawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

record Config<T>(Class<T> clazz, Consumer<T> apply, String filename) {
    void refresh() {
        apply.accept(ConfigLoader.load(clazz, filename));
    }
}

public class ConfigManager {
    private static final ArrayList<Config<?>> configs = new ArrayList<>();

    public static <T> T register(Class<T> clazz, String filename, Consumer<T> apply) {
        Optional.ofNullable(HorseSpawn.LOGGER)
                .ifPresent(l -> l.trace("register config manager {}", filename));

        configs.add(new Config<>(clazz, apply, filename));
        return ConfigLoader.load(clazz, filename);
    }

    public static void unregister(Class<?> clazz) {
        configs.removeIf(conf -> conf.clazz().equals(clazz));
    }

    public static void refresh(Class<?> clazz) {
        configs.stream()
                .filter(conf -> conf.clazz().equals(clazz))
                .forEach(Config::refresh);
    }

    public static void refreshAll() {
        configs.forEach(Config::refresh);
    }

    public static List<Class<?>> getAllRegistered() {
        return configs.stream()
                .map(Config::clazz)
                .collect(Collectors.toList());
    }
}