package gradle

fun Int.containFlag(flag: Int) = and(flag) > 0

fun Int.setFlag(flag: Int) = or(flag)

fun Int.removeFlag(flag: Int) = and(flag.inv())