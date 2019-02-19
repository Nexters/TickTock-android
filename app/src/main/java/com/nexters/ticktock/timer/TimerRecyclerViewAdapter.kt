package com.nexters.ticktock.timer

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nexters.ticktock.R


class TimerRecyclerViewAdapter (val context: Context,
                                val timerStep: MutableList<TimerStepItem>,
                                val recyclerView: RecyclerView,
                                val snapHelper: ControllableTimerSnapHelper
) : RecyclerView.Adapter<TimerRecyclerViewAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)


    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var countdownText = view.findViewById<TextView>(R.id.tv_countdown)

        fun bind() {
            this@TimerRecyclerViewAdapter.timerStep[super.getAdapterPosition()]
                    .also {
                        countdownText.text = it.time
                    }
        }

        override fun onClick(v: View?) {
            if (snapHelper.getAdapterSnapPosition() != super.getAdapterPosition()) {
                recyclerView.smoothScrollToPosition(super.getAdapterPosition())
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_timer_step, viewGroup, false))

    override fun getItemCount(): Int = timerStep.size

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) = viewHolder.bind()
}

