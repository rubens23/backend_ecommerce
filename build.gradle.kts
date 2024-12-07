import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.3.6"

}

group = "org.rubens"
version = "1.0-SNAPSHOT"

application{
    //mainClass.set("com.example.ApplicationKt")
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

}




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

    val koinVersion = "3.2.0-beta-1"


    implementation("io.insert-koin:koin-core:$koinVersion")

    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-core-jvm")



    implementation("io.ktor:ktor-server-auth-jwt-jvm")









}

tasks.test {
    useJUnitPlatform()
}

