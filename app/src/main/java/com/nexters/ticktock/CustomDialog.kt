package com.nexters.ticktock

import android.app.Activity
import android.app.Dialog
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager

class CustomDialog : View.OnClickListener {
    var activity: Activity

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun callFunction() {
        val dialog = Dialog(activity)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.component_custom_dialog)
        dialog.setCanceledOnTouchOutside(true) // 바깥쪽 터치시 사라짐

        val displayMetrics:DisplayMetrics = activity.applicationContext.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val layoutParams:WindowManager.LayoutParams = dialog.window.attributes
        layoutParams.copyFrom(dialog.window.attributes)
        layoutParams.width = width / 6 * 5
        layoutParams.height = height / 4

        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.radioBtn1 -> {

            }
        }
    }
}