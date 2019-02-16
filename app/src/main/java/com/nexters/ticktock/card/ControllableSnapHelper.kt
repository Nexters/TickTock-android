package com.nexters.ticktock.card

import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView

class ControllableSnapHelper(private val onSnapped: ((Int) -> Unit)? = null) : PagerSnapHelper() {
    var snappedPosition = 0
    private var snapToNext = false
    private var snapToPrevious = false
    lateinit var recyclerView: RecyclerView

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView!!
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        when {
            snapToNext -> {
                snapToNext = false
                snappedPosition = Math.min(recyclerView.adapter?.itemCount ?: 0, snappedPosition + 1)
            }
            snapToPrevious -> {
                snapToPrevious = false
                snappedPosition = Math.max(0, snappedPosition - 1)
            }
            else -> snappedPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
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

    fun getAdapterSnapPosition(): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return recyclerView.getChildAdapterPosition(snapView)
    }
}