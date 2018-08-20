package com.johnguant.redditthing.redditapi.model

class Thing<out E>(val kind: String, val data: E) {
    val id: String? = null
    val name: String = ""
}
