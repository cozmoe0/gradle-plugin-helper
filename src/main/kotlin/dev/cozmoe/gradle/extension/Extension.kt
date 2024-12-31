package dev.cozmoe.gradle.extension

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Extension(
    val name: String
)
