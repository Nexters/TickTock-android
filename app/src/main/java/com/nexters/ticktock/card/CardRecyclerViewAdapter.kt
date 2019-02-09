package com.nexters.ticktock.card

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.nexters.ticktock.R
import com.nexters.ticktock.alarmsetting.AlarmSettingFirstActivity

class CardRecyclerViewAdapter(
        val context: Context,
        val cardList: MutableList<CardItem>,
        val recyclerView: RecyclerView,
        val snapHelper: ControllableSnapHelper
) : RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder>() {

    var isDeletePhase = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val layoutInflater = LayoutInflater.from(context)

    private var onCardLongClickListener: ((View?) -> Unit)? = null

    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        private val endTimeTextView = view.findViewById<TextView>(R.id.endTimeText)
        private val destinationTextView = view.findViewById<TextView>(R.id.destinationText)
        private val durationTextView = view.findViewById<TextView>(R.id.durationText)
        private val daysTextView = view.findViewById<TextView>(R.id.daysText)
        private val deleteBtnView = view.findViewById<ImageView>(R.id.deleteBtn)
        private val activeSwitchView = view.findViewById<Switch>(R.id.activeSwitch)
        private val memoLayout = view.findViewById<ConstraintLayout>(R.id.memoLayout)
        private val memoText = view.findViewById<TextView>(R.id.memoText)
        private val memoBtn = view.findViewById<ImageButton>(R.id.memoBtn)
        private val cardLayout = view.findViewById<ConstraintLayout>(R.id.cardLayout)

        init {
            memoLayout.visibility = View.INVISIBLE
            memoLayout.alpha = 0.6f
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)

            memoBtn.setOnClickListener {
                if(memoLayout.visibility == View.VISIBLE) {
                    val slideUpAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)
                    memoLayout.startAnimation(slideUpAnim)
                    memoLayout.visibility = View.INVISIBLE
                } else {
                    val slideDownAnim = AnimationUtils.loadAnimation(context, R.anim.slide_up)
                    memoLayout.startAnimation(slideDownAnim)
                    memoLayout.visibility = View.VISIBLE
                }
            }
        }

        fun bind() {
            this@CardRecyclerViewAdapter.cardList[super.getAdapterPosition()]
                    .also {
                        endTimeTextView.text = it.endTime
                        destinationTextView.text = it.destination
                        durationTextView.text = it.getTime()
                        daysTextView.text = it.days.toString()
                        memoText.text = it.memo
                        cardLayout.setBackgroundColor(Color.parseColor(it.color))
                    }

            if (isDeletePhase) {
                deleteBtnView.visibility = View.VISIBLE
                activeSwitchView.visibility = View.INVISIBLE
            } else {
                deleteBtnView.visibility = View.INVISIBLE
                activeSwitchView.visibility = View.VISIBLE
            }
        }

        override fun onClick(v: View?) {
            if (snapHelper.getAdapterSnapPosition() != super.getAdapterPosition()) {
                recyclerView.smoothScrollToPosition(super.getAdapterPosition())
            } else {
                val alarmSettingIntent : Intent = Intent(context, AlarmSettingFirstActivity::class.java)
                context.startActivity(alarmSettingIntent)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (!isDeletePhase) {
                this@CardRecyclerViewAdapter.onCardLongClickListener?.invoke(v)
            }

            return true
        }
    }

    override fun getItemCount(): Int =
            cardList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_card, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) =
            viewHolder.bind()

    fun setOnCardLongClickListener(onCardLongClickListener: (View?) -> (Unit)) {
        this.onCardLongClickListener = onCardLongClickListener
    }
}