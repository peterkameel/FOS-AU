package com.peter_kameel.fos_au.ui.activity.main

import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.peter_kameel.fos_au.R
import com.peter_kameel.fos_au.service.SyncService
import com.peter_kameel.fos_au.data.util.ShareTAG
import com.peter_kameel.fos_au.data.util.SharedPrefs
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.helper.InterConn.InternetConnection
import com.peter_kameel.fos_au.ui.activity.login.LoginActivity
import com.peter_kameel.fos_au.ui.fragment.calc.CalcFrag
import com.peter_kameel.fos_au.ui.fragment.gpa.GPAFrag
import com.peter_kameel.fos_au.ui.fragment.main.MainFrag
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import kotlinx.android.synthetic.main.inter_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.privacy_dialog.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var toggel: ActionBarDrawerToggle
    private val model by viewModels<MainActivityViewModel>()
    private lateinit var img: ImageView
    private val scope by lazy { CoroutineScope(Dispatchers.IO) }
    private val check: Boolean by lazy { InternetConnection(this).isConnectToInternet }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggel = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggel)
        toggel.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //setup navigation bottom
        BNV.setOnNavigationItemSelectedListener(listener)
        supportFragmentManager.beginTransaction().replace(R.id.container, MainFrag()).commit()
        //On Item selected for navigation
        navigation.setNavigationItemSelectedListener(this)
        //check backup switch
        val backupSwitch = navigation.menu.findItem(R.id.backup)
            .actionView
            .findViewById<Switch>(R.id.backup_Switch)
        backupSwitch.isChecked =
            SharedPrefs.readSharedBoolean(this, ShareTAG().syncServiceBoolean, false)
        backupSwitch.setOnCheckedChangeListener { _: CompoundButton, check: Boolean ->
            if (check) {
                SharedPrefs.saveSharedBoolean(this, ShareTAG().syncServiceBoolean, true)
            } else {
                SharedPrefs.saveSharedBoolean(this, ShareTAG().syncServiceBoolean, false)
            }
        }
        //Set content of navigation header
        val header: View = navigation.getHeaderView(0)
        //set header data (Name && Email && Profile photo)
        header.navegationUserName.text =
            SharedPrefs.readSharedString(this, ShareTAG().userName, "test name")
        header.navegationUserEmail.text =
            SharedPrefs.readSharedString(this, ShareTAG().userEmail, "test email")
        img = header.img_view
        //show the user image
        Glide.with(this)
            .load(SharedPrefs.readSharedString(this, ShareTAG().userImageUri, ""))
            .centerCrop()
            .error(R.drawable.ic_account_circle_24px)
            .into(header.img_view)
        //Observe on restore massage if completed or not
        model.restoreLiveData.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        //Observe on User Data Changed
        model.userLiveData.observeForever {
            Toast.makeText(this, it.massage, Toast.LENGTH_SHORT).show()
            if (!it.user.username.isNullOrEmpty()) {
                SharedPrefs.saveSharedString(this, ShareTAG().userName, it.user.username)
            } else if (!it.user.password.isNullOrEmpty()) {
                SharedPrefs.saveSharedString(this, ShareTAG().userPassword, it.user.password)
            }
        }

        //start the sync service
        val service = ComponentName(this, SyncService::class.java)
        val job = JobInfo.Builder(1, service)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .build()
        val jobSchedulers: JobScheduler =
            getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobSchedulers.schedule(job)

    }

    private val listener = BottomNavigationView.OnNavigationItemSelectedListener {
        var fragment = Fragment()
        when (it.itemId) {
            R.id.navigation_home -> fragment = MainFrag()
            R.id.navigation_dashboard -> fragment = CalcFrag()
            R.id.navigation_gpa -> fragment = GPAFrag()
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        return@OnNavigationItemSelectedListener true
    }

    //On toggle selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggel.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //when item selected from navigation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
            R.id.restor -> {
                if (check) {
                    scope.launch { model.restoreData() }
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.picture -> {
                getImg()
            }
            R.id.name -> changeName()
            R.id.password -> {
                changePassword()
            }
            R.id.privacyPolicy -> {
                privacy()
            }
        }
        return false
    }


    //fun to logout
    private fun logout() {
        //for not skipping login activity
        SharedPrefs.saveSharedBoolean(this, ShareTAG().userLoginBoolean, false)
        //Delete User information
        SharedPrefs.saveSharedString(this, ShareTAG().userID, "")
        SharedPrefs.saveSharedString(this, ShareTAG().userName, "")
        SharedPrefs.saveSharedString(this, ShareTAG().userEmail, "")
        SharedPrefs.saveSharedString(this, ShareTAG().userPassword, "")
        //Move to Login activity
        startActivity(Intent(this, LoginActivity::class.java))
        //close this activity
        finish()
    }

    //fun to change user name when User Name item selected from navigation
    private fun changeName() {
        //Show SignUp Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.inter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //change dialog hints
        dialog.EnterTextLayout.hint = "Enter New User Name"
        //on click update user name will change in server and shard File
        dialog.inter.setOnClickListener {
            if (!dialog.EnterText.text.isNullOrEmpty()) {
                if (check) {
                    CoroutineHelper.ioToMain(
                        {
                            model.updateName(
                                SharedPrefs.readSharedString(
                                    this@MainActivity,
                                    ShareTAG().userEmail,
                                    ""
                                ),
                                dialog.EnterText.text.toString()
                            )
                        },
                        { dialog.dismiss() })
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }//end if
        }
        //on click cancel only dismiss the dialog
        dialog.interCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //fun to change user password when password item selected from navigation
    private fun changePassword() {
        var x = 0
        val pass = SharedPrefs.readSharedString(this, ShareTAG().userPassword, "")
        var newPassword = pass
        //Show SignUp Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.inter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //change dialog hints
        dialog.EnterTextLayout.hint = "Enter Your password"
        //on click update user name will change in server and shard File
        dialog.inter.setOnClickListener {
            when (x) {
                0 -> {
                    if (dialog.EnterText.text.toString() == pass) {
                        x = 1
                        dialog.EnterText.text = null
                        dialog.EnterTextLayout.hint = "Enter New Password"
                    } else {
                        dialog.EnterTextLayout.error = "Failed Password"
                    }
                }
                1 -> {
                    if (!dialog.EnterText.text.isNullOrEmpty()) {
                        x = 2
                        newPassword = dialog.EnterText.text.toString()
                        dialog.EnterText.text = null
                        dialog.EnterTextLayout.hint = "Confirm Your Password"
                    } else {
                        dialog.EnterTextLayout.error = "Enter Your New Password"
                    }
                }
                2 -> {
                    if (dialog.EnterText.text.toString() == newPassword) {
                        if (check) {
                            CoroutineHelper.ioToMain(
                                {
                                    model.updatePassword(
                                        SharedPrefs.readSharedString(
                                            this,
                                            ShareTAG().userEmail,
                                            ""
                                        ),
                                        SharedPrefs.readSharedString(
                                            this,
                                            ShareTAG().userPassword,
                                            ""
                                        ),
                                        newPassword
                                    )
                                },
                                { dialog.dismiss() })
                        } else {
                            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        dialog.EnterTextLayout.error = "Password Not Matched"
                    }
                }
            }//end when
        }
        //on click cancel only dismiss the dialog
        dialog.interCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //fun to show privacy policy
    private fun privacy() {
        //Show SignUp Dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.privacy_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //on click update user name will change in server and shard File
        dialog.web.loadUrl("file:///android_asset/Privacy.html")
        //on click cancel only dismiss the dialog
        dialog.accept.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    //fun to change profile img
    private fun getImg() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            //Show the img in img View
            SharedPrefs.saveSharedString(this, ShareTAG().userImageUri, data?.data.toString())
            Glide.with(this)
                .load(SharedPrefs.readSharedString(this, ShareTAG().userImageUri, ""))
                .centerCrop()
                .error(R.drawable.ic_account_circle_24px)
                .into(img)
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }
}