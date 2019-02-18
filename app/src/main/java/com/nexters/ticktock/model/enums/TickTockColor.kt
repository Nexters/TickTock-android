package com.nexters.ticktock.model.enums

import com.nexters.ticktock.R

enum class TickTockColor(val cardBgColorId: Int, val pickColorId: Int, val cardImgId: Int) {
    RED(
            cardBgColorId = R.color.ticktockRedCardColor,
            pickColorId = R.color.ticktockRedPickColor,
            cardImgId = R.drawable.img_red
    ),
    PURPLE(
            cardBgColorId = R.color.ticktockPurpleCardColor,
            pickColorId = R.color.ticktockPurplePickColor,
            cardImgId = R.drawable.img_purple
    ),
    BLUE(
            cardBgColorId = R.color.ticktockBlueCardColor,
            pickColorId = R.color.ticktockBluePickColor,
            cardImgId = R.drawable.img_blue
    ),
    GREEN(
            cardBgColorId = R.color.ticktockGreenCardColor,
            pickColorId = R.color.ticktockGreenPickColor,
            cardImgId = R.drawable.img_green
    ),
    YELLOW( // TODO 수정 필요
            cardBgColorId = R.color.ticktockYellowCardColor,
            pickColorId = R.color.ticktockYellowPickColor,
            cardImgId = R.drawable.img_green
    )
}