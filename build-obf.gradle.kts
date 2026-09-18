plugins {
    id("net.fabricmc.fabric-loom-remap")
}

val mcVersion = property("minecraft_version") as String
val loaderVersion = property("loader_version") as String
val mcDependency = property("minecraft_dependency") as String
val fabricApiVersion = property("fabric_api_version") as String
val yaclVersion = property("yacl_version") as String
val modmenuVersion = property("modmenu_version") as String
val accessWidener = when {
    sc.current.parsed >= "1.21.11" -> "1.21.11.accesswidener"
    else -> "1.21.10.accesswidener"
}

repositories {
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
    maven("https://maven.terraformersmc.com") {
        name = "Terraformers"
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("horsespawn") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }

    accessWidenerPath = rootProject.file("src/main/resources/accesswideners/${accessWidener}")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
    modImplementation("dev.isxander:yet-another-config-lib:${yaclVersion}")
    modImplementation("com.terraformersmc:modmenu:${modmenuVersion}")
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "minecraft" to mcVersion,
        "minecraft_dependency" to mcDependency,
        "yacl_version" to yaclVersion,
        "aw_file" to accessWidener,
    )
    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }
}