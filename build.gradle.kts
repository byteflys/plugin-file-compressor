import gradle.FileFlags.COPY_TO_DIRECTORY
import gradle.FileFlags.INCLUDE_PARENT_DIRECTORY
import gradle.FileFlags.IS_FILE
import gradle.FileType.DIRECTORY
import gradle.FileType.FILE
import gradle.PathType.ABSOLUTE
import gradle.PathType.BUILD
import gradle.PathType.ROOT_BUILD
import gradle.PathType.ROOT_PROJECT
import gradle.filePath
import gradle.path
import io.github.byteflys.plugin.core.CompressTask

plugins {
    id("io.github.byteflys.compressor")
}

// create jar
val makeJarTask = tasks.create("makeJar", Jar::class.java) {
    from(path(ROOT_PROJECT, DIRECTORY, "compressor/src"))
    archiveBaseName = "makeJar"
    destinationDirectory = File(path(ROOT_BUILD, DIRECTORY, "makeJar"))
}

// create configurations
configurations {
    create("artifacts")
}

// create artifacts
val jarArtifact = artifacts.add("artifacts", makeJarTask) {
    name = "jarArtifact"
}
val fileArtifact = artifacts.add("artifacts", File("/Users/easing/Dev/Gradle/gradle-8.6")) {
    name = "fileArtifact"
}

// gradle compress
tasks.create("compress", CompressTask::class.java) {
    // specify export path
    zipPath = path(BUILD, DIRECTORY, "./")
    zipName = "compressed"
    zipFormat = "jar"
    // include project files
    val gradleDir = path(ROOT_PROJECT, DIRECTORY, "gradle")
    copyDirectory(gradleDir, "./")
    // include build files
    val buildDir = path(BUILD, FILE, "libs/compressor-3.0.4-main.jar")
    copyDirectory(buildDir, "./")
    // include disk files
    val diskDir = path(ABSOLUTE, DIRECTORY, "/Users/easing/Dev/Gradle/gradle-8.7")
    copyDirectory(diskDir, "./")
    // include task output
    val jarTask = tasks.named("makeJar", Jar::class.java)
    val taskOutput = jarTask.get().archiveFile.filePath()
    copyFile(taskOutput, "./")
    dependsOn("makeJar")
    // include artifact
    copyArtifact(jarArtifact, jarArtifact.name)
    copyArtifact(fileArtifact, fileArtifact.name, IS_FILE or COPY_TO_DIRECTORY or INCLUDE_PARENT_DIRECTORY)
    // rename resource
    val originFile = tempZipDir().file("makeJar.jar").filePath()
    rename(originFile, "make-jar-output.jar")
    // set task output
    val outputFile = outputZipFile()
    outputs.file(outputFile)
}