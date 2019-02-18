package com.nexters.ticktock.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan


fun getHighlightedString(origin: String): SpannableString {

    val originList = origin.split("*")

    val highlightStart = originList[0].length
    val highlightEnd = originList[1].length + highlightStart

    return SpannableString(origin.replace("*", "")).apply {
        //            val boldFont = Typeface.create(ResourcesCompat.getFont(this@CardActivity, R.font.noto_sans_cjk_kr_bold), Typeface.NORMAL)
//            val highlightFont = Typeface.createFromAsset(resources.assets, "font/noto_sans_cjk_kr_bold.otf")
        setSpan(StyleSpan(Typeface.BOLD), highlightStart, highlightEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}