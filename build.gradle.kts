import io.github.byteflys.plugin.CompressTask

plugins {
    id("io.github.byteflys.filecompressor")
}

// gradle compress
tasks.create("compress", CompressTask::class.java) {
    copyDirectory(layout.buildDirectory.dir("libs").toString(), "./")
}