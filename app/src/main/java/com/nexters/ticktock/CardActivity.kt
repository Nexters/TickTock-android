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
import com.nexters.ticktock.dao.AlarmDao
import com.nexters.ticktock.dao.TickTockDBHelper
import com.nexters.ticktock.model.Alarm
import com.nexters.ticktock.onboarding.OnBoardingActivity
import com.nexters.ticktock.setting.SettingActivity
import com.nexters.ticktock.utils.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_card.*

class CardActivity : AppCompatActivity() {

    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

    private lateinit var cardContext: CardContext

    private lateinit var alarmDao: AlarmDao

    override fun onResume() {
        super.onResume()

        cardContext.active(alarmDao.findAll()
                .map { it.toCardItem() }
                .toList().sortedWith(compareBy({it.startTime}, {it.color}))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        Location.getInstance(this)
        if (Location.getInstance(this).isGPSConnected())
            locationTxt.text = Location.getInstance(this).getSubString()

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

        cardContext = CardContext(this)
        alarmDao = TickTockDBHelper.getInstance(this).alarmDao
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

                    setAnimationListener(object : AnimationAdapter() {
                        override fun onAnimationRepeat(animation: Animation?) {

                            if (position != RecyclerView.NO_POSITION && position != cardRecyclerViewAdapter.itemCount) {
                                deleteCheckMessageText.text =
                                        getHighlightedString("*${cardContext[position].title}*${resources.getString(R.string.deleteCheckMessage)}")
                            }
                        }
                    })
                })

                Observable.just(alarmDao.delete(removedCard.toAlarm()))
                        .subscribeOn(Schedulers.io())
                        .subscribe()
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

    }

    private fun Alarm.toCardItem() =
            CardItem(
                    id = id,
                    days = days,
                    title = title,
                    startLocation = startLocation,
                    endLocation = endLocation,
                    color = color,
                    enable = enable,
                    endTime = endTime,
                    startTime = endTime - (travelTime + steps.map { it.duration }.fold(Time(0)) { acc, time -> acc + time }),
                    travelTime = travelTime
            )

    private fun CardItem.toAlarm() =
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constant.GPS_ENABLE_REQUEST_CODE -> {
                if (Location.getInstance(this).isGPSConnected()) {
                    Location.getInstance(this).getLocation()
                    locationTxt.text = Location.getInstance(this).getSubString()
                }
            }
        }
    }
}
