plugins {
  java
  kotlin("jvm") version "1.5.0"
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "realjenius.ketris"
version = "1.0-SNAPSHOT"

tasks.jar {
  manifest {
    attributes("Main-Class" to "realjenius.ketris.KetrisKt")
  }
}
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.0.2")
}
