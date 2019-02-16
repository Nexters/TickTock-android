package com.nexters.ticktock.card

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.TransitionDrawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nexters.ticktock.alarmsetting.AlarmSettingFirstActivity
import com.nexters.ticktock.card.Static.COLOR_TRANSITION_TIME
import com.nexters.ticktock.card.Static.MAIN_TOGGLE_DURATION
import com.nexters.ticktock.model.enums.Day
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
        private const val POW = 2
        private val interpolator = CustomSpringInterpolator()
    }

    var isEditPhase = false
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_DELETE_TOGGLE)
        }

    private val layoutInflater = LayoutInflater.from(context)

    private var onCardLongClickListener: ((View?) -> Unit)? = null

    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view),
            View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener{

        private val activeSwitchView = view.findViewById<Switch>(R.id.activeSwitch)
        private val deleteBtnView = view.findViewById<ImageButton>(R.id.deleteBtn)

        /** card top */
        private val cardTopLayout = view.findViewById<ConstraintLayout>(R.id.cardTop)
        private val cardImgView = view.findViewById<ImageView>(R.id.cardImg)
        private val dayList = mapOf<Day, ImageView>(
                Day.MONDAY to view.findViewById(R.id.mondayImg),
                Day.Tuesday to view.findViewById(R.id.tuesdayImg),
                Day.Wednesday to view.findViewById(R.id.wednesdayImg),
                Day.Thursday to view.findViewById(R.id.thursdayImg),
                Day.Friday to view.findViewById(R.id.fridayImg),
                Day.WEEKDAY to view.findViewById(R.id.weekdayImg),
                Day.WEEKEND to view.findViewById(R.id.weekendImg)
        )
        // card title
        private val cardTitleTxtView = view.findViewById<TextView>(R.id.cardTitleTxt)
        // alarm start time
        private val alarmStartTimeHourTxtView = view.findViewById<TextView>(R.id.alarmStartTimeHour)
        private val alarmStartTimeMinuteTxtView = view.findViewById<TextView>(R.id.alarmStartTimeMinute)
        private val alarmSTartTimeMeridiemTxtView = view.findViewById<TextView>(R.id.alarmStartTimeMeridiem) // am or pm


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
            activeSwitchView.setOnCheckedChangeListener(this)
            deleteBtnView.setOnClickListener { deleteCard() }
            view.setOnTouchListener { _, _ ->
                if (isEditPhase) {
                    touchHelper?.startDrag(this)
                }
                false
            }
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
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(if (item.enable) 1f else 0f)
            cardImgView.colorFilter = ColorMatrixColorFilter(colorMatrix)

            cardTitleTxtView.text = item.title
            alarmStartTimeHourTxtView.text = item.startTime.hour.toString()
            alarmStartTimeMinuteTxtView.text = item.startTime.minute.let {
                if (it < 10) {
                    "0$it"
                } else {
                    it.toString()
                }
            }
            alarmSTartTimeMeridiemTxtView.text = item.startTime.meridiem

            dayList.forEach { day, view -> // work with constraint chain
                if (item.days.contains(day)) {
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.GONE
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
                this@CardRecyclerViewAdapter.onCardLongClickListener?.invoke(v)
            }

            return true
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            cardList[super.getAdapterPosition()].enable = isChecked

            val bgColor = (cardTopLayout.background as TransitionDrawable)

            val colorMatrix = ColorMatrix()

            val cardColorAnim = ValueAnimator.ofFloat(0f, 1f).apply {
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
            }

            if (isChecked) {
                bgColor.reverseTransition(COLOR_TRANSITION_TIME.toInt())
                cardColorAnim.start()
            } else {
                bgColor.startTransition(COLOR_TRANSITION_TIME.toInt())
                cardColorAnim.reverse()
            }
        }

        private fun deleteCard() {
            val position = super.getAdapterPosition()

            cardList.removeAt(position)
            notifyItemRemoved(position)
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

    fun setOnCardLongClickListener(onCardLongClickListener: (View?) -> (Unit)) {
        this.onCardLongClickListener = onCardLongClickListener
    }
}