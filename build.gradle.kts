plugins {
    id("net.fabricmc.fabric-loom")
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

    accessWidenerPath = rootProject.file("src/main/resources/accesswideners/26.1.accesswidener")
}

val mcVersion = property("minecraft_version") as String
val loaderVersion = property("loader_version") as String
val mcDependency = property("minecraft_dependency") as String
val fabricApiVersion = property("fabric_api_version") as String
val yaclVersion = property("yacl_version") as String
val modmenuVersion = property("modmenu_version") as String

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    implementation("net.fabricmc:fabric-loader:${loaderVersion}")

    implementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
    implementation("dev.isxander:yet-another-config-lib:${yaclVersion}")
    implementation("com.terraformersmc:modmenu:${modmenuVersion}")
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "minecraft" to mcVersion,
        "minecraft_dependency" to mcDependency,
        "yacl_version" to yaclVersion,
        "aw_file" to "26.1.accesswidener"
    )
    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }

    destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
}