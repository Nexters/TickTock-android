package com.nexters.ticktock.autocomplete

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class DirectionPagerAdapter: FragmentPagerAdapter {

    var data1: Fragment = DirectionFragment()
    var data2: Fragment = DirectionFragment()

    var mData: ArrayList<Fragment> = arrayListOf(data1, data2)

    constructor(fm: FragmentManager, transPath:ArrayList<SearchPubTransPath>): super(fm) {
        val bundle =  Bundle(1)
        bundle.putParcelableArrayList("transPath", transPath)
        data1.arguments = bundle
        data2.arguments = bundle
    }

    override fun getItem(position: Int): Fragment {
        return mData.get(position)
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var result: String? = null
        when (position) {
            // TODO 대중교통 길찾기이기 때문에 자동차는 없음
            // 버스 또는 지하철로 필터링은 가능
            0 -> result = "최단시간"
            1 -> result = "최소환승"
        }
        return result
    }
}