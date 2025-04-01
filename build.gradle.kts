plugins {
    id("java")
    id("application")
    id("org.hibernate.orm") version "6.6.12.Final"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://m2.duncte123.dev/releases")
    }

    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.2") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.18") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.codehaus.janino:janino:3.1.12")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.apache.commons:commons-text:1.13.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.3")

    implementation("org.postgresql:postgresql:42.7.5")

    implementation("org.hibernate:hibernate-core:6.6.12.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.hibernate.validator:hibernate-validator:8.0.2.Final")
    implementation("org.hibernate:hibernate-hikaricp:6.6.12.Final")

    implementation("com.zaxxer:HikariCP:6.3.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
}

group = "org.RubberDucky"
version = "1.0"
description = "RubberDucky"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    sourceSets["main"].java.srcDirs("src/main/java")
}

application {
    mainClass.set("Bot")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass
        )
    }

    from(
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    )

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
