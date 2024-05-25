package io.github.byteflys.compressor.gradle

import io.github.byteflys.compressor.gradle.FileType.DIRECTORY
import io.github.byteflys.compressor.gradle.FileType.FILE
import io.github.byteflys.compressor.gradle.PathType.ABSOLUTE
import io.github.byteflys.compressor.gradle.PathType.BUILD
import io.github.byteflys.compressor.gradle.PathType.PROJECT
import io.github.byteflys.compressor.gradle.PathType.ROOT_BUILD
import io.github.byteflys.compressor.gradle.PathType.ROOT_PROJECT
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

fun RegularFile.filePath() = asFile.absolutePath

fun Directory.dirPath() = asFile.absolutePath

fun Provider<RegularFile>.filePath() = get().filePath()

fun Provider<Directory>.dirPath() = get().dirPath()

fun Provider<RegularFile>.makeDir() {
    val file = get().asFile
    if (file.isDirectory)
        file.mkdirs()
    else
        file.parentFile.mkdirs()
}

fun Project.filePath(pathType: String, relativePath: String) = when (pathType) {
    PROJECT -> layout.projectDirectory.file(relativePath).filePath()
    BUILD -> layout.buildDirectory.file(relativePath).filePath()
    ROOT_PROJECT -> rootProject.layout.projectDirectory.file(relativePath).filePath()
    ROOT_BUILD -> rootProject.layout.buildDirectory.file(relativePath).filePath()
    ABSOLUTE -> relativePath
    else -> relativePath
}

fun Project.dirPath(pathType: String, relativePath: String) = when (pathType) {
    PROJECT -> layout.projectDirectory.dir(relativePath).dirPath()
    BUILD -> layout.buildDirectory.dir(relativePath).dirPath()
    ROOT_PROJECT -> rootProject.layout.projectDirectory.dir(relativePath).dirPath()
    ROOT_BUILD -> rootProject.layout.buildDirectory.dir(relativePath).dirPath()
    ABSOLUTE -> relativePath
    else -> relativePath
}

fun Project.path(pathType: String, fileType: String, relativePath: String) = when (fileType) {
    FILE -> filePath(pathType, relativePath)
    DIRECTORY -> dirPath(pathType, relativePath)
    else -> relativePath
}

fun Project.fileFromBuildPath(relativePath: String) = path(BUILD, FILE, relativePath)

fun Project.dirFromBuildPath(relativePath: String) = path(BUILD, FILE, relativePath)