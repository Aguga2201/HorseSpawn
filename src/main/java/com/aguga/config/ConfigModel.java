package com.aguga.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;


@SuppressWarnings("unused")
@Modmenu(modId = "horse-spawn")
@Config(name = "horse-spawn-config", wrapperName = "HorseSpawnConfig")
public class ConfigModel
{
    public Choices Mob = Choices.HORSE;
    public enum Choices
    {
        HORSE, DONKEY, DOLPHIN
    }

    public boolean saddle = true;
    public boolean chest = true;
}
