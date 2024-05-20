import gradle.FileType.DIRECTORY
import gradle.FileType.FILE
import gradle.PathType.ABSOLUTE
import gradle.PathType.BUILD
import gradle.PathType.ROOT_BUILD
import gradle.PathType.ROOT_PROJECT
import gradle.path
import io.github.byteflys.plugin.core.CompressTask

plugins {
    id("io.github.byteflys.compressor")
}

// create jar
tasks.create("makeJar", Jar::class.java) {
    from(path(ROOT_PROJECT, DIRECTORY, "compressor/src"))
    archiveBaseName = "makeJar"
    destinationDirectory = File(path(ROOT_BUILD, DIRECTORY, "makeJar"))
}

// gradle compress
tasks.create("compress", CompressTask::class.java) {
    // include project files
    val gradleDir = path(ROOT_PROJECT, DIRECTORY, "gradle")
    copyDirectory(gradleDir, "./")
    // include build files
    val buildDir = path(BUILD, FILE, "libs/compressor-3.0.4-main.jar")
    copyDirectory(buildDir, "./")
    // include disk files
    val diskDir = path(ABSOLUTE, DIRECTORY, "/Users/easing/Dev/Gradle/gradle-8.7")
    copyDirectory(diskDir, "./")
    // include task artifact
    val jarTask = tasks.named("makeJar", Jar::class.java)
    val artifactFile = jarTask.get().outputs.files.first().absolutePath
    copyFile(artifactFile, "./")
    dependsOn("makeJar")
}