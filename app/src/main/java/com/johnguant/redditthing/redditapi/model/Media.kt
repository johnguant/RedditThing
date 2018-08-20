package com.johnguant.redditthing.redditapi.model

class Media {
    val type: String? = null
    val oembed: Oembed? = null

    inner class Oembed {
        val providerUrl: String? = null
        val title: String? = null
        val type: String? = null
        val html: String? = null
        val authorName: String? = null
        val authorUrl: String? = null
        val height: Int = 0
        val width: Int = 0
        val version: String? = null
        val thumbnailWidth: Int = 0
        val thumbnailHeight: Int = 0
        val thumbnailUrl: String? = null
        val providerName: String? = null
    }
}
