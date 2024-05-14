package io.github.byteflys.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CompressTask : DefaultTask() {

    private val fileCopyTasks = LinkedHashMap<String, String>()
    private val directoryCopyTasks = LinkedHashMap<String, String>()
    private val fileRenameTasks = LinkedHashMap<String, String>()

    private val dir = project.layout.buildDirectory.dir("file-compress-temp").get()

    @OutputFile
    var output = project.layout.buildDirectory.file("artifact.jar").get().asFile

    fun copyFile(src: String, path: String) {
        fileCopyTasks[src] = path
    }

    fun copyDirectory(src: String, path: String) {
        directoryCopyTasks[src] = path
    }

    fun rename(path: String, name: String) {
        fileRenameTasks[path] = name
    }

    @TaskAction
    fun action() {
        fileCopyTasks.forEach { (src, path) ->
            val srcFile = File(src)
            val dstFile = dir.file(path).asFile
            srcFile.copyTo(dstFile, true)
        }
        directoryCopyTasks.forEach { (src, path) ->
            val srcFile = File(src)
            val dstFile = dir.dir(path).asFile
            srcFile.copyRecursively(dstFile, true)
        }
        fileRenameTasks.forEach { (path, name) ->
            val srcFile = dir.dir(path).asFile
            val dstFile = File(srcFile.parentFile, name)
            srcFile.renameTo(dstFile)
        }
        dir.asFile.delete()
    }
}