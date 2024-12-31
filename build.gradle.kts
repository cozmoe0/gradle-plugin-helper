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

tasks {
    create("sourcesJar", Jar::class) {
        group = "build"
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
}

val artifactTasks = arrayOf(
    tasks["sourcesJar"]
)

artifacts {
    artifactTasks.forEach(::archives)
}

