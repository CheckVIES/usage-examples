import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("application")
    kotlin("jvm") version "2.3.20"
    id ("com.github.ben-manes.versions") version "0.53.0"
}

group = "com.checkvies.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.checkvies:client:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-logging:3.4.2")
    implementation("io.ktor:ktor-client-cio:3.4.2")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.checkvies.example.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    // disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
        candidate.version.isNonStable() && !currentVersion.isNonStable()
    }
}