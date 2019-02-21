package com.nexters.ticktock.card.listener

import com.nexters.ticktock.card.CardItem

interface CardEventListener : EventListener {
    fun onNoCardThere() { }
    fun onCardRemove(position: Int, removedCard: CardItem) { }
    fun onFirstCardAdd(firstCard: CardItem) { }
    fun onCardAdd(addedCard: CardItem) { }
    fun onPhaseChange(isEditPhase: Boolean) { }
    fun onActive() { }
}