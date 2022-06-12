plugins {
    id("java")
}

group = "discord.badbusiness"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://m2.chew.pro/snapshots")
    }
}

dependencies {

    implementation("net.dv8tion:JDA:5.0.0-alpha.8")
    implementation("pw.chew:jda-chewtils:2.0-SNAPSHOT")
}