package com.nexters.ticktock.timer

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics

class SpeedControllableTimerLayoutManager(
        context: Context, orientation: Int, reverseLayout: Boolean, recyclerView: RecyclerView?,
        private val millisecondsPerInch: Float
) : LinearLayoutManager(context, orientation, reverseLayout) {

    // SmoothScroller 는 무조건 새로 만들어서 실행시켜야 함
    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        val linearSmoothScroller = object : LinearSmoothScroller(recyclerView?.context) {

            // 이동 속도 조정
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return millisecondsPerInch / displayMetrics!!.densityDpi
            }

            // center fit
            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
            }
        }

        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}