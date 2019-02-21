package com.nexters.ticktock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.nexters.ticktock.card.*
import com.nexters.ticktock.card.Static.MAIN_TOGGLE_DURATION
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.onboarding.OnBoardingActivity
import com.nexters.ticktock.utils.*
import com.nexters.ticktock.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_card.*
import java.util.*

class CardActivity : AppCompatActivity() {

    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

    private lateinit var cardList: MutableList<CardItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        //set OnBoarding Tutorial
        val preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        if(!preferences.getBoolean("onboarding_complete", false)) {

            val onboarding = Intent(this, OnBoardingActivity::class.java)
            startActivity(onboarding)

            finish()
            return
        }

        val alarmDao = TickTockDBHelper.getInstance(this).alarmDao

        alarmDao.save(Alarm(
                days = EnumSet.of(Day.Monday),
                title = "이제 개발은 그만~",
                startLocation = "1",
                endLocation = "2",
                color = TickTockColor.RED,
                enable = true,
                endTime = Time(40),
                travelTime = Time(60)
        ))

        Log.d("database", alarmDao.findAll().joinToString { it.title })

        mainWord.text = getHighlightedString(resources.getString(R.string.header1))
        requireNewCardTxt.text = getHighlightedString(resources.getString(R.string.requireNewCard))

        cardList = mutableListOf(
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Monday, Day.Thursday, Day.Friday),
                        "출근 알림알림알림",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.GREEN,
                        true
                ),
                CardItem(
                        15.hour() + 30.minute(),
                        19.hour() + 30.minute(),
                        EnumSet.of(Day.Monday, Day.Saturday, Day.Sunday),
                        "신림동은 누구 집이야?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.PURPLE,
                        false
                ),
                CardItem(
                        7.hour(),
                        8.hour() + 30.minute(),
                        EnumSet.of(Day.Monday, Day.Tuesday, Day.Wednesday, Day.Thursday, Day.Friday),
                        "ㅎㅎㅎㅎㅎ",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.RED,
                        true
                ),
                CardItem(
                        2.hour() + 30.minute(),
                        3.hour() + 10.minute(),
                        EnumSet.of(Day.Wednesday, Day.Sunday),
                        "새벽에 어딜 가려고",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.PURPLE,
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.BLUE,
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.BLUE,
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.GREEN,
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.RED,
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.BLUE,
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.BLUE,
                        true
                )

        ).apply { sortedWith(compareBy({ it.startTime }, { it.title })) }

        deleteCheckMessageText.text = getHighlightedString("*${cardList[0].title}*${resources.getString(R.string.deleteCheckMessage)}")

        val snapHelper = ControllableSnapHelper()

        cardRecyclerViewAdapter = CardRecyclerViewAdapter(this, cardList, recyclerView, snapHelper).apply {
            onCardEventListener = object : CardEventListener {
                override fun onCardLongClick(view: View?) {
                    mainEditPhaseBGtFilterImg.visible(MAIN_TOGGLE_DURATION)
                    mainWord.invisible(MAIN_TOGGLE_DURATION)
                    deleteCheckMessageText.visible(MAIN_TOGGLE_DURATION)
                }

                override fun onNoCardThere(adapter: CardRecyclerViewAdapter) {
                    requireNewCardImg.visible(MAIN_TOGGLE_DURATION)
                    requireNewCardTxt.visible(MAIN_TOGGLE_DURATION)
                    mainWord.invisible(MAIN_TOGGLE_DURATION)
                    mainEditPhaseBGtFilterImg.invisible(MAIN_TOGGLE_DURATION)
                    deleteCheckMessageText.invisible(MAIN_TOGGLE_DURATION)
                }

                override fun onFirstCardAdd(adapter: CardRecyclerViewAdapter) {
                    requireNewCardImg.invisible(MAIN_TOGGLE_DURATION)
                    requireNewCardTxt.invisible(MAIN_TOGGLE_DURATION)
                    mainWord.visible(MAIN_TOGGLE_DURATION)
                }

                override fun onCardDelete(position: Int, adapter: CardRecyclerViewAdapter) {
                    deleteCheckMessageText.startAnimation(AlphaAnimation(1F, 0F).apply {
                        duration = 200
                        repeatCount = 1
                        repeatMode = Animation.REVERSE

                        setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationEnd(animation: Animation?) { }
                            override fun onAnimationStart(animation: Animation?) { }
                            override fun onAnimationRepeat(animation: Animation?) {

                                if (position != RecyclerView.NO_POSITION && position != adapter.itemCount) {
                                    deleteCheckMessageText.text =
                                            getHighlightedString("*${cardList[position].title}*${resources.getString(R.string.deleteCheckMessage)}")
                                }
                            }
                        })
                    })
                }
            }
        }

        addBtn.setOnClickListener{
            val alarmSettingIntent = Intent(this, MainDetailActivity::class.java)
            startActivity(alarmSettingIntent)
        }

        editBtn.setOnClickListener {
            if (cardRecyclerViewAdapter.itemCount != 0) {
                mainEditPhaseBGtFilterImg.visible(MAIN_TOGGLE_DURATION)
                mainWord.invisible(MAIN_TOGGLE_DURATION)
                deleteCheckMessageText.visible(MAIN_TOGGLE_DURATION)
                cardRecyclerViewAdapter.isEditPhase = true
            }
        }

        mainEditPhaseBGtFilterImg.setOnClickListener {
            if (cardRecyclerViewAdapter.isEditPhase) {
                mainEditPhaseBGtFilterImg.invisible(MAIN_TOGGLE_DURATION)
                mainWord.visible(MAIN_TOGGLE_DURATION)
                deleteCheckMessageText.invisible(MAIN_TOGGLE_DURATION)
                cardRecyclerViewAdapter.isEditPhase = false
            }
        }

        settingBtn.setOnClickListener {
            val settingIntent = Intent(this, SettingActivity::class.java)
            startActivity(settingIntent)
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right)
        }

        recyclerView.apply { // 클래스 분리 해야 하는데 귀찮... ㅎㅎ
            layoutManager = SpeedControllableLinearLayoutManager(
                    this@CardActivity, LinearLayoutManager.HORIZONTAL, false, this, 50F)
            addItemDecoration(CardIndicatorDecoration(this@CardActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = cardRecyclerViewAdapter
            snapHelper.attachToRecyclerView(this)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var snapPosition = RecyclerView.NO_POSITION

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val snapPosition = snapHelper.getAdapterSnapPosition()
                    val snapPositionChanged = this.snapPosition != snapPosition

                    if (snapPositionChanged) {

                        if (cardRecyclerViewAdapter.isEditPhase) {
                            deleteCheckMessageText.startAnimation(AlphaAnimation(1F, 0F).apply {
                                duration = 200
                                repeatCount = 1
                                repeatMode = Animation.REVERSE

                                setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationEnd(animation: Animation?) {}
                                    override fun onAnimationStart(animation: Animation?) {}
                                    override fun onAnimationRepeat(animation: Animation?) {
                                        if (snapPosition != RecyclerView.NO_POSITION) {
                                            deleteCheckMessageText.text =
                                                    getHighlightedString("*${cardList[snapPosition].title}*${resources.getString(R.string.deleteCheckMessage)}")
                                        }
                                    }
                                })
                            })

                            this.snapPosition = snapPosition
                        } else if (snapPosition != RecyclerView.NO_POSITION) {

                            deleteCheckMessageText.text =
                                    getHighlightedString("*${cardList[snapPosition].title}*${resources.getString(R.string.deleteCheckMessage)}")
                        }
                    }
                }
            })
        }
    }
}
