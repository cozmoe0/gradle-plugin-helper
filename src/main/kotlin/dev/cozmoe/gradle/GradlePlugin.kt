package dev.cozmoe.gradle

import dev.cozmoe.gradle.extension.Extension
import dev.cozmoe.gradle.extension.GradleExtension
import dev.cozmoe.gradle.task.ConfigurableTask
import dev.cozmoe.gradle.task.GradleTask
import dev.cozmoe.gradle.task.Task
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

abstract class GradlePlugin : Plugin<Project> {
    companion object {
        internal const val DEFAULT_TASK_GROUP = "other"
    }

    abstract val id: String

    protected open val conflictsWith = setOf<String>()

    protected lateinit var project: Project
        private set

    private val postHooks = mutableListOf<Runnable>()

    override fun apply(project: Project) {
        this.project = project
        this.applyPlugin()

        project.afterEvaluate {
            this.conflictsWith.filter(project.pluginManager::hasPlugin)
                .ifEmpty {
                    this.postHooks.forEach(Runnable::run)
                    this.afterEvaluate()
                    return@afterEvaluate
                }.also { conflicts ->
                    throw RuntimeException("Plugin $id conflicts with the following plugins: ${conflicts.joinToString(",")}")
                }
        }
    }

    protected abstract fun applyPlugin()

    protected open fun afterEvaluate() = Unit

    protected fun <T : GradleExtension<*>> registerExtension(
        extensionClass: Class<T>,
        vararg objects: Any? = arrayOf(this@GradlePlugin)
    ): T =
        this.project.extensions.create(
            extensionClass.getDeclaredAnnotation(Extension::class.java)
                ?.name
                ?: throw RuntimeException("Extension class must be annotated with @Extension"),
            extensionClass,
            *objects
        )

    protected inline fun <reified T : GradleExtension<*>> registerExtension(
        vararg objects: Any? = arrayOf(this@GradlePlugin)
    ): T = registerExtension(T::class.java, *objects)

    protected fun <T : GradleTask> registerTask(
        taskClass: Class<T>
    ): TaskProvider<T> {
        val taskAnnotation = taskClass.getDeclaredAnnotation(Task::class.java)
            ?: throw RuntimeException("Task class must be annotated with @Task")

        return this.project.tasks.register(
            taskAnnotation.name,
            taskClass,
        ).applyIf(taskAnnotation.group != DEFAULT_TASK_GROUP) {
            this.get().group = taskAnnotation.group
        }
    }

    protected fun <T : ConfigurableTask<*>> registerTask(
        configurableTaskClass: Class<T>,
        configureBlock: T.() -> Unit
    ): TaskProvider<T> = registerTask(configurableTaskClass).also { task ->
        this.postHooks.add { task.configure(configureBlock) }
    }

    protected inline fun <reified T : GradleTask> registerTask(): TaskProvider<T> = registerTask(T::class.java)

    protected inline fun <reified T : ConfigurableTask<*>> registerTask(
        noinline configureBlock: T.() -> Unit
    ) = this.registerTask(T::class.java, configureBlock)
}