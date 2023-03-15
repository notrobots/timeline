package dev.notrobots.libandroidstuff.extensions

fun Any?.toStringOrNull(): String? {
    return this?.toString()
}