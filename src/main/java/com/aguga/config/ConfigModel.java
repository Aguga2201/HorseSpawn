package com.aguga.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.SectionHeader;


@SuppressWarnings("unused")
@Modmenu(modId = "horse-spawn")
@Config(name = "horse-spawn-config", wrapperName = "HorseSpawnConfig")
public class ConfigModel
{
    @SectionHeader("General")
    public Choices Mob = Choices.HORSE;
    public enum Choices
    {
        HORSE, DONKEY, DOLPHIN
    }
    public boolean saddle = true;
    public boolean chest = true;
    public boolean spawnInCreative = true;

    @SectionHeader("Attributes")
    public boolean randomAttributes = true;
    @RangeConstraint(min = 1, max = 40)
    public int health = 24;
    @RangeConstraint(min = 0.1, max = 24)
    public double speed = 8;
    @RangeConstraint(min = 0.1, max = 16.5)
    public double jump = 3;
}
