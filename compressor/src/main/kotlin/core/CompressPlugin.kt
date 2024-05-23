package io.github.byteflys.compressor

import core.CompressorValues.defaultTaskName
import core.CompressorValues.extensionObjectName
import io.github.byteflys.plugin.core.CompressTask
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class CompressPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val task = project.tasks.create(defaultTaskName, CompressTask::class.java)
        project.extensions.add(extensionObjectName, task)
    }
}