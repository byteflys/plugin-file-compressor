import io.github.byteflys.compressor.core.ExecutableJar
import io.github.byteflys.compressor.core.executeTask
import io.github.byteflys.compressor.gradle.FileFlags.COPY_TO_DIRECTORY
import io.github.byteflys.compressor.gradle.FileFlags.INCLUDE_PARENT_DIRECTORY
import io.github.byteflys.compressor.gradle.FileFlags.IS_DIRECTORY
import io.github.byteflys.compressor.gradle.FileType.DIRECTORY
import io.github.byteflys.compressor.gradle.FileType.FILE
import io.github.byteflys.compressor.gradle.PathType.ABSOLUTE
import io.github.byteflys.compressor.gradle.PathType.BUILD
import io.github.byteflys.compressor.gradle.PathType.ROOT_BUILD
import io.github.byteflys.compressor.gradle.PathType.ROOT_PROJECT
import io.github.byteflys.compressor.gradle.filePath
import io.github.byteflys.compressor.gradle.path

plugins {
    id("io.github.byteflys.compressor")
}

// create jar
val makeJarTask = tasks.create("makeJar", ExecutableJar::class.java) {
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
val fileArtifact = artifacts.add("artifacts", File("/home/easing/Dev/Gradle/gradle-8.6")) {
    name = "fileArtifact"
}

compressor {
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
    val diskDir = path(ABSOLUTE, DIRECTORY, "/home/easing/Dev/Gradle/gradle-8.7")
    copyDirectory(diskDir, "./")
    // include task output
    val jarTask = tasks.named("makeJar", Jar::class.java)
    val taskOutput = jarTask.get().archiveFile.filePath()
    copyFile(taskOutput, "./")
    dependsOn("makeJar")
    // include artifact
    copyArtifact(jarArtifact, jarArtifact.name)
    copyArtifact(fileArtifact, fileArtifact.name, IS_DIRECTORY or COPY_TO_DIRECTORY or INCLUDE_PARENT_DIRECTORY)
    // rename resource
    val originFile = tempZipDir().file("makeJar.jar").filePath()
    rename(originFile, "make-jar-output.jar")
    // set task output
    val outputFile = outputZipFile()
    outputs.file(outputFile)
}

// instant execute
executeTask("makeJar")
executeTask("compress")