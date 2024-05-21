package io.github.byteflys.plugin.core

import gradle.CopyStrategy.COPY_TO_DIRECTORY
import gradle.CopyStrategy.INCLUDE_PARENT_DIRECTORY
import gradle.dirPath
import module.LingalaZipFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.Path

abstract class CompressTask : DefaultTask() {

    private val fileCopyTasks = LinkedHashMap<String, String>()
    private val directoryCopyTasks = LinkedHashMap<String, String>()
    private val fileRenameTasks = LinkedHashMap<String, String>()

    private val tempZipFolder = project.layout.buildDirectory.dir("file-compress-temp").get()

    @Input
    var zipPath = project.layout.buildDirectory.dirPath()

    @Input
    var zipName = "compressed"

    @Input
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

    fun tempZipDir() = tempZipFolder

    fun outputZipFile() = File("$zipPath/$zipName.$zipFormat")

    @TaskAction
    fun execute() {
        // include file
        fileCopyTasks.forEach { (src, path) ->
            val srcFile = File(src)
            val dstFile = tempZipFolder.file(path).asFile
            srcFile.copyTo(dstFile, true)
        }
        directoryCopyTasks.forEach { (src, path) ->
            val srcFile = File(src)
            val dstFile = tempZipFolder.dir(path).asFile
            srcFile.copyRecursively(dstFile, true)
        }
        fileRenameTasks.forEach { (path, name) ->
            val srcFile = tempZipFolder.dir(path).asFile
            val dstFile = File(srcFile.parentFile, name)
            srcFile.renameTo(dstFile)
        }
        // create output file
        val outputZipFile = outputZipFile()
        // delete outdated zip file
        outputZipFile.delete()
        // rename zip folder name
        val zipFolder = Path(tempZipFolder.asFile.parentFile.absolutePath, zipName).toFile()
        tempZipFolder.asFile.renameTo(zipFolder)
        // compress zip file
        val zipFile = LingalaZipFile(outputZipFile)
        zipFile.addFolder(zipFolder)
        // delete zip folder
        zipFolder.deleteRecursively()
    }
}