package com.nexters.ticktock.card

import com.nexters.ticktock.card.listener.CardChangeListener
import com.nexters.ticktock.model.enums.Day
import com.nexters.ticktock.model.enums.TickTockColor
import com.nexters.ticktock.utils.Time
import java.util.*

class CardItem(
        val id: Long,
        startTime: Time,
        endTime: Time,
        days: EnumSet<Day>,
        title: String,
        startLocation: String,
        endLocation: String,
        color: TickTockColor,
        enable: Boolean
) {
    var startTime: Time = startTime
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var endTime: Time = endTime
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var days: EnumSet<Day> = days
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var title: String = title
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var startLocation: String = startLocation
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var endLocation: String = endLocation
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var color: TickTockColor = color
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }
    var enable: Boolean = enable
        set(value) {
            field = value
            cardChangeListener?.onCardChange(this@CardItem, CardChangeListener.ENABLE)
        }

    var cardChangeListener: CardChangeListener? = null
}