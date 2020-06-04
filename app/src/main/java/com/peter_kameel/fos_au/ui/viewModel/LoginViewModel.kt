package com.peter_kameel.fos_au.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.data.repository.RetrofitRepository
import com.peter_kameel.fos_au.interfaces.RetrofitInterface
import com.peter_kameel.fos_au.pojo.UserRequest

class LoginViewModel : ViewModel() {

    val loginLiveData: MutableLiveData<UserRequest> by lazy { MutableLiveData<UserRequest>() }
    val signUPLiveData: MutableLiveData<UserRequest> by lazy { MutableLiveData<UserRequest>() }
    private val repository by lazy {
        RetrofitRepository(
            RetrofitInterface.invoke()
        )
    }

    //Login fun
    fun login(email: String, password: String) {
        CoroutineHelper.ioToMain({ repository.login(email, password) },
            { loginLiveData.postValue(it) })
    }

    //Sign UP fun
    fun signUP(username: String, email: String, password: String) {
        CoroutineHelper.ioToMain({ repository.signUp(username, email, password) },
            { signUPLiveData.postValue(it) })
    }
}



