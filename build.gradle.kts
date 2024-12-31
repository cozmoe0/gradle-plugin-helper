plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.cozmoe.gradle"
version = libs.versions.project.get()

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(gradleApi())
}

val sourcesJar by tasks.registering(Jar::class) {
    group = "build"
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts {
    add("implementation", sourcesJar)
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "dev.cozmoe.gradle"
            artifactId = "gradle-plugin-helper"
            version = version.toString()
        }
    }
}

