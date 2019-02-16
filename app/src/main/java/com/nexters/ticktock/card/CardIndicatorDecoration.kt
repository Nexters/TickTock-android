package com.nexters.ticktock.card

 import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.nexters.ticktock.R

class CardIndicatorDecoration(val context: Context) : OffsetItemDecoration(context) {

    private val indicatorColor = ContextCompat.getColor(context, R.color.cardIndicatorColor)

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private val mIndicatorHeight = 28.DP().toInt()

    private val mIndicatorStrokeWidth = 4.DP()

    private val mIndicatorLineStrokeWidth = 1.DP()

    private val indicatorTotalWidth = 280.DP()

    /**
     * Some more natural animation interpolation
     */
    private val mInterpolator = AccelerateDecelerateInterpolator()

    private val mPaint = Paint()

    init {
        mPaint.color = indicatorColor
        mPaint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = mIndicatorLineStrokeWidth

        val itemCount = parent.adapter!!.itemCount

        // center horizontally, calculate width and subtract half from center
        var indicatorStartX = (parent.width - indicatorTotalWidth) / 2f

        // center vertically in the allotted space
//        val indicatorPosY = parent.height - mIndicatorHeight / 2f
        val indicatorPosY = 0f
        drawIndicatorLine(c, indicatorStartX, indicatorPosY, indicatorTotalWidth)

        // find active page (which should be highlighted)
        val layoutManager = parent.layoutManager as LinearLayoutManager?
        val activePosition = layoutManager!!.findFirstVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION || itemCount < 2) {
            return
        }

        // find offset of active page (if the user is scrolling)
        val activeChild = layoutManager.findViewByPosition(activePosition)
        val left = activeChild!!.left - (parent.width - 292.DP().toInt()) / 2

        val width = activeChild.width

        // on swipe the active item will be positioned from [-width, 0]
        // interpolate offset for smooth animation
        val progress = mInterpolator.getInterpolation(left * -1 / width.toFloat())

        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount)
    }

    private fun drawIndicatorLine(c: Canvas, posX: Float, posY: Float, width: Float) {
        c.drawLine(posX, posY + 2.DP(), posX + width, posY + 2.DP(), mPaint)
    }

    private fun drawHighlights(c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
                               highlightPosition: Int, progress: Float, itemCount: Int) {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mIndicatorStrokeWidth

        // width of item indicator including padding
        val itemWidth = indicatorTotalWidth / itemCount

        var highlightStart = indicatorStartX + itemWidth * highlightPosition
        if (progress == 0f) {
            // no swipe, draw a normal indicator
            c.drawLine(highlightStart, indicatorPosY,
                    highlightStart + itemWidth, indicatorPosY, mPaint)
        } else {
            // calculate partial highlight
            val partialLength = itemWidth * progress

            // draw the cut off highlight
            c.drawLine(highlightStart + partialLength, indicatorPosY,
                    highlightStart + itemWidth, indicatorPosY, mPaint)

            // draw the highlight overlapping to the next item as well
                highlightStart += itemWidth
                c.drawLine(highlightStart, indicatorPosY,
                        highlightStart + partialLength, indicatorPosY, mPaint)

        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = mIndicatorHeight + 3.DP().toInt()
    }

    companion object {

        private val DP = Resources.getSystem().displayMetrics.density
    }

    fun Int.DP() =
        this * Resources.getSystem().displayMetrics.density

}