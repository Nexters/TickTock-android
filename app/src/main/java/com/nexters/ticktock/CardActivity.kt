package com.nexters.ticktock

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import com.nexters.ticktock.card.CardItem
import com.nexters.ticktock.card.CardRecyclerViewAdapter
import com.nexters.ticktock.card.DividerItemDecoration
import com.nexters.ticktock.dto.DayGroup
import kotlinx.android.synthetic.main.activity_card.*

class CardActivity : OrmAppCompatActivity() {

    private lateinit var cardRecyclerViewAdapter: CardRecyclerViewAdapter

    private lateinit var cardList: MutableList<CardItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        cardList = mutableListOf(
                CardItem("종각역 마이크임팩트", "11:00", "13:30", DayGroup(mutableListOf("월", "수", "금"))),
                CardItem("강남역", "11:00", "13:30", DayGroup(mutableListOf("월", "금"))),
                CardItem("사당역", "11:00", "13:30", DayGroup(mutableListOf("수", "금")))
        )

        cardRecyclerViewAdapter = CardRecyclerViewAdapter(this, cardList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CardActivity, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = cardRecyclerViewAdapter
            LinearSnapHelper().attachToRecyclerView(this)
        }
    }
}
