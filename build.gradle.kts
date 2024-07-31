import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

group = "se.wilmer"
version = "1.0.0-SNAPSHOT"


java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}


dependencies {
    implementation("org.spongepowered:configurate-gson:4.2.0-SNAPSHOT")
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}


tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
    main = "se.wilmer.tireswing.TireSwing"
    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
    apiVersion = "1.21"
}