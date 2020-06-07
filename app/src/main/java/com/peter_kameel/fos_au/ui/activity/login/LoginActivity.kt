package com.peter_kameel.fos_au.ui.activity.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.peter_kameel.fos_au.R
import com.peter_kameel.fos_au.data.util.ShareTAG
import com.peter_kameel.fos_au.data.util.SharedPrefs
import com.peter_kameel.fos_au.helper.InterConn.InternetConnection
import com.peter_kameel.fos_au.pojo.UserRequest
import com.peter_kameel.fos_au.ui.activity.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.privacy_dialog.*
import kotlinx.android.synthetic.main.signup_dialog.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private val EMAIL_PATTERN =
        "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
    private val pattern: Pattern = Pattern.compile(EMAIL_PATTERN)
    lateinit var userImg: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Skip Login Activity If User Save Login Data By Remember me CheckBox
        checker()

        //read privacy policy
        privacy()

        //Use LoginViewModel
        val model by viewModels<LoginViewModel>()
        //Observe on live data from ViewModel
        model.loginLiveData.observe(this, Observer<UserRequest> {
            when (it.massage) {
                "Login Successfull" -> {
                    //Save User Data in SharedPref
                    SharedPrefs.saveSharedString(this, ShareTAG().userID, it.user.id)
                    SharedPrefs.saveSharedString(this, ShareTAG().userName, it.user.username)
                    SharedPrefs.saveSharedString(this, ShareTAG().userEmail, it.user.email)
                    SharedPrefs.saveSharedString(this, ShareTAG().userPassword, it.user.password)
                    //Check Remember me check box
                    if (checkbox.isChecked) {
                        SharedPrefs.saveSharedBoolean(
                            applicationContext,
                            ShareTAG().userLoginBoolean,
                            true
                        )
                    }
                    //Login && finish this activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                "Check Your Password" -> {
                    password.error = "Check your Password"
                    LoginProgressBar.visibility = GONE
                }
                "Check Your Email" -> {
                    email.error = "Check Your Email"
                    LoginProgressBar.visibility = GONE
                }
                "Error Try Again" -> {
                    Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        })
        //Observe on Sign Up live Data from ViewModel
        model.signUPLiveData.observe(this, Observer<UserRequest> {
            Toast.makeText(applicationContext, it.massage, Toast.LENGTH_LONG).show()
        })
        //Observe on api error massage
        model.errorMassage.observeForever {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        //Call login fun from ViewModel
        loginButton.setOnClickListener {
            //Check internet connection and save it in boolean value
            val check: Boolean = InternetConnection(
                this
            ).isConnectToInternet
            when {
                //check mail format
                !pattern.matcher(emailEditText.text.toString()).matches() -> email.error =
                    "Failed Email"
                //check if username field is empty
                emailEditText.text!!.isEmpty() -> email.error = "This Field Can't be Empty"
                //check if password field is empty
                passwordEditText.text!!.isEmpty() -> password.error = "This Field Can't be Empty"
                //if connection
                check -> {
                    //Progress par
                    LoginProgressBar.visibility = VISIBLE
                    model.login(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                } //use this line to skip the activity
                //if no connection
                else -> Toast.makeText(
                    this,
                    "Check Internet Connection",
                    Toast.LENGTH_LONG
                ).show()
            }//End When
        }

        //Call signUP fun from ViewModel
        signupButton.setOnClickListener {
            //Show SignUp Dialog
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.signup_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //sign up button
            dialog.signup_sign.setOnClickListener {
                //Check internet connection and save it in boolean value
                val check: Boolean = InternetConnection(
                    this
                ).isConnectToInternet
                //check if username field is empty
                when {
                    //check if User Name field is empty
                    dialog.nameEditText_sign.text!!.isEmpty() -> dialog.name_sign.error =
                        "Enter User Name"
                    //check if email field is empty
                    dialog.emailEditText_sign.text!!.isEmpty() -> dialog.email_sign.error =
                        "Enter Email"
                    //check mail format
                    !pattern.matcher(dialog.emailEditText_sign.text.toString())
                        .matches() -> dialog.email_sign.error =
                        "Failed Email"
                    //check if password field is empty
                    dialog.passwordEditText_sign.text!!.isEmpty() -> dialog.password_sign.error =
                        "Enter Password"
                    //if connection
                    check -> {
                        model.signUP(
                            dialog.nameEditText_sign.text.toString(),
                            dialog.emailEditText_sign.text.toString(),
                            dialog.passwordEditText_sign.text.toString()
                        )
                        dialog.dismiss()
                    }
                    //if no connection
                    else -> Toast.makeText(
                        this,
                        "Check Internet Connection",
                        Toast.LENGTH_LONG
                    ).show()
                }//End When
            }
            //cancel button
            dialog.cancel_sign.setOnClickListener { dialog.dismiss() }
            //get the image
            dialog.user_img.setOnClickListener {
                userImg = dialog.user_img
                getImg()
            }
            //show the dialog
            dialog.show()
        }
    }

    //Skip Login Activity If User Save Login Data By Remember me CheckBox
    private fun checker() {
        val check = java.lang.Boolean.valueOf(
            SharedPrefs.readSharedBoolean(
                this,
                ShareTAG().userLoginBoolean,
                false
            )
        )
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(ShareTAG().userLoginBoolean, check)
        //The Value if you click on Login Activity and Set the value is FALSE and whe false the activity will be visible
        if (check) {
            startActivity(intent)
            finish()
        } //If no the Admin Activity not Do Anything
    }

    //fun for privacy policy
    private fun privacy() {
        if (!SharedPrefs.readSharedBoolean(this, ShareTAG().privacyBoolean, false)) {
            login_layout.visibility = GONE
            //Show SignUp Dialog
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.privacy_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //disable accept button
            dialog.accept.isEnabled = false
            //enable or disable accept button
            dialog.accept_checkbox.setOnCheckedChangeListener { _, isChecked ->
                dialog.accept.isEnabled = isChecked
            }
            //on click update user name will change in server and shard File
            dialog.web.loadUrl("file:///android_asset/Privacy.html")
            //if click terms web content
            dialog.terms.setOnClickListener {
                dialog.web.loadUrl("file:///android_asset/terms.html")
            }
            //on click cancel only dismiss the dialog
            dialog.accept.setOnClickListener {
                login_layout.visibility = VISIBLE
                SharedPrefs.saveSharedBoolean(this, ShareTAG().privacyBoolean, true)
                dialog.dismiss()
            }
            dialog.show()
        }
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
                .load(data?.data)
                .centerCrop()
                .error(R.drawable.ic_account_circle_24px)
                .into(userImg)
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

}