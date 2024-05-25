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
        classpath("io.github.byteflys:compressor-gradle-plugin:3.0.4")
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

If you donot wanna to execute task by command line manually

Just call the compress api, then task will automatically execute when script is loaded

**_build.gradle.kts_**
```kotlin
compress()
```

#### 4. Include Project Files

Package files from project dir into zip file

**_build.gradle.kts_**
```kotlin
compress()
```

