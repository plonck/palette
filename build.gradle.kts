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

loom {
  accessWidenerPath = file("src/main/resources/palette.accesswidener")
}
