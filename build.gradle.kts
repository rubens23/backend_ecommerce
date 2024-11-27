import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
}

group = "org.rubens"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.2.0")
    implementation("org.mongodb:bson-kotlinx:5.2.0")

    implementation("org.mongodb:bson-kotlin:5.2.0")

    implementation("org.litote.kmongo:kmongo:4.8.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.8.0")


}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}