package com.peter_kameel.fos_au.ui.fragment.calc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.peter_kameel.fos_au.R
import kotlinx.android.synthetic.main.calc_fragment.*
import kotlinx.android.synthetic.main.calc_fragment.view.*
import kotlinx.android.synthetic.main.main_fragment.view.*

class CalcFrag : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calc_fragment, container, false)
        //Use ViewModel
        val calcViewModel = ViewModelProviders.of(this).get(CalcFragViewModel::class.java)
        //Observe on live data
        calcViewModel.gpaLiveData.observe(viewLifecycleOwner, Observer<Double> {
            //Show GPA
            val snack = Snackbar.make(view, "GPA : $it", Snackbar.LENGTH_INDEFINITE)
            snack.setAction("Close", View.OnClickListener { snack.dismiss() })
            snack.show()
        })

        //initialize the banner ade
        MobileAds.initialize(context)
        val adRequest = AdRequest.Builder().build()
        view.adView3.adListener = object : AdListener() {
            override fun onAdLoaded() {
                view.adView3.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: Int) {
                view.adView3.visibility = View.GONE
            }
        }
        view.adView3.loadAd(adRequest)


        //First Check if their is data then Validate it And Calc GPA
        view.Calc_BU.setOnClickListener {
            when {
                //Check if last GPA is empty
                view.Last_GPA.text.isNullOrEmpty() -> view.Last_GPA_layout.error =
                    "You Must Fill This Field"
                //Validate Last GPA Text
                view.Last_GPA.text.toString().toDouble() <= 0 || view.Last_GPA.text.toString()
                    .toDouble() > 4 -> {
                    view.Last_GPA_layout.error = "Must Number between [0..4]"
                }
                //Check if last Hours is empty
                view.Last_Hours.text.isNullOrEmpty() -> view.Last_Hours_layout.error =
                    "You Must Fill This Field"
                //Validate Last Hours
                view.Last_Hours.text.toString().toInt() <= 0 -> view.Last_Hours_layout.error =
                    "Must be at least 1 hour"
                //Check if Term GPA is empty
                view.Term_GPA.text.isNullOrEmpty() -> view.Term_GPA_layout.error =
                    "You Must Fill This Field"
                //Validate Term GPA
                view.Term_GPA.text.toString().toDouble() <= 0 || view.Term_GPA.text.toString()
                    .toDouble() > 4 ->
                    view.Term_GPA_layout.error = "Must Number between [0..4]"
                //Check if Term Hours is empty
                view.Term_Hours.text.isNullOrEmpty() -> view.Term_Hours_layout.error =
                    "You Must Fill This Field"
                //Validate Term Hours
                view.Term_Hours.text.toString().toInt() < 0 || view.Term_Hours.text.toString()
                    .toInt() > 21 ->
                    view.Term_Hours_layout.error = "Must be between [0..21]"
                // if all is not empty tack the data and calc gpa
                else -> {
                    calcViewModel.calc(
                        Last_GPA.text.toString(),
                        Last_Hours.text.toString(),
                        Term_GPA.text.toString(),
                        Term_Hours.text.toString()
                    )//End Calc
                }//End else
            }//End when
        }//End OnClick
        return view
    }

}