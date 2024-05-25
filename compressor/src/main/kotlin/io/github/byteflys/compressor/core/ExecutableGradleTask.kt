package io.github.byteflys.compressor.core

import io.github.byteflys.compressor.gradle.makeDir
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

interface ExecutableGradleTask {
    fun execute()
}

fun Project.executeTask(name: String) {
    val executable = tasks.named(name).get() as ExecutableGradleTask
    executable.execute()
    println("$name is Executed")
}

abstract class ExecutableJar : Jar(), ExecutableGradleTask {
    override fun execute() {
        archiveFile.makeDir()
        copy()
    }
}

abstract class ExecutableCompressTask : CompressTask(), ExecutableGradleTask