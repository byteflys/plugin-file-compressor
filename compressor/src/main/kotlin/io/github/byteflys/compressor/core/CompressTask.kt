package io.github.byteflys.compressor.core

import io.github.byteflys.compressor.gradle.FileFlags.COPY_TO_DIRECTORY
import io.github.byteflys.compressor.gradle.FileFlags.INCLUDE_PARENT_DIRECTORY
import io.github.byteflys.compressor.gradle.FileFlags.IS_DIRECTORY
import io.github.byteflys.compressor.gradle.FileFlags.IS_FILE
import io.github.byteflys.compressor.gradle.containFlag
import io.github.byteflys.compressor.gradle.dirPath
import io.github.byteflys.compressor.module.LingalaZipFile
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.PublishArtifact
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

    fun copyFile(src: String, path: String, flags: Int = COPY_TO_DIRECTORY) {
        val copyToDirectory = flags.containFlag(COPY_TO_DIRECTORY)
        val path = if (copyToDirectory)
            "$path/${File(src).name}"
        else
            path
        fileCopyTasks[src] = path
    }

    fun copyDirectory(src: String, path: String, flags: Int = INCLUDE_PARENT_DIRECTORY) {
        val includeParentDirectory = flags.containFlag(INCLUDE_PARENT_DIRECTORY)
        val path = if (includeParentDirectory)
            "$path/${File(src).name}"
        else
            path
        directoryCopyTasks[src] = path
    }

    fun copyArtifact(
        artifact: PublishArtifact,
        path: String,
        flags: Int = IS_FILE or COPY_TO_DIRECTORY or INCLUDE_PARENT_DIRECTORY
    ) {
        val isDirectory = flags.containFlag(IS_DIRECTORY)
        if (isDirectory)
            copyDirectory(artifact.file.absolutePath, path, flags)
        else
            copyFile(artifact.file.absolutePath, path, flags)
    }

    fun rename(path: String, name: String) {
        fileRenameTasks[path] = name
    }

    fun tempZipDir() = tempZipFolder

    fun outputZipFile() = File("$zipPath/$zipName.$zipFormat")

    @TaskAction
    open fun execute() {
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