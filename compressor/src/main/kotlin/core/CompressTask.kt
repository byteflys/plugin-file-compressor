package io.github.byteflys.plugin.core

import gradle.CopyStrategy.COPY_TO_DIRECTORY
import gradle.CopyStrategy.INCLUDE_PARENT_DIRECTORY
import gradle.dirPath
import module.LingalaZipFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.Path

abstract class CompressTask : DefaultTask() {

    private val fileCopyTasks = LinkedHashMap<String, String>()
    private val directoryCopyTasks = LinkedHashMap<String, String>()
    private val fileRenameTasks = LinkedHashMap<String, String>()

    private val dir = project.layout.buildDirectory.dir("file-compress-temp").get()

    @OutputFile
    private lateinit var output: File

    var zipPath = project.layout.buildDirectory.dirPath()
    var zipName = "compressed"
    var zipFormat = "zip"

    init {
        // for execute without cache
        outputs.upToDateWhen { false }
    }

    fun copyFile(src: String, path: String, strategy: String = COPY_TO_DIRECTORY) {
        val path = if (strategy == COPY_TO_DIRECTORY)
            "$path/${File(src).name}"
        else
            path
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
        // create output file
        output = File("$zipPath/$zipName.$zipFormat")
        // include file
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
        // rename zip folder name
        val renamedFile = Path(dir.asFile.parentFile.absolutePath, zipName).toFile()
        dir.asFile.renameTo(renamedFile)
        // compress zip file
        val zipFile = LingalaZipFile(output)
        zipFile.addFolder(dir.asFile)
        // delete zip folder
        renamedFile.deleteRecursively()
    }
}