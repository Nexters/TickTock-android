package com.nexters.ticktock

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.nexters.ticktock.card.*
import com.nexters.ticktock.card.Static.MAIN_TOGGLE_DURATION
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.utils.hour
import com.nexters.ticktock.utils.invisible
import com.nexters.ticktock.utils.minute
import com.nexters.ticktock.utils.visible
import kotlinx.android.synthetic.main.activity_card.*
import java.util.*

class CardActivity : OrmAppCompatActivity() {

    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

    private lateinit var cardList: MutableList<CardItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        mainWord.text = getHighlightedString(resources.getString(R.string.header1))

        cardList = mutableListOf(
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.MONDAY, Day.Thursday, Day.Friday),
                        "출근 알림알림알림",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        "GREEN",
                        true
                ),
                CardItem(
                        15.hour() + 30.minute(),
                        19.hour() + 30.minute(),
                        EnumSet.of(Day.MONDAY, Day.Saturday),
                        "관악구 신림동은 누구 집이야?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        "GREEN",
                        false
                ),
                CardItem(
                        7.hour(),
                        8.hour() + 30.minute(),
                        EnumSet.of(Day.MONDAY, Day.Friday),
                        "ㅎㅎㅎㅎㅎ",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        "GREEN",
                        true
                ),
                CardItem(
                        11.hour() + 30.minute(),
                        12.hour() + 30.minute(),
                        EnumSet.of(Day.Wednesday),
                        "개발 다 끝난 거지?",
                        "관악구 신림동 1423-8",
                        "사당역 투썸플레이스",
                        "GREEN",
                        true
                )
        )

        deleteCheckMessageText.text = getHighlightedString("@${cardList[0].title}@${resources.getString(R.string.deleteCheckMessage)}")

        val snapHelper = ControllableSnapHelper()

        cardRecyclerViewAdapter = CardRecyclerViewAdapter(this, cardList, recyclerView, snapHelper).apply {
            setOnCardLongClickListener {
                mainEditPhaseBGtFilterImg.visible(MAIN_TOGGLE_DURATION)
                mainWord.invisible(MAIN_TOGGLE_DURATION)
                deleteCheckMessageText.visible(MAIN_TOGGLE_DURATION)
                this.isEditPhase = true
            }
        }

        editBtn.setOnClickListener {
            mainEditPhaseBGtFilterImg.visible(MAIN_TOGGLE_DURATION)
            mainWord.invisible(MAIN_TOGGLE_DURATION)
            deleteCheckMessageText.visible(MAIN_TOGGLE_DURATION)
            cardRecyclerViewAdapter.isEditPhase = true
        }

        mainEditPhaseBGtFilterImg.setOnClickListener {
            if (cardRecyclerViewAdapter.isEditPhase) {
                mainEditPhaseBGtFilterImg.invisible(MAIN_TOGGLE_DURATION)
                mainWord.visible(MAIN_TOGGLE_DURATION)
                deleteCheckMessageText.invisible(MAIN_TOGGLE_DURATION)
                cardRecyclerViewAdapter.isEditPhase = false
            }
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

                    if (snapPositionChanged && cardRecyclerViewAdapter.isEditPhase) {

                        deleteCheckMessageText.startAnimation(AlphaAnimation(1F, 0F).apply {
                            duration = 200
                            repeatCount = 1
                            repeatMode = Animation.REVERSE

                            setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationEnd(animation: Animation?) { }
                                override fun onAnimationStart(animation: Animation?) { }
                                override fun onAnimationRepeat(animation: Animation?) {
                                    if (snapPosition != RecyclerView.NO_POSITION) {
                                        deleteCheckMessageText.text =
                                                getHighlightedString("@${cardList[snapPosition].title}@${resources.getString(R.string.deleteCheckMessage)}")
                                    }
                                }
                            })
                        })

                        this.snapPosition = snapPosition

                    } else if (snapPosition != RecyclerView.NO_POSITION) {
                        deleteCheckMessageText.text =
                                getHighlightedString("@${cardList[snapPosition].title}@${resources.getString(R.string.deleteCheckMessage)}")
                    }
                }
            })
        }
    }

    private fun getHighlightedString(origin: String): SpannableString {

        val originList = origin.split("@")

        val highlightStart = originList[0].length
        val highlightEnd = originList[1].length + highlightStart

        return SpannableString(origin.replace("@", "")).apply {
//            val boldFont = Typeface.create(ResourcesCompat.getFont(this@CardActivity, R.font.noto_sans_cjk_kr_bold), Typeface.NORMAL)
//            val highlightFont = Typeface.createFromAsset(resources.assets, "font/noto_sans_cjk_kr_bold.otf")
            setSpan(StyleSpan(Typeface.BOLD), highlightStart, highlightEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
