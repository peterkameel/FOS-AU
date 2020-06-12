package com.peter_kameel.fos_au.ui.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.data.repository.RetrofitRepository
import com.peter_kameel.fos_au.interfaces.RetrofitInterface
import com.peter_kameel.fos_au.data.repository.RoomRepository
import com.peter_kameel.fos_au.data.util.ShareTAG
import com.peter_kameel.fos_au.data.util.SharedPrefs
import com.peter_kameel.fos_au.pojo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {


    val restoreLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val userLiveData: MutableLiveData<UserRequest> by lazy { MutableLiveData<UserRequest>() }
    private val userID by lazy {
        SharedPrefs.readSharedString(application, ShareTAG.userID, "")
    }

    private val room by lazy { RoomRepository(application) }
    private val scope by lazy { CoroutineScope(Dispatchers.IO) }
    private val repository by lazy { RetrofitRepository(RetrofitInterface.invoke()) }

    //fun for restore data from server
    suspend fun restoreData() {
        CoroutineHelper.io3Jobs(
            { repository.getRestoredData(userID) },
            { insertRestoredSemesters(it?.semester ?: listOf()) },
            { it, _ -> insertRestoredCourses(it?.course ?: listOf()) })
    }

    private fun insertRestoredSemesters(list: List<DataRequest.Semester>) {
        if (!list.isNullOrEmpty()) {
            for (semester in list) {
                val newSemester = SemesterEntity(
                    0,
                    semester.userid,
                    semester.name,
                    semester.note,
                    true
                )
                scope.launch { room.interSemester(newSemester) }
            }
        }
    }

    private fun insertRestoredCourses(list: List<DataRequest.Course>) {
        if (!list.isNullOrEmpty()) {
            for (course in list) {
                val newCourse = CoarseEntity(
                    0,
                    course.userid,
                    course.semesterid,
                    course.name,
                    course.deg,
                    course.hour,
                    true
                )
                CoroutineHelper.ioToMain(
                    { room.interCourse(newCourse) }, // inter the new course
                    { restoreLiveData.postValue("Restore Completed") }) //post the liveData massage
            }//End for
        }//End if
    }

    //fun change user data
    suspend fun updateName(email: String, username: String) {
        CoroutineHelper.ioToMain(
            { repository.updateName(email, username) },
            { userLiveData.postValue(it) })
    }

    suspend fun updatePassword(email: String, password: String, newpassword: String) {
        CoroutineHelper.ioToMain(
            { repository.updatePassword(email, password, newpassword) },
            { userLiveData.postValue(it) })
    }
}