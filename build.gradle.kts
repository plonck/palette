plugins {
  alias(libs.plugins.fabric.loom)
}

group = "org.plonck"
version = file("VERSION").readText().trim()

dependencies {
  minecraft(libs.minecraft)
  mappings(loom.officialMojangMappings())
  modImplementation(libs.fabric.loader)
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

loom {
  accessWidenerPath = file("src/main/resources/palette.accesswidener")
}

tasks.processResources {
  val props = mapOf(
    "version" to project.version,
    "minecraft_version" to libs.versions.minecraft.get(),
    "fabric_loader_version" to libs.versions.fabric.loader.get(),
  )

  filteringCharset = "UTF-8"

  filesMatching("fabric.mod.json") {
    expand(props)
  }
}
