package com.nexters.ticktock.card

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics

class SpeedControllableLinearLayoutManager(
        context: Context, orientation: Int, reverseLayout: Boolean, recyclerView: RecyclerView?,
        private val millisecondsPerInch: Float
) : LinearLayoutManager(context, orientation, reverseLayout) {

    private val linearSmoothScroller = object : LinearSmoothScroller(recyclerView?.context) {

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return millisecondsPerInch / displayMetrics!!.densityDpi
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        super.smoothScrollToPosition(recyclerView, state, position)

        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}