plugins {
  java
  kotlin("jvm") version "1.8.10"
  id("com.github.johnrengelman.shadow") version "8.1.0"
  id("org.jmailen.kotlinter") version "3.12.0"
}

group = "realjenius.ketris"
version = "1.0.0"

repositories {
  mavenCentral()
}

kotlin {
  jvmToolchain(17)
}

tasks.jar {
  manifest {
    attributes("Main-Class" to "realjenius.ketris.KetrisKt")
  }
}

tasks.shadowJar {
  archiveBaseName.set("ketris")
  archiveClassifier.set("")
  archiveVersion.set("")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.googlecode.lanterna:lanterna:3.1.1")
}

