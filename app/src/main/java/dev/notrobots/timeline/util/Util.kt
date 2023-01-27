package dev.notrobots.timeline.util

import androidx.fragment.app.Fragment
import com.google.gson.Gson
import dev.notrobots.androidstuff.util.requireNotEmpty
import java.util.*

/**
 * Returns the hashCode of the given fields.
 *
 * This is meant to be used inside your class' hashCode() method
 */
fun hashCodeOf(vararg fields: Any?): Int {
    requireNotEmpty(fields) {
        "You must provide at least one field"
    }

    // Start with a non-zero constant. Prime is preferred.
    var result = 17

    for (field in fields) {
        result *= 31

        when (field) {
            is Boolean -> result += (if (field) 1 else 0)               // 1 bit   » 32-bit
            is Byte -> result += field                                  // 8 bits  » 32-bit
            is Char -> result += field.code                             // 16 bits  » 32-bit
            is Short -> result += field                                 // 16 bits  » 32-bit
            is Int -> result += field                                   // 32 bits  » 32-bit
            is Long -> result += (field xor (field ushr 32)).toInt()    // 64 bits  » 32-bit
            is Float -> result += field.toBits()                        // 32 bits  » 32-bit
            is Double -> {
                val bits = field.toBits()
                result += (bits xor (bits ushr 32)).toInt()             // 64 bits (double) » 64-bit (long) » 32-bit (int)
            }
            is Array<*> -> Arrays.hashCode(field)
            is ByteArray -> Arrays.hashCode(field)
            is CharArray -> Arrays.hashCode(field)
            is ShortArray -> Arrays.hashCode(field)
            is IntArray -> Arrays.hashCode(field)
            is LongArray -> Arrays.hashCode(field)
            is FloatArray -> Arrays.hashCode(field)
            is DoubleArray -> Arrays.hashCode(field)

            else -> field.hashCode()                                    // var bits » 32-bit
        }
    }

    return result;
}

inline fun <reified T> cloneObject(instance: T): T {
    val gson = Gson()

    return gson.fromJson(gson.toJson(instance), T::class.java)
}

inline fun <reified T> compareObjects(a: T?, b: Any?): Boolean {
    val gson by lazy { Gson() }

    return b is T && gson.toJson(a) == gson.toJson(b)
}

inline fun <reified T> savedStateName(fieldName: String): String {
    return "${T::class.simpleName}.$fieldName"
}