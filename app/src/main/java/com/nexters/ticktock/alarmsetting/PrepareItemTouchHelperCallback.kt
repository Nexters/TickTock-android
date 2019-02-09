package com.nexters.ticktock.alarmsetting

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class PrepareItemTouchHelperCallback(val itemMoveListener : OnItemMoveListener) : ItemTouchHelper.Callback() {

    interface OnItemMoveListener{
        fun onItemMove(fromPosition: Int, toPosition: Int) : Boolean
    }

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {

        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = 0

        return makeMovementFlags(dragFlag, swipeFlag)
    }

    override fun onMove(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        itemMoveListener.onItemMove(holder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        // 안써용
    }

}