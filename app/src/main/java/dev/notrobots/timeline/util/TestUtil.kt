package dev.notrobots.timeline.util

import dev.notrobots.androidstuff.util.now
import dev.notrobots.timeline.extensions.nextItem
import dev.notrobots.timeline.extensions.nextKey
import dev.notrobots.timeline.extensions.nextString
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object TestUtil {
    val FAKE_USERS = mapOf(
        "johndoe" to "John",
        "janedoe" to "Jane",
        "albert9" to "Albert",
        "jerma985" to "Jeremy",
        "jakethedog" to "Jake",
        "paulallen" to "Paul"
    )
    val POST_IMAGE_URLS = listOf(
        "https://www.rd.com/wp-content/uploads/2021/01/GettyImages-1175550351.jpg",
//        "https://s36700.pcdn.co/wp-content/uploads/2019/11/Frenchie_getty627306148.png",
        "https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png",
        "https://www.bioparco.it/wp-content/uploads/2016/06/fennec_immagineMDG_6287_SCOPRI.jpg",
        "https://www.thesprucepets.com/thmb/FCbzcHU1jAN-Wu1NEwxrjFylbBQ=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/about-fennec-foxes-as-pets-1236778-hero-e5e8ebfbd07b4516a2508ea59b8d461b.JPG"

    )
    const val ALPHABET = "qwertyuiopasdfghjklzxcvbnm"

    fun randomPostImage(): String {
        return Random.nextItem(POST_IMAGE_URLS)
    }

    fun randomPastTimestamp(): Long {
        return System.currentTimeMillis() - Random.nextLong(
            TimeUnit.MINUTES.toMillis(2),
            TimeUnit.MINUTES.toMillis(20)
        )
    }
}