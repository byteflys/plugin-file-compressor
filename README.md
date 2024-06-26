# About this Project

A compressor that allow packaging any resource from anywhere into a Zip/Jar/Apk

# Core Ability

- Support Zip/Jar/Apk Formats
- Compress Any File into a Zip File
- Compress Any Directory into a Zip File
- Decompress and Modify Zip File
- Rename Existed Files
- Resources Could Come from Project, Build, Disk, Artifact, or Task Output
- Output Zip File Can Be Used as Input of Other Gradle Tasks
- Output of Other Gradle Tasks Can Be Used as Input of Compress Task
- Customized Output Path and Format
- Flexible Apis Provided by DSL Object
- Support Task Instant Execution without Gradle Command

# Requirements

- JDK Version above 11
- Gradle Version above 8.6

# Steps for Usage

Step into Next. Now good fun starts

#### 1. Setup Plugin Repository

**_settings.gradle.kts_**
```kotlin
pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.23"
        id("io.github.byteflys.compressor") version "3.0.4"
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.byteflys:compressor:3.0.4")
    }
}
```
#### 2. Apply Plugin

**_build.gradle.kts_**
```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.github.byteflys.compressor")
}
```

#### 3. Configure Task

**_build.gradle.kts_**
```kotlin
compressor {
    zipPath = path(BUILD, DIRECTORY, "./")
    zipName = "compressed"
    zipFormat = "jar"
}
```

#### 4. Define Compress Content

Plugin provides a dsl object named `compressor` with type `CompressTask`

CompressTask provides a serial of apis that start with `copyXXX`

This topic will be detailed later

**_build.gradle.kts_**
```kotlin
compressor {
    val diskDir = path(ABSOLUTE, DIRECTORY, "/Users/easing/Dev/Gradle/gradle-8.7")
    copyDirectory(diskDir, "./")
}
```

#### 5. Execute Task

**_terminal_**
```shell
gradle compress
```

Until this point, you have completed the plugin integration

For more detailed usage, please read on

# CompressTask API

#### 1. Configure Zip Name and Format

Supported formats : Zip/Jar/Apk

**_build.gradle.kts_**
```kotlin
compressor {
    zipPath = path(BUILD, DIRECTORY, "./")
    zipName = "compressed"
    zipFormat = "jar"
}
```

#### 2. Configure Task Output

Specify a file as task output, if other reliant task need to visit it

**_build.gradle.kts_**
```kotlin
compressor {
    val outputFile = outputZipFile()
    outputs.file(outputFile)
}
```

#### 3. Instant Execution

If you don't want to execute task by command line manually

Just call the executeTask api, then task will automatically execute when script is loaded

Executed task must be a child type of ExecutableGradleTask

By default, compress task has implemented ExecutableGradleTask interface

**_build.gradle.kts_**
```kotlin
val makeJarTask = tasks.create("makeJar", ExecutableJar::class.java) {
    from(path(ROOT_PROJECT, DIRECTORY, "compressor/src"))
    archiveBaseName = "makeJar"
    destinationDirectory = File(path(ROOT_BUILD, DIRECTORY, "makeJar"))
}

executeTask("makeJar")
executeTask("compress")
```

#### 4. Include Project Files

Package files from project dir

**_build.gradle.kts_**
```kotlin
compressor {
    val gradleDir = path(ROOT_PROJECT, DIRECTORY, "gradle")
    copyDirectory(gradleDir, "./")
}
```

#### 5. Include Build Files

Package files from project build dir

**_build.gradle.kts_**
```kotlin
compressor {
    val buildDir = path(BUILD, FILE, "libs/compressor-3.0.4-main.jar")
    copyDirectory(buildDir, "./")
}
```

#### 6. Include Disk Files

Package files from disk dir

**_build.gradle.kts_**
```kotlin
compressor {
    val diskDir = path(ABSOLUTE, DIRECTORY, "/home/easing/Dev/Gradle/gradle-8.7")
    copyDirectory(diskDir, "./")
}
```

#### 7. Include Task Output

Package generated files of other task

**_build.gradle.kts_**
```kotlin
val makeJarTask = tasks.create("makeJar", Jar::class.java) {
    from(path(ROOT_PROJECT, DIRECTORY, "compressor/src"))
    archiveBaseName = "makeJar"
    destinationDirectory = File(path(ROOT_BUILD, DIRECTORY, "makeJar"))
}

compressor {
    val jarTask = tasks.named("makeJar", Jar::class.java)
    val taskOutput = jarTask.get().archiveFile.filePath()
    copyFile(taskOutput, "./")
    dependsOn("makeJar")
}
```

#### 8. Include Artifact

Package artifact defined by script or other plugin

**_build.gradle.kts_**
```kotlin
configurations {
    create("artifacts")
}

val jarArtifact = artifacts.add("artifacts", makeJarTask) {
    name = "jarArtifact"
}
val fileArtifact = artifacts.add("artifacts", File("/home/easing/Dev/Gradle/gradle-8.6")) {
    name = "fileArtifact"
}

compressor {
    copyArtifact(jarArtifact, jarArtifact.name)
    copyArtifact(fileArtifact, fileArtifact.name, IS_DIRECTORY or COPY_TO_DIRECTORY or INCLUDE_PARENT_DIRECTORY)
}
```

#### 9. Rename Packaged Resource

**_build.gradle.kts_**
```kotlin
compressor {
    val originFile = tempZipDir().file("makeJar.jar").filePath()
    rename(originFile, "make-jar-output.jar")
}
```

# Complete Sample

**_build.gradle.kts_**
```kotlin
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
```

# End

We get a zip file like this

![Compressed.zip](https://github.com/byteflys/plugin-file-compressor/assets/168255750/40dbdaab-0cac-45bd-864d-c8494aad824e)

