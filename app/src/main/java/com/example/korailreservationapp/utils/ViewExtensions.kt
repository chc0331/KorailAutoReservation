package com.example.korailreservationapp.utils

import android.view.View


fun View.toggleVisibility() {
    if (this.visibility == View.GONE)
        this.visibility = View.VISIBLE
    else if (this.visibility == View.VISIBLE)
        this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

