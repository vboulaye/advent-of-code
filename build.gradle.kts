plugins {
    kotlin("jvm") version "1.7.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:multik-api:0.1.1")
    implementation("org.jetbrains.kotlinx:multik-default:0.1.1")
    testImplementation(kotlin("test"))
    implementation("tools.aqua:z3-turnkey:4.12.2.1")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
        test {
            java.srcDirs("test")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }

    test {
        useJUnitPlatform()
    }
}
