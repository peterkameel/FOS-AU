package com.peter_kameel.fos_au.data.repository

import android.content.Context
import com.peter_kameel.fos_au.data.RoomDB
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity


class RoomRepository(private val context: Context) {

    private val room by lazy { RoomDB.room(this.context) }

    //fun for Sync Service
    suspend fun getUnSyncSemesters() =
        room.getUnSyncSemesters() //get the UnSync Semester

    suspend fun getUnSyncCourses() =
        room.getUnSyncCourses() //get the UnSync Semester

    suspend fun updateSyncSemesters(id: Int) =
        room.updateSyncSemesters(id) //update sync value in UnSync Semester

    suspend fun updateSyncCourses(id: Int) =
        room.updateSyncCourse(id)  //update sync value in UnSync Courses


    //fun for MainActivityViewModel
    suspend fun interCourse(course: CoarseEntity) =
        room.insertCoarse(course) // inter the new Course th other fun interSemester used before in GPAFragment fun


    //fun for GPAFragment ViewModel
    suspend fun interSemester(semester: SemesterEntity) =
        room.insertSemester(semester) //inter a new Semester

    suspend fun getSemesters() = room.getSemestersAsync() //get the list of semesters


    //fun for CourseAdapterViewModel
    suspend fun editCourse(course: CoarseEntity) =
        room.editCourse(course) // fun update Course

    suspend fun deleteCourse(course: CoarseEntity) =
        room.deleteCourse(course) //fun delete course


    //fun for Semester Adapter
    suspend fun updateSemester(semester: SemesterEntity) =
        room.updateSemester(semester) // update semester in data

    suspend fun updateNote(note: String, id: Int) = room.updateSemesterNote(note, id) //update note
    suspend fun deleteSemester(semester: SemesterEntity) =
        room.deleteSemester(semester) // delete semester from data

    suspend fun deleteSemesterCourses(id: Int) =
        room.deleteSemesterCourses(id)

    suspend fun getCoarseAsync(id: Int) =
        room.getCoarseAsync(id)

}