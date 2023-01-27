package dev.notrobots.timeline.extensions

import kotlin.random.Random

fun <T> Random.nextItem(iterable: Iterable<T>): T {
    return iterable.elementAt(nextInt(0, iterable.count()))
}

fun <K, V> Random.nextItem(map: Map<K, V>): Map.Entry<K, V> {
    return map.entries.elementAt(nextInt(0, map.count()))
}

fun Random.nextChar(string: String): Char {
    return string[nextInt(0, string.length)]
}

fun <K, V> Random.nextKey(map: Map<K, V>): K {
    return map.keys.elementAt(nextInt(0, map.count()))
}

fun Random.nextString(minLength: Int, maxLength: Int, alphabet: String): String {
    val length = nextInt(minLength, maxLength)

    return IntRange(0, length).map {
        Random.nextChar(alphabet)
    }.joinToString("")
}