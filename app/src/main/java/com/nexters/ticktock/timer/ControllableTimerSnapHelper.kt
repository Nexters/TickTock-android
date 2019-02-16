package com.nexters.ticktock.timer

import android.content.Context
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.activity_timer.view.*

class ControllableTimerSnapHelper(private var context: TimerActivity,
                                  private var circularProgressbar: CircularProgressbar,
                                  private var buttonNext : Button,
                                  private var buttonReset : Button,
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
                    context.onTimerReset()
                    context.mCountDownTimer!!.cancel()
                    val time : List<String> = context.stepList[snappedPosition].time.split(":")
                    val realTime : Long = (time[2].toLong() + time[1].toLong() * 60  + time[0].toLong() * 3600)
                    context.mTimeToGo = realTime
                    context.mPreferences.setStartedTime(context.getNow())
                    context.TIMER_LENGTH = realTime

                    if(!buttonNext.isEnabled)
                        buttonNext.isEnabled = true

                    if(snappedPosition == 0)
                        buttonReset.isEnabled = false
                    else if(!buttonReset.isEnabled)
                        buttonReset.isEnabled = true

                    //set current position
                    context.curPos = currentPos

                    context.startTimer()
                }
                else if(snappedPosition == recyclerView.adapter?.itemCount!! - 1) {
                    context.onTimerReset()
                    context.mCountDownTimer!!.cancel()
                    buttonNext.isEnabled = false
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


