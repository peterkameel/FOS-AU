package com.peter_kameel.fos_au.pojo


import com.google.gson.annotations.SerializedName

data class DataRequest(
    @SerializedName("course")
    val course: List<Course>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("massage")
    val massage: String,
    @SerializedName("semester")
    val semester: List<Semester>
) {
    data class Course(
        @SerializedName("deg")
        val deg: String,
        @SerializedName("hour")
        val hour: String,
        @SerializedName("_id")
        val _id: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("semesterid")
        val semesterid: String,
        @SerializedName("userid")
        val userid: String
    )

    data class Semester(
        @SerializedName("_id")
        val _id: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("note")
        val note: String,
        @SerializedName("userid")
        val userid: String
    )
}