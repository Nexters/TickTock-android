package com.nexters.ticktock.setting

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import com.nexters.ticktock.R
import com.nexters.ticktock.dao.TickTockDBHelper


class AlarmDeleteDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.setting_dialog)

        val deleteBtn = findViewById<ImageButton>(R.id.btn_delete_alarm)
        val cancelBtn = findViewById<ImageButton>(R.id.btn_cancle_dialog)

        deleteBtn.setOnClickListener {
            //db 모든 알람 삭제
            val alarmDao = TickTockDBHelper.getInstance(context).alarmDao
            alarmDao.deleteAll()
            dismiss()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}