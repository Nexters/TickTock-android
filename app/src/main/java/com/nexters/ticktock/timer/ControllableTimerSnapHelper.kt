package com.nexters.ticktock.timer

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.nexters.ticktock.R
import kotlinx.android.synthetic.main.activity_timer.view.*

class ControllableTimerSnapHelper(private var context: TimerActivity,
                                  private var stepText : TextView,
                                  private var stepTimeList : MutableList<String>,
                                  private var stepNum : TextView,
                                  private var buttonNext : ImageButton,
                                  private var buttonReset : ImageButton,
                                  private val onSnapped: ((Int) -> Unit)? = null
) : PagerSnapHelper() {
    private var snappedPosition = 0
    private var snapToNext = false
    private var snapToPrevious = false
    var currentPos = 0
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
            else -> {
                snappedPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)

                if(snappedPosition >=0 && snappedPosition < recyclerView.adapter?.itemCount!! - 1) {
                    currentPos = snappedPosition

                    //timer 재시작
                    stepText.text = stepTimeList[snappedPosition]
                    stepNum.text = context.getString(R.string.timer_step_num, snappedPosition + 1, stepTimeList.size)
                    context.onTimerReset()
                    context.mCountDownTimer!!.cancel()
                    val time : List<String> = context.stepList[snappedPosition].time.split(":")
                    val realTime : Long = (time[1].toLong() + time[0].toLong() * 60 )
                    context.mTimeToGo = realTime
                    context.mPreferences.setStartedTime(context.getNow())
                    context.TIMER_LENGTH = realTime

                    if(buttonNext.visibility == View.INVISIBLE)
                        buttonNext.visibility = View.VISIBLE

                    if(snappedPosition == 0)
                        buttonReset.visibility = View.INVISIBLE
                    else if(buttonReset.visibility == View.INVISIBLE)
                        buttonReset.visibility = View.VISIBLE



                    //set current position
                    context.curPos = currentPos

                    context.startTimer()
                }
                else if(snappedPosition == recyclerView.adapter?.itemCount!! - 1) {
                    stepText.text = stepTimeList[snappedPosition]
                    stepNum.text = context.getString(R.string.timer_step_num, snappedPosition + 1, stepTimeList.size)
                    context.onTimerReset()
                    context.mCountDownTimer!!.cancel()
                    buttonNext.visibility = View.INVISIBLE
                }
            }
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


