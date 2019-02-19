package com.nexters.ticktock.timer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.nexters.ticktock.R

class CircularProgressbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                    defStyle: Int = R.attr.circularProgressBarStyle) : View(context, attrs, defStyle) {

    private val mCircleBounds = RectF()

    private val mSquareRect = RectF()


    private var mBackgroundColorPaint = Paint()

    var circleStrokeWidth = 10
        private set

    private var mGravity = Gravity.CENTER

    private var mHorizontalInset = 0

    private var mIsInitializing = true

    var isMarkerEnabled = false

    var isThumbEnabled = true

    private var mMarkerColorPaint: Paint? = null

    var markerProgress = 0.3f
        set(progress) {
            isMarkerEnabled = true
            field = progress
        }

    private var mOverrdraw = false

    var progress = 0.3f
        set(progress) {
            if (progress == this.progress) {
                return
            }

            if (progress == 1f) {
                mOverrdraw = false
                field = 1f
            } else {

                mOverrdraw = progress >= 1

                field = progress % 1.0f
            }

            if (!mIsInitializing) {
                invalidate()
            }
        }

    private var mProgressBackgroundColor: Int = 0

    private var mProgressColor: Int = 0

    private var mProgressColorPaint: Paint? = null

    private var mRadius: Float = 0.toFloat()

    private var mThumbColorPaint = Paint()

    private var mThumbPosX: Float = 0.toFloat()

    private var mThumbPosY: Float = 0.toFloat()

    private var mThumbRadius = 20

    private var mTranslationOffsetX: Float = 0.toFloat()

    private var mTranslationOffsetY: Float = 0.toFloat()

    private var mVerticalInset = 0

    var progressColor: Int
        get() = mProgressColor
        set(color) {
            mProgressColor = color

            updateProgressColor()
        }


    private val currentRotation: Float
        get() = 360 * progress


    private val markerRotation: Float
        get() = 360 * markerProgress

    init {

        val attributes = context
                .obtainStyledAttributes(attrs, R.styleable.HoloCircularProgressBar,
                        defStyle, 0)
        if (attributes != null) {
            try {
                progressColor = attributes
                        .getColor(R.styleable.HoloCircularProgressBar_progress_color, Color.CYAN)
                setProgressBackgroundColor(attributes
                        .getColor(R.styleable.HoloCircularProgressBar_progress_background_color,
                                Color.GREEN))
                progress = attributes.getFloat(R.styleable.HoloCircularProgressBar_progress, 0.0f)
                markerProgress = attributes.getFloat(R.styleable.HoloCircularProgressBar_marker_progress,
                        0.0f)
                setWheelSize(attributes
                        .getDimension(R.styleable.HoloCircularProgressBar_stroke_width, 10f).toInt())
                isThumbEnabled = attributes
                        .getBoolean(R.styleable.HoloCircularProgressBar_thumb_visible, true)
                isMarkerEnabled = attributes
                        .getBoolean(R.styleable.HoloCircularProgressBar_marker_visible, true)

                mGravity = attributes
                        .getInt(R.styleable.HoloCircularProgressBar_android_gravity,
                                Gravity.CENTER)
            } finally {
                attributes.recycle()
            }
        }

        mThumbRadius = circleStrokeWidth * 3

        updateBackgroundColor()

        updateMarkerColor()

        updateProgressColor()

        // the view has now all properties and can be drawn
        mIsInitializing = false

    }

    override fun onDraw(canvas: Canvas) {

        canvas.translate(mTranslationOffsetX, mTranslationOffsetY)

        val progressRotation = currentRotation

        // draw the background
        if (!mOverrdraw) {
            canvas.drawArc(mCircleBounds, 270f, -(360 - progressRotation), false,
                    mBackgroundColorPaint)
        }

        // draw the progress or a full circle if overdraw is true
        canvas.drawArc(mCircleBounds, 270f, if (mOverrdraw) 360F else progressRotation, false,
                mProgressColorPaint!!)

        // draw the marker at the correct rotated position
        if (isMarkerEnabled) {
            val markerRotation = markerRotation

            canvas.save()
            canvas.rotate(markerRotation - 90)
            canvas.drawLine((mThumbPosX + mThumbRadius / 2 * 1.4).toFloat(), mThumbPosY,
                    (mThumbPosX - mThumbRadius / 2 * 1.4).toFloat(), mThumbPosY, mMarkerColorPaint!!)
            canvas.restore()
        }

        if (isThumbEnabled) {
            // draw the thumb square at the correct rotated position
            canvas.save()
            canvas.rotate(progressRotation - 90)
            // rotate the square by 45 degrees
            canvas.rotate(45f, mThumbPosX, mThumbPosY)
            mSquareRect.left = mThumbPosX - mThumbRadius / 3
            mSquareRect.right = mThumbPosX + mThumbRadius / 3
            mSquareRect.top = mThumbPosY - mThumbRadius / 3
            mSquareRect.bottom = mThumbPosY + mThumbRadius / 3
            //canvas.drawRect(mSquareRect, mThumbColorPaint);
            canvas.drawCircle(mThumbPosX, mThumbPosY, (mThumbRadius / 2.3).toInt().toFloat(), mThumbColorPaint)
            canvas.restore()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = View.getDefaultSize(
                suggestedMinimumHeight + paddingTop + paddingBottom,
                heightMeasureSpec)
        val width = View.getDefaultSize(
                suggestedMinimumWidth + paddingLeft + paddingRight,
                widthMeasureSpec)

        val diameter: Int
        if (heightMeasureSpec == View.MeasureSpec.UNSPECIFIED) {
            // ScrollView
            diameter = width
            computeInsets(0, 0)
        } else if (widthMeasureSpec == View.MeasureSpec.UNSPECIFIED) {
            // HorizontalScrollView
            diameter = height
            computeInsets(0, 0)
        } else {
            // Default
            diameter = Math.min(width, height)
            computeInsets(width - diameter, height - diameter)
        }

        setMeasuredDimension(diameter, diameter)

        val halfWidth = diameter * 0.5f

        // width of the drawed circle (+ the drawedThumb)
        val drawedWith: Float
        if (isThumbEnabled) {
            drawedWith = mThumbRadius * (5f / 6f)
        } else if (isMarkerEnabled) {
            drawedWith = circleStrokeWidth * 1.4f
        } else {
            drawedWith = circleStrokeWidth / 2f
        }

        // -0.5f for pixel perfect fit inside the viewbounds
        mRadius = halfWidth - drawedWith - 0.5f

        mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius)

        mThumbPosX = (mRadius * Math.cos(0.0)).toFloat()
        mThumbPosY = (mRadius * Math.sin(0.0)).toFloat()

        mTranslationOffsetX = halfWidth + mHorizontalInset
        mTranslationOffsetY = halfWidth + mVerticalInset

    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            progress = state.getFloat(INSTANCE_STATE_PROGRESS)
            markerProgress = state.getFloat(INSTANCE_STATE_MARKER_PROGRESS)

            val progressColor = state.getInt(INSTANCE_STATE_PROGRESS_COLOR)
            if (progressColor != mProgressColor) {
                mProgressColor = progressColor
                updateProgressColor()
            }

            val progressBackgroundColor = state
                    .getInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR)
            if (progressBackgroundColor != mProgressBackgroundColor) {
                mProgressBackgroundColor = progressBackgroundColor
                updateBackgroundColor()
            }

            isThumbEnabled = state.getBoolean(INSTANCE_STATE_THUMB_VISIBLE)

            isMarkerEnabled = state.getBoolean(INSTANCE_STATE_MARKER_VISIBLE)

            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE_SAVEDSTATE))
            return
        }

        super.onRestoreInstanceState(state)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE_SAVEDSTATE, super.onSaveInstanceState())
        bundle.putFloat(INSTANCE_STATE_PROGRESS, progress)
        bundle.putFloat(INSTANCE_STATE_MARKER_PROGRESS, markerProgress)
        bundle.putInt(INSTANCE_STATE_PROGRESS_COLOR, mProgressColor)
        bundle.putInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR, mProgressBackgroundColor)
        bundle.putBoolean(INSTANCE_STATE_THUMB_VISIBLE, isThumbEnabled)
        bundle.putBoolean(INSTANCE_STATE_MARKER_VISIBLE, isMarkerEnabled)
        return bundle
    }


    fun setProgressBackgroundColor(color: Int) {
        mProgressBackgroundColor = color

        updateMarkerColor()
        updateBackgroundColor()
    }


    fun setWheelSize(dimension: Int) {
        circleStrokeWidth = dimension

        // update the paints
        updateBackgroundColor()
        updateMarkerColor()
        updateProgressColor()
    }


    @SuppressLint("NewApi")
    private fun computeInsets(dx: Int, dy: Int) {
        var absoluteGravity = mGravity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            absoluteGravity = Gravity.getAbsoluteGravity(mGravity, layoutDirection)
        }

        when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.LEFT -> mHorizontalInset = 0
            Gravity.RIGHT -> mHorizontalInset = dx
            Gravity.CENTER_HORIZONTAL -> mHorizontalInset = dx / 2
            else -> mHorizontalInset = dx / 2
        }
        when (absoluteGravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.TOP -> mVerticalInset = 0
            Gravity.BOTTOM -> mVerticalInset = dy
            Gravity.CENTER_VERTICAL -> mVerticalInset = dy / 2
            else -> mVerticalInset = dy / 2
        }
    }


    private fun updateBackgroundColor() {
        mBackgroundColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundColorPaint.color = mProgressBackgroundColor
        mBackgroundColorPaint.style = Paint.Style.STROKE
        mBackgroundColorPaint.strokeWidth = circleStrokeWidth.toFloat()

        invalidate()
    }


    private fun updateMarkerColor() {
        mMarkerColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMarkerColorPaint!!.color = mProgressBackgroundColor
        mMarkerColorPaint!!.style = Paint.Style.STROKE
        mMarkerColorPaint!!.strokeWidth = (circleStrokeWidth / 2).toFloat()

        invalidate()
    }


    private fun updateProgressColor() {
        mProgressColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressColorPaint!!.color = mProgressColor
        mProgressColorPaint!!.style = Paint.Style.STROKE
        mProgressColorPaint!!.strokeWidth = circleStrokeWidth.toFloat()

        mThumbColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mThumbColorPaint.color = mProgressColor
        mThumbColorPaint.style = Paint.Style.FILL_AND_STROKE
        mThumbColorPaint.strokeWidth = circleStrokeWidth.toFloat()

        invalidate()
    }

    companion object {

        private val TAG = CircularProgressbar::class.java.simpleName
        private const val INSTANCE_STATE_SAVEDSTATE = "saved_state"
        private const val INSTANCE_STATE_PROGRESS = "progress"
        private const val INSTANCE_STATE_MARKER_PROGRESS = "marker_progress"
        private const val INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR = "progress_background_color"
        private const val INSTANCE_STATE_PROGRESS_COLOR = "progress_color"
        private const val INSTANCE_STATE_THUMB_VISIBLE = "thumb_visible"
        private const val INSTANCE_STATE_MARKER_VISIBLE = "marker_visible"
    }

}