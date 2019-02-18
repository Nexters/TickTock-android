package com.nexters.ticktock.onboarding

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nexters.ticktock.R
import com.nexters.ticktock.databinding.FragmentOnboardingScreen1Binding



class OnBoardingScreen1 : Fragment() {

    var closeVp : OnBoardingInterface? = null

    private lateinit var binding : FragmentOnboardingScreen1Binding

    override fun onAttach(activity : Activity) {
        super.onAttach(activity)

        try {
            closeVp = activity as OnBoardingInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement onFragmentChangeListener")
        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_screen1, container, false)

        binding.tutorialX.setOnClickListener {
            closeVp!!.finishOnBoarding()
        }
        return binding.root
    }
}