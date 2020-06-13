package com.peter_kameel.fos_au.data.repository

import com.peter_kameel.fos_au.helper.SafeRetrofitRequest
import com.peter_kameel.fos_au.interfaces.RemoteErrorMassage
import com.peter_kameel.fos_au.interfaces.RetrofitInterface
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity

class RetrofitRepository(private val api: RetrofitInterface) : SafeRetrofitRequest(),
    RemoteErrorMassage {

    //Login fun (LoginViewModel)
    suspend fun login(email: String, password: String) =
        apiRequest(this) { api.login(email, password) }

    suspend fun signUp(username: String, email: String, password: String) =
        apiRequest(this) {
            api.signUp(username, email, password)
        }

    suspend fun forget(email: String) =
        apiRequest(this) { api.forget(email) } //send email if password is forget

    //fun for MainActivityViewModel
    suspend fun getRestoredData(id: String) =
        apiRequest(this) {
            api.restoreAsync(id)
        } // get the saved Semesters from server

    //fun for Service
    suspend fun syncSemester(list: List<SemesterEntity>) =
        apiRequest(this) {
            api.syncSemester(list)
        } //Send Semesters updates to the server

    suspend fun syncCourse(list: List<CoarseEntity>) =
        apiRequest(this) {
            api.syncCourse(list)
        } //Send Semesters updates to the server

    suspend fun updateName(email: String, username: String) =
        apiRequest(this) { api.updateName(email, username) } //update user name on server

    suspend fun updatePassword(email: String, password: String, newpassword: String) =
        apiRequest(this) {
            api.updatePassword(email, password, newpassword)
        } //update user password on server

    override fun onError(msg: String) {
        TODO("Not yet implemented")
    }

}