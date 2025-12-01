plugins {
    kotlin("jvm") version "2.2.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "9.2.1"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:multik-core:0.2.3")
    implementation("org.jetbrains.kotlinx:multik-default:0.2.3")
    testImplementation(kotlin("test"))
    implementation("tools.aqua:z3-turnkey:4.14.1")
}
//
//tasks {
//    sourceSets {
//        main {
//            java.srcDirs("src")
//        }
//        test {
//            java.srcDirs("test")
//        }
//    }
//
//    wrapper {
//        gradleVersion = "7.3"
//    }
//
//    test {
//        useJUnitPlatform()
//    }
//}
