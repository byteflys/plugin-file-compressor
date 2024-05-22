package gradle

object FileFlags {

    const val INCLUDE_PARENT_DIRECTORY = 0x01 shl 0
    const val EXCLUDE_PARENT_DIRECTORY = 0x01 shl 1

    const val COPY_TO_FILE = 0x01 shl 2
    const val COPY_TO_DIRECTORY = 0x01 shl 3

    const val IS_FILE = 0x01 shl 4
    const val IS_DIRECTORY = 0x01 shl 5
}