package com.peter_kameel.fos_au.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.data.repository.RoomRepository
import com.peter_kameel.fos_au.helper.adapters.SemesterAdapter
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class GPAFragViewModel(application: Application) : AndroidViewModel(application) {
    val semesterLiveData: MutableLiveData<List<SemesterEntity>> by lazy { MutableLiveData<List<SemesterEntity>>() }
    val gpaLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val repository by lazy { RoomRepository(application) }

    //fun used in GPA Fragment
    //fun to insert semester
    suspend fun interSemester(semester: SemesterEntity) {
        CoroutineHelper.ioToMain(
            { repository.interSemester(semester) }, {}).join() // insert semester to room
    }

    //fun get semester list
    suspend fun getSemester() {
        CoroutineHelper.ioToMain(
            { repository.getSemesters() }, // insert semester to room
            { semesterLiveData.postValue(it) })
    }

    //fun to calc GPA
    fun calcGPA(list: List<SemesterEntity>) {
        var totalPoints = 0.0
        var totalHours = 0
        for (item in list) {
            CoroutineScope(IO).launch {
                CoroutineHelper.ioToMain(
                    { repository.getCoarseAsync(item.id) },
                    {
                        totalPoints += SemesterAdapter.getGpa(it) * SemesterAdapter.getHours(it)
                        totalHours += SemesterAdapter.getHours(it)
                    }).join()
                gpaLiveData.postValue("${totalPoints / totalHours}")
            }//end launch
        }//end for loop
    }


    //fun used in Semester Adapter
    suspend fun updateSemester(semester: SemesterEntity) =
        repository.updateSemester(semester) // update semester in data

    suspend fun updateNote(note: String, id: Int) = repository.updateNote(note, id) //update note
    suspend fun deleteSemester(semester: SemesterEntity) =
        repository.deleteSemester(semester) // delete semester from data

    suspend fun deleteSemesterCourses(id: Int) = repository.deleteSemesterCourses(id)
    suspend fun interCourse(course: CoarseEntity) =
        repository.interCourse(course) // inter the new Course

}
