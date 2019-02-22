package com.nexters.ticktock.alarmsetting

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import com.nexters.ticktock.CardActivity
import com.nexters.ticktock.R
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.databinding.ActivityAlarmSettingThirdBinding
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.Step
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.timer.AlarmReceiver
import com.nexters.ticktock.timer.TimerActivity
import com.nexters.ticktock.utils.Time
import com.nexters.ticktock.utils.getHighlightedString
import com.nexters.ticktock.utils.getResizedString
import java.util.*
import kotlin.collections.ArrayList



class AlarmSettingThirdActivity : AppCompatActivity(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private lateinit var binding: ActivityAlarmSettingThirdBinding
    private lateinit var prepareTimeRecyclerAdapter: PrepareTimeRecyclerAdapter

    private lateinit var daySet: EnumSet<Day>
    private var startLocation: String? = null
    private var endLocation: String? = null
    private lateinit var travelTime: Time
    private lateinit var endTime: Time
    private lateinit var stepList: ArrayList<Step>

    private lateinit var startTime: Time

    private var originTime = 0
    private var quickTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        binding = DataBindingUtil.setContentView(this, com.nexters.ticktock.R.layout.activity_alarm_setting_third)

        binding.tvTitle.text = getHighlightedString(resources.getString(com.nexters.ticktock.R.string.tv_alarm_setting_third_title))
        binding.tvStartTimeDescription.text = getResizedString("${startTime.meridiem}* ${startTime.hour}:${startTime.minute}*", 1.85f)

        val travelHour = travelTime.time / 60
        if (travelHour != 0) binding.tvTimeToDestination.text = "${travelHour}시간 ${travelTime.minute}분"
        else binding.tvTimeToDestination.text = "${travelTime.minute}분"

        val prepareTime = stepList.map { it.duration }.fold(Time(0)) { acc, time -> acc + time }
        val prepareHour = prepareTime.time / 60
        if (prepareHour != 0) binding.tvPrepareTime.text = "${prepareHour}시간 ${prepareTime.minute}분"
        else binding.tvPrepareTime.text = "${prepareTime.minute}분"

        binding.edMemo.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.tvMemoMax.visibility = View.GONE
                    binding.layoutSave.isClickable = true
                    binding.layoutSave.setBackgroundColor(ContextCompat.getColor(applicationContext, com.nexters.ticktock.R.color.btnEnable))
                }
                else if (count == 0) {
                    binding.tvMemoMax.visibility = View.VISIBLE
                    binding.layoutSave.isClickable = false
                    binding.layoutSave.setBackgroundColor(ContextCompat.getColor(applicationContext, com.nexters.ticktock.R.color.btnDisEnable))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.radiogrpQuickTime.setOnCheckedChangeListener(this)

        binding.recyclerviewPrepareTime.layoutManager = LinearLayoutManager(this)
        prepareTimeRecyclerAdapter = PrepareTimeRecyclerAdapter(this, stepList)
        binding.recyclerviewPrepareTime.adapter = prepareTimeRecyclerAdapter

        binding.btnBack.setOnClickListener(this)
        binding.layoutPrepareTime.setOnClickListener(this)
        binding.layoutSave.setOnClickListener(this)
    }

    private fun getData() {
        daySet = intent.getSerializableExtra("daySet") as EnumSet<Day>
        startLocation = intent.getStringExtra("startLocation")
        endLocation = intent.getStringExtra("endLocation")
        travelTime = intent.getSerializableExtra("travelTime") as Time
        endTime = intent.getSerializableExtra("endTime") as Time
        stepList = intent.getSerializableExtra("stepList") as ArrayList<Step>

        startTime = endTime - travelTime

        for (item in stepList) {
            startTime -= item.duration
        }

        originTime = startTime.time
    }

    private fun saveData() {
        val alarmDao = TickTockDBHelper.getInstance(this).alarmDao

        var tickTockColor = when (binding.radiogrpColor.checkedRadioButtonId) {
            binding.radiobtnRed.id -> TickTockColor.RED
            binding.radiobtnGreen.id -> TickTockColor.GREEN
            binding.radiobtnYellow.id -> TickTockColor.YELLOW
            binding.radiobtnBlue.id -> TickTockColor.BLUE
            binding.radiobtnPurple.id -> TickTockColor.PURPLE
            else -> TickTockColor.RED
        }

        val stepListSize = stepList.size
        stepList.add(Step(
                name = "째깍타임",
                duration = Time(quickTime),
                order = stepListSize
        ))

        alarmDao.save(Alarm(
                days = daySet,
                title = binding.edMemo.text.toString(),
                startLocation = startLocation!!,
                endLocation = endLocation!!,
                color = tickTockColor,
                enable = true,
                endTime = endTime,
                travelTime = travelTime,
                steps = stepList.toMutableSet()
        ))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnBack.id -> {
                finish()
            }

            binding.layoutSave.id -> {
                // TODO 저장
                saveData()

                val alarmDao = TickTockDBHelper.getInstance(this).alarmDao

                val calendarSet = Calendar.getInstance()
                val startHour = startTime.time / 60
                val startMinute = startTime.minute

                calendarSet.set(Calendar.HOUR_OF_DAY, startHour)
                calendarSet.set(Calendar.MINUTE, startMinute)

                Log.d("StartAlarm", "$startHour:$startMinute")

                val mAlarmIntent:Intent = Intent(this, AlarmReceiver::class.java)
                val pIntent : PendingIntent = PendingIntent.getBroadcast(this, alarmDao.findAll().size, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val rightNow : Calendar = Calendar.getInstance()

                if(rightNow.timeInMillis < calendarSet.timeInMillis) {

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)
                    if(Build.VERSION.SDK_INT >= 23)
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)

                    else if(Build.VERSION.SDK_INT >= 21)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)

                    else
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSet.timeInMillis, pIntent)
                }

                val intent = Intent(this, CardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    fun textBold(textView: TextView, start: Int, end: Int) {
        val spannable = SpannableString(textView.text)
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    fun textResize(textView: TextView, start: Int, end: Int, size: Int) {
        val spannable = SpannableString(textView.text)
        spannable.setSpan(AbsoluteSizeSpan(size), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group) {
            binding.radiogrpQuickTime -> {
                when (checkedId) {
                    binding.radiobtnQuickTime1.id -> {
                        quickTime = 0
                        val time = Time(originTime - quickTime)
                        binding.tvStartTimeDescription.text = getResizedString("${time.meridiem} " + "*${time.hour}:${time.minute}*", 1.85f)
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, com.nexters.ticktock.R.color.colorWhite))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, com.nexters.ticktock.R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, com.nexters.ticktock.R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, com.nexters.ticktock.R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, com.nexters.ticktock.R.color.colorGray))
                    }
                    binding.radiobtnQuickTime2.id -> {
                        quickTime = 5
                        val time = Time(originTime - quickTime)
                        binding.tvStartTimeDescription.text = getResizedString("${time.meridiem} " + "*${time.hour}:${time.minute}*", 1.85f)
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime3.id -> {
                        quickTime = 10
                        val time = Time(originTime - quickTime)
                        binding.tvStartTimeDescription.text = getResizedString("${time.meridiem} " + "*${time.hour}:${time.minute}*", 1.85f)
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime4.id -> {
                        quickTime = 15
                        val time = Time(originTime - quickTime)
                        binding.tvStartTimeDescription.text = getResizedString("${time.meridiem} " + "*${time.hour}:${time.minute}*", 1.85f)
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime5.id -> {
                        quickTime = 30
                        val time = Time(originTime - quickTime)
                        binding.tvStartTimeDescription.text = getResizedString("${time.meridiem} " + "*${time.hour}:${time.minute}*", 1.85f)
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                    }
                }
            }
        }
    }
}
