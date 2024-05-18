package gradle

import org.gradle.api.Project

fun Project.subProject(name: String): Project {
    val subprojects = rootProject.subprojects
    val matched = subprojects.first {
        it.name == name
    }
    return matched
}

fun Project.childProject(name: String): Project {
    return childProjects[name]!!
}