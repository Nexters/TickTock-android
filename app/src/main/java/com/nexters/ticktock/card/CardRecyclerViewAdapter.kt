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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.nexters.ticktock.MainDetailActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.alarmsetting.AlarmSettingFirstActivity
import com.nexters.ticktock.card.Static.COLOR_TRANSITION_TIME
import com.nexters.ticktock.card.Static.MAIN_TOGGLE_DURATION
import com.nexters.ticktock.card.listener.CardEventListener
import com.nexters.ticktock.card.listener.CardChangeListener
import com.nexters.ticktock.dao.AlarmDao
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.getArrangedDays
import com.nexters.ticktock.utils.invisible
import com.nexters.ticktock.utils.visible
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class CardRecyclerViewAdapter(
        val context: Context,
        val cardContext: CardContext,
        val recyclerView: RecyclerView,
        val snapHelper: ControllableSnapHelper
) : RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder>(), CardEventListener {

    companion object {
        const val PAYLOAD_DELETE_TOGGLE = "DELETE_TOGGLE"
    }

    private val layoutInflater = LayoutInflater.from(context)
    private val alarmDao: AlarmDao = TickTockDBHelper.getInstance(context).alarmDao

    init {
        cardContext.addCardEventListener(this)
    }

    override fun getPriority(): Int = 5

    inner class ViewHolder(val view: View)
        : RecyclerView.ViewHolder(view),
            View.OnClickListener, View.OnLongClickListener, CardChangeListener {

        override fun getPriority(): Int = 5

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

        fun bind() {
            bindButton()

            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            activeSwitchView.setOnCheckedChangeListener { _, isChecked ->
                cardContext[super.getAdapterPosition()].enable = isChecked
            }
            deleteBtnView.setOnClickListener {
                cardContext.removeAt(super.getAdapterPosition())
            }

            this@CardRecyclerViewAdapter.cardContext[super.getAdapterPosition()].run {
                activeSwitchView.isChecked = enable
                bindCardTop(this)
                bindCardBottom(this)
                cardChangeListener = this@ViewHolder
            }
        }

        fun bindButton() {
            if (!cardContext.isEditPhase) {
                activeSwitchView.visible(MAIN_TOGGLE_DURATION)
                deleteBtnView.invisible(MAIN_TOGGLE_DURATION)
            } else {
                activeSwitchView.invisible(MAIN_TOGGLE_DURATION)
                deleteBtnView.visible(MAIN_TOGGLE_DURATION)
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
            } else if (!cardContext.isEditPhase) {
                val intent = Intent(context, MainDetailActivity::class.java)
                intent.putExtra("CARD_ID", cardContext[super.getAdapterPosition()].id)
                context.startActivity(intent)
            }
        }

        override fun onLongClick(v: View?): Boolean =
                true.apply { if (!cardContext.isEditPhase) {
                    cardContext.isEditPhase = true
                } }

        override fun onCardChange(changedCard: CardItem, diff: Int) {
            if (diff == CardChangeListener.ENABLE) {

                val isChecked = changedCard.enable
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
                    changedCard.color.cardBgColorId
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

            Observable.just(alarmDao.save(changedCard.toAlarm()))
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }

    override fun getItemCount(): Int =
            cardContext.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_card, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) =
            viewHolder.bind()

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_DELETE_TOGGLE)) {
            holder.bindButton()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onPhaseChange(isEditPhase: Boolean) {
        notifyItemRangeChanged(0, cardContext.size, PAYLOAD_DELETE_TOGGLE)
    }

    override fun onCardAdd(addedCard: CardItem) {
        notifyDataSetChanged()
    }

    override fun onCardRemove(position: Int, removedCard: CardItem) {
        notifyItemRemoved(position)
        if (itemCount != 1) {
            notifyItemChanged(position - 1)
            notifyItemChanged(position)
        }
    }

    override fun onActive() {
        notifyDataSetChanged()
    }

    fun CardItem.toAlarm() =
            Alarm(
                    id = id,
                    days = days,
                    title = title,
                    startLocation = startLocation,
                    endLocation = endLocation,
                    color = color,
                    enable = enable,
                    endTime = endTime,
                    travelTime = travelTime
            )
}