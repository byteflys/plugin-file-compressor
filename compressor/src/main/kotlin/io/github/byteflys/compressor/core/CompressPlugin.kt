package io.github.byteflys.compressor.core

import io.github.byteflys.compressor.core.CompressorValues.defaultTaskName
import io.github.byteflys.compressor.core.CompressorValues.extensionObjectName
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class CompressPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val task = project.tasks.create(defaultTaskName, ExecutableCompressTask::class.java)
        project.extensions.add(extensionObjectName, task)
    }
}