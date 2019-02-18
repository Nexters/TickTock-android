package com.nexters.ticktock.card

import android.view.View

interface CardEventListener {
    fun onCardLongClick(view: View?)
    fun onNoCardThere(adapter: CardRecyclerViewAdapter)
    fun onCardDelete(position: Int, adapter: CardRecyclerViewAdapter)
    fun onFirstCardAdd(adapter: CardRecyclerViewAdapter)
}