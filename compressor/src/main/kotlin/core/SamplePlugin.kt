package io.github.byteflys.compressor

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.OutputDirectory
import java.io.File

abstract class SampleTask : DefaultTask() {

    @Input
    var src = ""

    @Input
    var dst = ""

    // if output exists, skip execution
    @OutputDirectory
    var file = ""

    @TaskAction
    fun action() {
        println("TaskAction Start")
        val srcFile = File(src)
        val dstFile = File(dst)
        srcFile.copyRecursively(dstFile, true)
        println("TaskAction Finish")
    }
}

abstract class SamplePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("SampleTask", SampleTask::class.java) { task ->
            task.group = "Sample"
            task.description = "A Sample Task"
            task.src = "gradle"
            task.dst = "./gradleBak"
            task.file = task.dst
            println("SamplePlugin is applied")
        }
    }
}