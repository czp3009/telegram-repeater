import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("SpellCheckingInspection")
plugins {
    kotlin("jvm") version "1.2.71"
    id("com.github.johnrengelman.shadow") version "4.0.1"
}

@Suppress("SpellCheckingInspection")
group = "com.hiczp"
version = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
}

@Suppress("SpellCheckingInspection")
dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile("org.telegram:telegrambots:4.1")
    compile("com.github.salomonbrys.kotson:kotson:2.5.0")
    compile("io.github.microutils:kotlin-logging:1.6.10")
    compile("org.slf4j:slf4j-log4j12:1.7.25")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    @Suppress("SpellCheckingInspection")
    manifest {
        attributes["Main-Class"] = "com.hiczp.telegram.repeater.MainKt"
    }
}
