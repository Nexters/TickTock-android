package com.nexters.ticktock.card.listener

import com.nexters.ticktock.card.CardItem

interface CardChangeListener : EventListener {

    companion object {
        const val START_TIME = 0
        const val END_TIME = 1
        const val DAYS = 2
        const val TITLE = 3
        const val START_LOCATION = 4
        const val END_LOCATION = 5
        const val COLOR = 6
        const val ENABLE = 7
    }

    fun onCardChange(changedCard: CardItem, diff: Int) { }
}