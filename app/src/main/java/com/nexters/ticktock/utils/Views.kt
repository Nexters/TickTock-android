package com.nexters.ticktock.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.visible(duration: Long) {
    animate().alpha(1f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.VISIBLE
                }
            })
}

fun View.invisible(duration: Long) {
    animate().alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.INVISIBLE
                }
            })
}