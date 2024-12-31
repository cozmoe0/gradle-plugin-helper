package dev.cozmoe.gradle.task

import dev.cozmoe.gradle.extension.GradleExtension
import org.gradle.api.tasks.Internal

abstract class ConfigurableTask<E : GradleExtension<*>> : GradleTask() {

    private var _configuration: E? = null

    @get:Internal
    protected val configuration: E get() = _configuration!!

    protected open fun applyConfiguration() = Unit

    fun configure(configuration: E) = apply {
        this._configuration = configuration
        this.applyConfiguration()
    }
}