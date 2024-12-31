package dev.cozmoe.gradle.extension

import dev.cozmoe.gradle.GradlePlugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import javax.inject.Inject

abstract class GradleExtension<out T : GradlePlugin>
@Inject constructor(
    protected val project: Project,
    protected val plugin: T
) {

    @Internal
    protected val objects: ObjectFactory = project.objects

}