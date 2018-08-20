package com.johnguant.redditthing

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.johnguant.redditthing.redditapi.model.Link

class LinkViewModel(): ViewModel() {
    val linkList: LiveData<PagedList<Link>>? = null
}