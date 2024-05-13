import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    api("com.google.code.gson:gson:2.10.1")
}

// define variables
val _name = project.property("NAME").toString()
val _group = project.property("GROUP").toString()
val _id = project.property("ID").toString()
val _version = project.property("VERSION").toString()
val _description = project.property("DESCRIPTION").toString()
val _website = project.property("WEBSITE").toString()
val _vcsUrl = project.property("VCS").toString()
val _tags = project.property("TAGS").toString().split(",")
val _pluginClass = "io.github.byteflys.plugin.SamplePlugin"

// configure shadow rule
tasks.withType<ShadowJar> {
    archiveBaseName.set("$_name-shadowed")
    archiveClassifier.set("")
    archiveVersion.set(_version)
    isEnableRelocation = true
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.9.23"))
        relocate("com.google", "shadow")
        relocate("org.jetbrains", "shadow")
        relocate("org.intellij", "shadow")
    }
}

// create shadowed jar
// gradle shadowJar

// define a plugin
gradlePlugin {
    plugins {
        create("FileCompressorPlugin") {
            displayName = _name
            group = _group
            id = _id
            version = _version
            description = _description
            website = _website
            vcsUrl = _vcsUrl
            tags = _tags
            implementationClass = _pluginClass
        }
    }
}

// publish plugin to local
publishing {
    repositories {
        maven {
            name = "File"
            url = uri(layout.buildDirectory.dir("repository"))
        }
    }
}

// publish to file system repo
// gradle publishPluginMavenPublicationToFileRepository

// publish to gradle plugin central
// gradle publishPlugins