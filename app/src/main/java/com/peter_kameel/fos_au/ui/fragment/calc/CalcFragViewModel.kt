package com.peter_kameel.fos_au.ui.fragment.calc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalcFragViewModel : ViewModel() {

    val gpaLiveData: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    //Get Data and Calc GPA
    fun calc(LG: String, LH: String, TG: String, TH: String) {
        val points = (LG.toDouble() * LH.toDouble())
        val termPoints = (TG.toDouble() * TH.toDouble())
        val gpa = ((points + termPoints) / (LH.toDouble() + TH.toDouble()))
        gpaLiveData.value = gpa
    }

}