package io.github.byteflys.plugin.core

import gradle.CopyStrategy.INCLUDE_PARENT_DIRECTORY
import module.LingalaZipFile
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
    var output = project.layout.buildDirectory.file("compressed.zip").get().asFile

    init {
        // for execute without cache
        outputs.upToDateWhen { false }
    }

    fun copyFile(src: String, path: String) {
        fileCopyTasks[src] = path
    }

    fun copyDirectory(src: String, path: String, strategy: String = INCLUDE_PARENT_DIRECTORY) {
        val path = if (strategy == INCLUDE_PARENT_DIRECTORY)
            "$path/${File(src).name}"
        else
            path
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
        // delete outdated zip file
        output.delete()
        // compress zip file
        val zipFile = LingalaZipFile(output)
        zipFile.addFolder(dir.asFile)
        // delete temp file
        dir.asFile.deleteRecursively()
    }
}