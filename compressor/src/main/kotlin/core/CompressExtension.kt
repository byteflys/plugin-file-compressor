package core

import core.CompressorValues.defaultTaskName
import io.github.byteflys.plugin.core.CompressTask
import org.gradle.api.Project

fun Project.compress() {
    val task = tasks.named(defaultTaskName, CompressTask::class.java).get()
    task.execute()
}