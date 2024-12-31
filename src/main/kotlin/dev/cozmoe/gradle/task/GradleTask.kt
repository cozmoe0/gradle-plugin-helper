package dev.cozmoe.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GradleTask : DefaultTask() {

    @TaskAction
    abstract fun run()
}