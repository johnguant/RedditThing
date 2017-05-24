package com.johnguant.redditthing.redditapi.model

class Listing<E> {

     val kind: String? = null
     val data: ListingData? = null

    inner class ListingData {
        internal var before: String? = null
        internal var after: String? = null
        internal var modhash: String? = null
        internal var children: List<Thing<E>>? = null
    }
}
