package com.peter_kameel.fos_au.ui.activity.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.data.repository.RetrofitRepository
import com.peter_kameel.fos_au.interfaces.RemoteErrorMassage
import com.peter_kameel.fos_au.interfaces.RetrofitInterface
import com.peter_kameel.fos_au.pojo.UserRequest

class LoginViewModel : ViewModel(), RemoteErrorMassage {

    val loginLiveData: MutableLiveData<UserRequest> by lazy { MutableLiveData<UserRequest>() }
    val signUPLiveData: MutableLiveData<UserRequest> by lazy { MutableLiveData<UserRequest>() }
    val errorMassage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val repository by lazy {
        RetrofitRepository(RetrofitInterface.invoke())
    }

    //Login fun
    fun login(email: String, password: String) {
        CoroutineHelper.ioToMain({ repository.login(email, password) },
            { it?.let { it1 -> loginLiveData.postValue(it1) } })
    }

    //Sign UP fun
    fun signUP(username: String, email: String, password: String) {
        CoroutineHelper.ioToMain({ repository.signUp(username, email, password) },
            { it?.let { it1 -> signUPLiveData.postValue(it1) } })
    }

    //forget password fun
    fun forget(email: String) {
        CoroutineHelper.ioToMain(
            { repository.forget(email) },
            { loginLiveData.postValue(it) })
    }

    override fun onError(msg: String) {
        errorMassage.postValue(msg)
    }


}



