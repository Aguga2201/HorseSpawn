pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom_version")
        id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version providers.gradleProperty("stonecutter_version")
}

stonecutter {
    create(rootProject) {
        versions("1.20.1", "1.21.1", "1.21.11").buildscript("build-obf.gradle.kts")
        versions("26.1", "26.2")
    }
}

rootProject.name = "horsespawn"