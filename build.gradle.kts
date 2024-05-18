import gradle.FileType.DIRECTORY
import gradle.PathType.ROOT_PROJECT
import gradle.path
import io.github.byteflys.plugin.core.CompressTask

plugins {
    id("io.github.byteflys.compressor")
}

// gradle compress
tasks.create("compress", CompressTask::class.java) {
    val gradleDir = path(ROOT_PROJECT, DIRECTORY, "gradle")
    copyDirectory(gradleDir, "./")
}