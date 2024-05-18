package gradle

import gradle.FileType.DIRECTORY
import gradle.FileType.FILE
import gradle.PathType.ABSOLUTE
import gradle.PathType.BUILD
import gradle.PathType.PROJECT
import gradle.PathType.ROOT_BUILD
import gradle.PathType.ROOT_PROJECT
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

fun RegularFile.filePath() = asFile.absolutePath

fun Directory.dirPath() = asFile.absolutePath

fun Provider<RegularFile>.filePath() = get().filePath()

fun Provider<Directory>.dirPath() = get().dirPath()

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