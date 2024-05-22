package gradle

fun Int.containFlag(flag: Int) = and(flag) > 0

fun Long.containFlag(flag: Long) = and(flag) > 0