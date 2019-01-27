package com.nexters.ticktock.card

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nexters.ticktock.R

class CardRecyclerViewAdapter(
        context: Context,
        val cardList: MutableList<CardItem>
) : RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val titleView = view.findViewById<TextView>(R.id.title)
        private val leadTimeView = view.findViewById<TextView>(R.id.leadTime)
        private val daysView = view.findViewById<TextView>(R.id.days)

        fun setData() {
            this@CardRecyclerViewAdapter.cardList[super.getAdapterPosition()]
                    .also {
                        titleView.text = it.title
                        leadTimeView.text = it.getTime()
                        daysView.text = it.days.toString()
                    }
        }
    }

    override fun getItemCount(): Int =
            cardList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(layoutInflater.inflate(R.layout.item_card, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) =
            viewHolder.setData()

}