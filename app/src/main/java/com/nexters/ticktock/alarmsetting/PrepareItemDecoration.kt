package com.nexters.ticktock.alarmsetting

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import android.util.TypedValue

class PrepareItemDecoration(var width: Int, var height: Int, val context: Context) : RecyclerView.ItemDecoration() {

    // dp -> pixel 단위로 변경
    private fun dpToPx(context: Context, dp: Int): Int {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spaceWidth = dpToPx(context, width)
        val spaceHeight = dpToPx(context, height)

        outRect.left = spaceWidth
        outRect.right = spaceWidth
        outRect.bottom = spaceHeight
    }
}