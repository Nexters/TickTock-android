package com.nexters.ticktock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.nexters.ticktock.alarmsetting.AlarmSettingFirstActivity
import com.nexters.ticktock.card.*
import com.nexters.ticktock.card.Static.MAIN_TOGGLE_DURATION
import com.nexters.ticktock.card.listener.AnimationAdapter
import com.nexters.ticktock.card.listener.CardEventListener
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.onboarding.OnBoardingActivity
import com.nexters.ticktock.utils.*
import com.nexters.ticktock.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_card.*
import java.util.*

class CardActivity : AppCompatActivity() {

    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

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

        mainWord.text = getHighlightedString(resources.getString(R.string.header1))
        requireNewCardTxt.text = getHighlightedString(resources.getString(R.string.requireNewCard))

        val snapHelper = ControllableSnapHelper()

        val cardContext = CardContext(this)

        cardRecyclerViewAdapter = CardRecyclerViewAdapter(this, cardContext, recyclerView, snapHelper)

        val cardEventListener = object : CardEventListener {
            override fun getPriority(): Int = 2

            override fun onPhaseChange(isEditPhase: Boolean) {
                if (isEditPhase) {
                    mainEditPhaseBGtFilterImg.visible(MAIN_TOGGLE_DURATION)
                    mainWord.invisible(MAIN_TOGGLE_DURATION)
                    deleteCheckMessageText.visible(MAIN_TOGGLE_DURATION)
                } else {
                    mainEditPhaseBGtFilterImg.invisible(MAIN_TOGGLE_DURATION)
                    mainWord.visible(MAIN_TOGGLE_DURATION)
                    deleteCheckMessageText.invisible(MAIN_TOGGLE_DURATION)
                }
            }

            override fun onNoCardThere() {
                requireNewCardImg.visible(MAIN_TOGGLE_DURATION)
                requireNewCardTxt.visible(MAIN_TOGGLE_DURATION)
                mainWord.invisible(MAIN_TOGGLE_DURATION)
                mainEditPhaseBGtFilterImg.invisible(MAIN_TOGGLE_DURATION)
                deleteCheckMessageText.invisible(MAIN_TOGGLE_DURATION)
            }

            override fun onFirstCardAdd(firstCard: CardItem) {
                requireNewCardImg.invisible(MAIN_TOGGLE_DURATION)
                requireNewCardTxt.invisible(MAIN_TOGGLE_DURATION)
                mainWord.visible(MAIN_TOGGLE_DURATION)

                deleteCheckMessageText.text = getHighlightedString("*${firstCard.title}*${resources.getString(R.string.deleteCheckMessage)}")
            }

            override fun onCardRemove(position: Int, removedCard: CardItem) {
                deleteCheckMessageText.startAnimation(AlphaAnimation(1F, 0F).apply {
                    duration = 200
                    repeatCount = 1
                    repeatMode = Animation.REVERSE

                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation?) { }
                        override fun onAnimationStart(animation: Animation?) { }
                        override fun onAnimationRepeat(animation: Animation?) {

                            if (position != RecyclerView.NO_POSITION && position != cardRecyclerViewAdapter.itemCount) {
                                deleteCheckMessageText.text =
                                        getHighlightedString("*${cardContext[position].title}*${resources.getString(R.string.deleteCheckMessage)}")
                            }
                        }
                    })
                })
            }
        }

        // TODO deleteCheckMessageText.text = getHighlightedString("*${cardContext[0].title}*${resources.getString(R.string.deleteCheckMessage)}")
        cardContext.addCardEventListener(cardEventListener)

        addBtn.setOnClickListener {
            val intent = Intent(this, AlarmSettingFirstActivity::class.java)
            startActivity(intent)
        }

        editBtn.setOnClickListener {
            if (cardRecyclerViewAdapter.itemCount != 0) {
                cardContext.isEditPhase = true
            }
        }

        mainEditPhaseBGtFilterImg.setOnClickListener {
            if (cardContext.isEditPhase) {
                cardContext.isEditPhase = false
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

                        if (cardContext.isEditPhase) {
                            deleteCheckMessageText.startAnimation(AlphaAnimation(1F, 0F).apply {
                                duration = 200
                                repeatCount = 1
                                repeatMode = Animation.REVERSE

                                setAnimationListener(object : AnimationAdapter() {
                                    override fun onAnimationRepeat(animation: Animation?) {
                                        if (snapPosition != RecyclerView.NO_POSITION) {
                                            deleteCheckMessageText.text =
                                                    getHighlightedString("*${cardContext[snapPosition].title}*${resources.getString(R.string.deleteCheckMessage)}")
                                        }
                                    }
                                })
                            })

                            this.snapPosition = snapPosition
                        } else if (snapPosition != RecyclerView.NO_POSITION) {

                            deleteCheckMessageText.text =
                                    getHighlightedString("*${cardContext[snapPosition].title}*${resources.getString(R.string.deleteCheckMessage)}")
                        }
                    }
                }
            })
        }


//        val alarmDao = TickTockDBHelper.getInstance(this).alarmDao
//
//        alarmDao.deleteAll()
//
//        alarmDao.save(Alarm(
//                days = EnumSet.of(Day.Monday),
//                title = "이제 개발은 그만~",
//                startLocation = "1",
//                endLocation = "2",
//                color = TickTockColor.RED,
//                enable = true,
//                endTime = Time(40),
//                travelTime = Time(60)
//        ))
//
//        Log.d("database", alarmDao.findAll().joinToString { it.title })

        cardContext.active(mutableListOf(
                CardItem(
                        1,
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
                        1,
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
                        1,
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
                        1,
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
                        1,
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
                        1,
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
                        1,
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
                        1,
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
                        1,
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
                        1,
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        TickTockColor.BLUE,
                        true
                )

        ).apply { sortedWith(compareBy({ it.startTime }, { it.title })) })
    }
}
