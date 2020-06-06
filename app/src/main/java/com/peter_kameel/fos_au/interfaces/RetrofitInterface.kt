package com.peter_kameel.fos_au.interfaces

import com.peter_kameel.fos_au.data.RetrofitClint
import com.peter_kameel.fos_au.pojo.*
import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterface {

    companion object {
        operator fun invoke(): RetrofitInterface {
            return RetrofitClint.retrofitRequest(RetrofitInterface::class.java)
        }
    }

    //fun for login activity (login & sign up)
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): UserRequest


    @FormUrlEncoded
    @POST("/user/signup")
    suspend fun signUp(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): UserRequest


    //fun to sync data
    @POST("/api/syncs")
    suspend fun syncSemester(@Body semester: List<SemesterEntity>): DataRequest

    @POST("/api/syncc")
    suspend fun syncCourse(@Body course: List<CoarseEntity>): DataRequest

    //fun to restore data from server
    //fun to sync data
    @FormUrlEncoded
    @POST("/api/restore")
    suspend fun restoreAsync(@Field("userid") id: String): DataRequest

    //fun for update user data
    @FormUrlEncoded
    @POST("/user/username")
    suspend fun updateName(
        @Field("email") email: String,
        @Field("username") username: String
    ): UserRequest


    @FormUrlEncoded
    @POST("/user/userpassword")
    suspend fun updatePassword(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("newpassword") newpassword: String
    ): UserRequest
}