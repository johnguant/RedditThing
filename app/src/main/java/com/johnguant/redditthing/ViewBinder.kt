package com.johnguant.redditthing

import android.view.View

interface ViewBinder<in T> {
    fun bind(t: T) : View
}