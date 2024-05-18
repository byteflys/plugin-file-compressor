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
    api("net.lingala.zip4j:zip4j:2.11.5")
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
val _pluginClass = "io.github.byteflys.compressor.SamplePlugin"

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
group = _group
version = _version
gradlePlugin {
    plugins {
        website = _website
        vcsUrl = _vcsUrl
        create("compressor") {
            id = _id
            implementationClass = _pluginClass
            displayName = _name
            description = _description
            tags = _tags
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
        mavenLocal()
    }
}

// publish to file system repo
// gradle publishAllPublicationsToFileRepository

// publish to maven local
// gradle publishAllPublicationsToMavenLocal

// publish to gradle plugin central
// gradle publishPlugins