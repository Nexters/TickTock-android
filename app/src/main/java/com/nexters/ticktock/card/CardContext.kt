package com.nexters.ticktock.card

import android.content.Context
import com.nexters.ticktock.card.listener.CardEventListener
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CardContext(val context: Context) : ArrayList<CardItem>() {

    private val cardEventListenerList: MutableList<CardEventListener> = mutableListOf()

    var isEditPhase: Boolean = false
        set(value) {
            field = value
            Flowable.fromIterable(cardEventListenerList)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it.onPhaseChange(value)
                    }
        }

    fun active(cardList: List<CardItem> = listOf()) {

        if (cardList.any { it.id == 0L }) {
            throw IllegalArgumentException("CardContext 의 초기 값은 DB 에서 가져와야 해요")
        }

        if (cardList.isEmpty()) {
            Flowable.fromIterable(cardEventListenerList)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { it.onNoCardThere() }
        }

        super.clear()
        super.addAll(cardList)
    }

    override fun add(element: CardItem): Boolean {
        return super.add(element).apply {
            this@CardContext.sortedWith(compareBy({ it.startTime }, { it.color }, { it.title }))
            val itemCount = size

            Flowable.fromIterable(cardEventListenerList)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it.onCardAdd(element)

                        if (itemCount == 1) {
                            it.onFirstCardAdd(element)
                        }
                    }
        }
    }

    override fun removeAt(index: Int): CardItem {
        return super.removeAt(index).apply {
            cardEventListenerList.forEach { it.onCardRemove(index, this) }
        }.also {
            if (size == 0) {
                Flowable.fromIterable(cardEventListenerList)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { it.onNoCardThere() }
            }
        }
    }

    fun addCardEventListener(cardEventListener: CardEventListener) {
        this.cardEventListenerList.add(cardEventListener)
        cardEventListenerList.sortBy { -it.getPriority() }
    }
}