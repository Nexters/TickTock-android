package com.nexters.ticktock.card

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener(context: Context) : View.OnTouchListener {

    companion object {
        const val SWIPE_DISTANCE_THRESHOLD = 50
        const val SWIPE_VELOCITY_THRESHOLD = 50
    }

    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent?): Boolean =
            gestureDetector.onTouchEvent(event)

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y

            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                Log.d("SWIPE_TEXT", "distanceX = $distanceX, velocityX = $velocityX, distanceY = $distanceY, velocityY = $velocityY")
                if (distanceX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }

                return true
            } else if (Math.abs(distanceX) < Math.abs(distanceY)
                    && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD
                    && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {

                Log.d("SWIPE_TEXT", "distanceX = $distanceX, velocityX = $velocityX, distanceY = $distanceY, velocityY = $velocityY")
                if (distanceY > 0) {
                    onSwipeBottom()
                } else {
                    onSwipeTop()
                }

                return true
            }
            Log.d("SWIPE_TEXT", "distanceX = $distanceX, velocityX = $velocityX, distanceY = $distanceY, velocityY = $velocityY")

            return false
        }

    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onSwipeTop() {}

    open fun onSwipeBottom() {}
}