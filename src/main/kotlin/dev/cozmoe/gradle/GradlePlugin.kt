package dev.cozmoe.gradle

import dev.cozmoe.gradle.extension.Extension
import dev.cozmoe.gradle.extension.GradleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

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
}