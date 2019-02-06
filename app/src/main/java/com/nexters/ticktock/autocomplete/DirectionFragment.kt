package com.nexters.ticktock.autocomplete


import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.FragmentDirectionBinding

class DirectionFragment : Fragment() {

    private lateinit var binding: FragmentDirectionBinding
    private lateinit var transPath: ArrayList<SearchPubTransPath>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              bundle: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_direction, container, false)

        transPath = getArguments()!!.getParcelableArrayList("transPath")

        binding.recyclerviewDirection.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewDirection.adapter = DirectionRecyclerAdapter(activity, activity!!.windowManager.defaultDisplay, transPath)
        binding.recyclerviewDirection.addOnItemTouchListener(RecyclerItemClickListener(activity, binding.recyclerviewDirection, object: RecyclerItemClickListener.OnItemClickListener {

            /*
             * DirectionRecyclerAdapter(길찾기 어댑터) 클릭
             */
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                intent.putExtra("totalTime", transPath.get(position).path.totalTime)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }

            override fun onLongItemClick(view: View, position: Int) {}
        }))

        return binding.root
    }
}
