rootProject.name = "zems.playground"

pluginManagement {
  repositories {
    mavenLocal()
    maven("https://repo.spring.io/milestone")
    mavenCentral()
    gradlePluginPortal()
  }
}

include("app")
include("config")
include("binaries")
