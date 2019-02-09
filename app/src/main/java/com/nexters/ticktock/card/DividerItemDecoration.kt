package com.nexters.ticktock.card

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

class DividerItemDecoration : RecyclerView.ItemDecoration {

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    val divider: Drawable

    constructor(context: Context) {
        divider = context.obtainStyledAttributes(ATTRS)
                .let { styledAttributes ->
                    styledAttributes.getDrawable(0).also { styledAttributes.recycle() }
                }!!
    }

    constructor(context: Context, resId: Int) {
        divider = ContextCompat.getDrawable(context, resId)!!
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount

        for (i: Int in 0..(childCount - 1)) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }




}