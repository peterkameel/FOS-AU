package com.peter_kameel.fos_au.ui.fragment.main

import android.Manifest
import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.widget.Toast
import com.peter_kameel.fos_au.R
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.peter_kameel.fos_au.helper.InterConn.InternetConnection
import com.peter_kameel.fos_au.data.util.URLString
import kotlinx.android.synthetic.main.main_fragment.view.*
import kotlinx.android.synthetic.main.main_fragment.view.adView

class MainFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        //download Classrooms file
        view.Classrooms_BU.setOnClickListener {
            when {
                //Check WRITE_EXTERNAL_STORAGE Permission
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED -> {
                    //Request The Permission
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                    )
                }
                //if no internet connection
                !InternetConnection(context).isConnectToInternet -> {
                    Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_LONG).show()
                }
                //if else download the file
                else -> {
                    download(URLString.classroom, "Classrooms.pdf")
                }
            }//End when
        }//End Listener
        //download Guide file
        view.Guide_BU.setOnClickListener {
            when {
                //Check WRITE_EXTERNAL_STORAGE Permission
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED -> {
                    //Request The Permission
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                    )
                }
                //if no internet connection
                !InternetConnection(context).isConnectToInternet -> {
                    Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_LONG).show()
                }
                //if else download the file
                else -> {
                    download(URLString.guide, "Guide.pdf")
                }
            }//End when
        }//End Listener
        //Open Links in Browser
        view.Schedules_BU.setOnClickListener { openUrl(URLString.Schedules) }
        view.Survey_BU.setOnClickListener { openUrl(URLString.Survey) }
        view.Email_BU.setOnClickListener { openUrl(URLString.Email) }
        view.Affairs_BU.setOnClickListener { openUrl(URLString.Affairs) }

        //initialize the banner ade
        MobileAds.initialize(context)
        val adRequest = AdRequest.Builder().build()
        view.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                view.adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: Int) {
                view.adView.visibility = View.GONE
            }
        }
        view.adView.loadAd(adRequest)

        return view
    }

    // Fun used to download classrooms & guide & exams
    private fun download(URL: String, File_name: String) {
        // initialize DownloadManager ang give it the url
        val download: DownloadManager.Request = DownloadManager.Request(Uri.parse(URL))
        //Save file in Downloads directory , You can change it as you like
        download.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File_name)
        //Mack the notification is visible to show the download information
        download.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val dm = requireContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(download)
    }

    //Used to intent url to browser
    private fun openUrl(URL: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
        startActivity(intent)
    }

}