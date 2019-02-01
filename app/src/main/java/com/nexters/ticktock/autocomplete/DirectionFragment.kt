package com.nexters.ticktock.autocomplete


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.FragmentDirectionBinding

class DirectionFragment : Fragment() {

    private lateinit var binding: FragmentDirectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              bundle: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_direction, container, false)

        val transPath: ArrayList<SearchPubTransPath> = getArguments()!!.getParcelableArrayList("transPath")

        binding.recyclerviewDirection.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewDirection.adapter = DirectionRecyclerAdapter(activity, activity!!.windowManager.defaultDisplay, transPath)

        return binding.root
    }
}
