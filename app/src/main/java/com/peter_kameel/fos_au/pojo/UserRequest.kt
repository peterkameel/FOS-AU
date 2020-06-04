package com.peter_kameel.fos_au.pojo


import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("massage")
    val massage: String,
    @SerializedName("user")
    val user: User
) {
    data class User(
        @SerializedName("email")
        val email: String,
        @SerializedName("_id")
        val id: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("username")
        val username: String
    )
}