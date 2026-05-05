plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    // Incluir o módulo de anotações
    implementation(project(":annotations"))
    // Usar o processador de anotações
    kapt(project(":processor"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(23)
}