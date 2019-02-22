package com.nexters.ticktock

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nexters.ticktock.databinding.ActivityNoneBinding
import com.nexters.ticktock.utils.getHighlightedString

class NoneActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityNoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_none)

        binding.tvTitle.text = getHighlightedString("*이런..!*\n페이지를\n찾을 수 없어요")

        binding.btnClose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnClose.id -> finish()
        }
    }
}
