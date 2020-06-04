package com.peter_kameel.fos_au.data.repository

import com.peter_kameel.fos_au.helper.SafeRetrofitRequest
import com.peter_kameel.fos_au.interfaces.RetrofitInterface
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity

class RetrofitRepository(private val api: RetrofitInterface) : SafeRetrofitRequest() {

    //Login fun (LoginViewModel)
    suspend fun login(email: String, password: String) =
        apiRequest {
            api.login(
                email,
                password
            )
        }

    suspend fun signUp(username: String, email: String, password: String) =
        apiRequest {
            api.signUp(
                username,
                email,
                password
            )
        }

    //fun for MainActivityViewModel
    suspend fun getRestoredData(id: String) =
        apiRequest {
            api.restoreAsync(
                id
            )
        } // get the saved Semesters from server

    //fun for Service
    suspend fun syncSemester(list: List<SemesterEntity>) =
        apiRequest {
            api.syncSemester(
                list
            )
        } //Send Semesters updates to the server

    suspend fun syncCourse(list: List<CoarseEntity>) =
        apiRequest {
            api.syncCourse(
                list
            )
        } //Send Semesters updates to the server

    suspend fun updateName(email: String, username: String) =
        apiRequest { api.updateName(email, username) } //update user name on server

    suspend fun updatePassword(email: String, password: String, newpassword: String) =
        apiRequest {
            api.updatePassword(
                email,
                password,
                newpassword
            )
        } //update user password on server

}