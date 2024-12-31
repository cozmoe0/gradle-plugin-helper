package dev.cozmoe.gradle.task

import dev.cozmoe.gradle.GradlePlugin

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Task(
    val name: String,
    val group: String = GradlePlugin.DEFAULT_TASK_GROUP
)
