import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
  alias(libs.plugins.fabric.loom)
  alias(libs.plugins.spotless)
}

group = "org.plonck"
version = libs.versions.minecraft.get()

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
    "version_minecraft" to libs.versions.minecraft.get(),
    "version_fabricloader" to libs.versions.fabric.loader.get(),
  )

  filteringCharset = "UTF-8"

  filesMatching("fabric.mod.json") {
    expand(props)
  }
}

tasks.register("showMinecraftVersion") {
  group = "palette"

  val minecraftVersion = libs.versions.minecraft.get()

  doLast {
    println(minecraftVersion)
  }
}

configure<SpotlessExtension> {
  java {
    target("src/**/*.java")
    licenseHeaderFile(rootProject.file("HEADER"))
  }
}
