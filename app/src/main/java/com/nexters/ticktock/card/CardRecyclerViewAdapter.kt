package com.nexters.ticktock.card

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nexters.ticktock.R
import com.nexters.ticktock.alarmsetting.AlarmSettingFirstActivity
import com.nexters.ticktock.card.Static.COLOR_TRANSITION_TIME
import com.nexters.ticktock.card.Static.MAIN_TOGGLE_DURATION
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.getArrangedDays
import com.nexters.ticktock.utils.invisible
import com.nexters.ticktock.utils.visible


class CardRecyclerViewAdapter(
        val context: Context,
        val cardList: MutableList<CardItem>,
        val recyclerView: RecyclerView,
        val snapHelper: ControllableSnapHelper
) : RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder>() {

    companion object {
        const val PAYLOAD_DELETE_TOGGLE = "DELETE_TOGGLE"
    }

    var isEditPhase = false
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_DELETE_TOGGLE)
        }

    private val layoutInflater = LayoutInflater.from(context)

    var onCardEventListener: CardEventListener? = null

    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view),
            View.OnClickListener, View.OnLongClickListener{

        private val activeSwitchView = view.findViewById<Switch>(R.id.activeSwitch)
        private val deleteBtnView = view.findViewById<ImageButton>(R.id.deleteBtn)

        /** card top */
        private val cardTopLayout = view.findViewById<ConstraintLayout>(R.id.cardTop)
        private val cardImgView = view.findViewById<ImageView>(R.id.cardImg)
        private val dayList = mapOf<Day, ImageView>(
                Day.Monday to view.findViewById(R.id.mondayImg),
                Day.Tuesday to view.findViewById(R.id.tuesdayImg),
                Day.Wednesday to view.findViewById(R.id.wednesdayImg),
                Day.Thursday to view.findViewById(R.id.thursdayImg),
                Day.Friday to view.findViewById(R.id.fridayImg),
                Day.Saturday to view.findViewById(R.id.saturdayImg),
                Day.Sunday to view.findViewById(R.id.sundayImg),
                Day.Weekday to view.findViewById(R.id.weekdayImg),
                Day.Weekend to view.findViewById(R.id.weekendImg)
        )
        // card title
        private val cardTitleTxtView = view.findViewById<TextView>(R.id.cardTitleTxt)
        // alarm start time
        private val alarmStartTimeHourTxtView = view.findViewById<TextView>(R.id.alarmStartTimeHour)
        private val alarmStartTimeMinuteTxtView = view.findViewById<TextView>(R.id.alarmStartTimeMinute)
        private val alarmStartTimeMeridiemTxtView = view.findViewById<TextView>(R.id.alarmStartTimeMeridiem) // am or pm


        /** card bottom */
        // start location
        private val startTimeTxtView = view.findViewById<TextView>(R.id.startTimeTxt)
        private val startLocationTxtView = view.findViewById<TextView>(R.id.startLocationTxt)
        // end location
        private val endTimeTxtView = view.findViewById<TextView>(R.id.endTimeTxt)
        private val endLocationTxtView = view.findViewById<TextView>(R.id.endLocationTxt)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            activeSwitchView.setOnCheckedChangeListener { _, isChecked -> cardColorToggle(isChecked) }
            deleteBtnView.setOnClickListener { deleteCard() }
        }

        fun bind() {
            editToggle()

            this@CardRecyclerViewAdapter.cardList[super.getAdapterPosition()].run {
                activeSwitchView.isChecked = enable
                bindCardTop(this)
                bindCardBottom(this)
            }
        }

        private fun bindCardTop(item: CardItem) {
            cardTopLayout.setBackgroundResource(item.color.cardBgColorId)
            cardImgView.setImageResource(item.color.cardImgId)

            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(if (item.enable) 1f else 0f)
            cardImgView.colorFilter = ColorMatrixColorFilter(colorMatrix)

            if (item.enable) {
                cardTopLayout.setBackgroundResource(item.color.cardBgColorId)
            } else {
                cardTopLayout.setBackgroundResource(R.color.cardDisableBGColor)
            }

            cardTitleTxtView.text = item.title
            alarmStartTimeHourTxtView.text = item.startTime.hour.toString()
            alarmStartTimeMinuteTxtView.text = item.startTime.minute.let {
                if (it < 10) {
                    "0$it"
                } else {
                    it.toString()
                }
            }
            alarmStartTimeMeridiemTxtView.text = item.startTime.meridiem

            for (dayView in dayList) { // work with constraint chain
                if (item.days.getArrangedDays().contains(dayView.key)) {
                    dayView.value.visibility = View.VISIBLE
                } else {
                    dayView.value.visibility = View.GONE
                }
            }
        }

        private fun bindCardBottom(item: CardItem) {
            startTimeTxtView.text = item.startTime.toString()
            startLocationTxtView.text = item.startLocation
            endTimeTxtView.text = item.endTime.toString()
            endLocationTxtView.text = item.endLocation
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
            if (!isEditPhase) {
                isEditPhase = true
                this@CardRecyclerViewAdapter.onCardEventListener?.onCardLongClick(v)
            }

            return true
        }

        private fun cardColorToggle(isChecked: Boolean) {

            val cardItem = cardList[super.getAdapterPosition()]

            cardItem.enable = isChecked

            val colorMatrix = ColorMatrix()

            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = COLOR_TRANSITION_TIME
                addUpdateListener {animator ->
                    colorMatrix.setSaturation(animator.animatedFraction)
                    cardImgView.colorFilter = ColorMatrixColorFilter(colorMatrix)
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        colorMatrix.setSaturation(if (isChecked) 1f else 0f)
                        cardImgView.colorFilter = ColorMatrixColorFilter(colorMatrix)
                    }
                })
            }.run {
                if (isChecked) {
                    start()
                } else {
                    reverse()
                }
            }

            val bgColorFrom = (cardTopLayout.background as ColorDrawable).color
            val bgColorTo = ContextCompat.getColor(context, if (isChecked) {
                cardItem.color.cardBgColorId
            } else {
                R.color.cardDisableBGColor
            })

            ValueAnimator.ofObject(ArgbEvaluator(), bgColorFrom, bgColorTo).apply {
                duration = COLOR_TRANSITION_TIME
                addUpdateListener {
                    cardTopLayout.setBackgroundColor(it.animatedValue as Int)
                }
            }.start()

        }

        private fun deleteCard() {
            val position = super.getAdapterPosition()

            cardList.removeAt(position)

            if (itemCount == 0) {
                isEditPhase = false
                this@CardRecyclerViewAdapter.onCardEventListener?.onNoCardThere(this@CardRecyclerViewAdapter)
            }

            this@CardRecyclerViewAdapter.onCardEventListener?.onCardDelete(position, this@CardRecyclerViewAdapter)

            notifyItemRemoved(position)
            if (itemCount != 1) {
                notifyItemChanged(position - 1)
                notifyItemChanged(position)
            }
        }

        fun editToggle() {
            if (!isEditPhase) {
                activeSwitchView.visible(MAIN_TOGGLE_DURATION)
                deleteBtnView.invisible(MAIN_TOGGLE_DURATION)
            } else {
                activeSwitchView.invisible(MAIN_TOGGLE_DURATION)
                deleteBtnView.visible(MAIN_TOGGLE_DURATION)
            }
        }
    }

    override fun getItemCount(): Int =
            cardList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_card, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) =
            viewHolder.bind()

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_DELETE_TOGGLE)) {
            holder.editToggle()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}