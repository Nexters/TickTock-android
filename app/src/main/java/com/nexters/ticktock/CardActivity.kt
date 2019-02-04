package com.nexters.ticktock

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.nexters.ticktock.card.*
import com.nexters.ticktock.dto.DayGroup
import kotlinx.android.synthetic.main.activity_card.*

class CardActivity : OrmAppCompatActivity() {

    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

    private lateinit var cardList: MutableList<CardItem>

    companion object {
        const val DURATION_OF_BG_TRANSITION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        header.text = resources.getString(R.string.header1)

        cardList = mutableListOf(
                CardItem("종각역 마이크임팩트", "15:00", "20", DayGroup(mutableListOf("월", "수", "금")), "어머나 세상에 이런 일이"),
                CardItem("강남역", "12:00", "60", DayGroup(mutableListOf("월", "금")), "경무와 약속"),
                CardItem("사당역", "17:00", "30", DayGroup(mutableListOf("수", "금")), "PM 님과 약속")
        )

        deleteCheckMessageText.text = String.format(resources.getString(R.string.deleteCheckMessage), cardList[0].destination)

        cardRecyclerViewAdapter = CardRecyclerViewAdapter(this, cardList, recyclerView).apply {
            this.setOnCardLongClickListener {
                (activity_card_layout.background as TransitionDrawable).startTransition(DURATION_OF_BG_TRANSITION)
                addBtn.visibility = View.INVISIBLE
                settingBtn.visibility = View.INVISIBLE
                header.visibility = View.INVISIBLE
                deleteCheckMessageText.visibility = View.VISIBLE
                this.isDeletePhase = true
            }
        }

        val snapHelper = ControllableSnapHelper()

        recyclerView.apply {
            layoutManager = SpeedControllableLinearLayoutManager(
                    this@CardActivity, LinearLayoutManager.HORIZONTAL, false, this, 50F)
            addItemDecoration(OffsetItemDecoration(this@CardActivity))
            itemAnimator = DefaultItemAnimator()
            adapter = cardRecyclerViewAdapter
            snapHelper.attachToRecyclerView(this)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var snapPosition = RecyclerView.NO_POSITION
                val anim = AlphaAnimation(1F, 0F).apply {
                    duration = 200
                    repeatCount = 1
                    repeatMode = Animation.REVERSE

                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation?) { }
                        override fun onAnimationStart(animation: Animation?) { }
                        override fun onAnimationRepeat(animation: Animation?) {
                            deleteCheckMessageText.text = String.format(resources.getString(R.string.deleteCheckMessage), cardList[snapPosition].destination)
                        }
                    })
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val snapPosition = snapHelper.getAdapterSnapPosition(recyclerView)
                    val snapPositionChanged = this.snapPosition != snapPosition
                    if (snapPositionChanged && cardRecyclerViewAdapter.isDeletePhase) {

                        deleteCheckMessageText.startAnimation(anim)

                        this.snapPosition = snapPosition
                    }
                }
            })
        }

        activity_card_layout.setOnClickListener {
            if (cardRecyclerViewAdapter.isDeletePhase) {
                (it.background as TransitionDrawable).reverseTransition(DURATION_OF_BG_TRANSITION)
                addBtn.visibility = View.VISIBLE
                settingBtn.visibility = View.VISIBLE
                header.visibility = View.VISIBLE
                deleteCheckMessageText.visibility = View.INVISIBLE
                cardRecyclerViewAdapter.isDeletePhase = false
            }
        }
    }

    fun SnapHelper.getAdapterSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return recyclerView.getChildAdapterPosition(snapView)
    }
}
