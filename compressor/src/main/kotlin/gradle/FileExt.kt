package gradle

import java.io.File
import kotlin.io.path.Path

object FileExt {

    fun File.file(file: String): String {
        return Path(this.absolutePath, file).toFile().absolutePath
    }
}