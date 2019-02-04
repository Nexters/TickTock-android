package com.nexters.ticktock.card

import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView

class ControllableSnapHelper(val onSnapped: ((Int) -> Unit)? = null) : LinearSnapHelper() {
    var snappedPosition = 0
    private var snapToNext = false
    private var snapToPrevious = false
    var recyclerView: RecyclerView? = null

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        if (snapToNext) {
            snapToNext = false
            snappedPosition = Math.min(recyclerView?.adapter?.itemCount ?: 0, snappedPosition + 1)
        } else if (snapToPrevious) {
            snapToPrevious = false
            snappedPosition = Math.max(0, snappedPosition - 1)
        } else {
            snappedPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        }

        onSnapped?.invoke(snappedPosition)
        return snappedPosition
    }

    fun snapToNext() {
        snapToNext = true
        onFling(Int.MAX_VALUE, Int.MAX_VALUE)
    }

    fun snapToPrevious() {
        snapToPrevious = true
        onFling(Int.MAX_VALUE, Int.MAX_VALUE)
    }
}