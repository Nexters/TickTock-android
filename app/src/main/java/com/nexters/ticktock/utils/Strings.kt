package com.nexters.ticktock.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan


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

// 절대 크기 dp
fun getResizedString(origin: String, size: Int): SpannableString {
    val originList = origin.split("*")

    val highlightStart = originList[0].length
    val highlightEnd = originList[1].length + highlightStart

    return SpannableString(origin.replace("*", "")).apply {
        setSpan(AbsoluteSizeSpan(size), highlightStart, highlightEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

// 상대 크기
fun getResizedString(origin: String, size: Float): SpannableString {
    val originList = origin.split("*")

    val highlightStart = originList[0].length
    val highlightEnd = originList[1].length + highlightStart

    return SpannableString(origin.replace("*", "")).apply {
        setSpan(RelativeSizeSpan(size), highlightStart, highlightEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun getUnderlinedString(origin: String): SpannableString {
    val originList = origin.split("*")

    val highlightStart = originList[0].length
    val highlightEnd = originList[1].length + highlightStart

    return SpannableString(origin.replace("*", "")).apply {
        setSpan(UnderlineSpan(), highlightStart, highlightEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}