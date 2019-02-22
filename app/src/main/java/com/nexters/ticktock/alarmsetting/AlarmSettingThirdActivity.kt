package com.nexters.ticktock.alarmsetting

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
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
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.ActivityAlarmSettingThirdBinding
import com.nexters.ticktock.model.Step
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.timer.TimerActivity
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
    private var travelTime: Int? = null
    private lateinit var stepList: ArrayList<Step>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getData()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_setting_third)

        binding.tvTitle.text = getHighlightedString(resources.getString(R.string.tv_alarm_setting_third_title))
        binding.tvStartTimeDescription.text = getResizedString(resources.getString(R.string.tv_start_time_description), 1.85f)

        binding.edMemo.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) binding.tvMemoMax.visibility = View.GONE
                else if (count == 0) binding.tvMemoMax.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.radiogrpQuickTime.setOnCheckedChangeListener(this)

        binding.recyclerviewPrepareTime.layoutManager = LinearLayoutManager(this)
        val arrayList = ArrayList<String>()
        for (i in 0..5)
            arrayList.add(i, "1시간 5분")
        prepareTimeRecyclerAdapter = PrepareTimeRecyclerAdapter(this, arrayList)
        binding.recyclerviewPrepareTime.adapter = prepareTimeRecyclerAdapter

        binding.btnBack.setOnClickListener(this)
        binding.layoutPrepareTime.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    private fun getData() {
        daySet = intent.getSerializableExtra("daySet") as EnumSet<Day>
        startLocation = intent.getStringExtra("startLocation")
        endLocation = intent.getStringExtra("endLocation")
        travelTime = intent.getIntExtra("travelTime", 0)
        stepList = intent.getSerializableExtra("stepList") as ArrayList<Step>
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnBack.id -> {
                finish()
            }

            binding.btnSave.id -> {
                // TODO 저장
                val intent = Intent(this, TimerActivity::class.java)
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
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime2.id -> {
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime3.id -> {
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime4.id -> {
                        binding.radiobtnQuickTime1.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime2.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime3.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                        binding.radiobtnQuickTime4.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                        binding.radiobtnQuickTime5.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
                    }
                    binding.radiobtnQuickTime5.id -> {
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
