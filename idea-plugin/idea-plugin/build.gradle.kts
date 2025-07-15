import kotlin.text.set

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.22"
  id("org.jetbrains.intellij") version "1.15.0"
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

// 配置 IntelliJ 插件版本
val intellijVersion = "2024.3.4.1"

group = "cn.icframework"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}
dependencies {
  implementation("com.alibaba.fastjson2:fastjson2:2.0.53")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set(intellijVersion)
  type.set("IC") // Target IDE Platform
  plugins.set(listOf("com.intellij.java"))
}


tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
  }

  patchPluginXml {
    sinceBuild.set("241.0")
    untilBuild.set("251.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }

  shadowJar {
  }
}
