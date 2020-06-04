package com.peter_kameel.fos_au.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Semester")
class SemesterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userid: String,
    val name: String,
    val note: String,
    val sync: Boolean
)

@Entity(tableName = "Coarse")
class CoarseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userid: String,
    val semesterid: String,
    val name: String,
    val deg: String,
    val hour: String,
    val sync: Boolean
)