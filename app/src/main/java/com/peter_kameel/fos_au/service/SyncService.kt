package com.peter_kameel.fos_au.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.peter_kameel.fos_au.helper.CoroutineHelper
import com.peter_kameel.fos_au.data.repository.RetrofitRepository
import com.peter_kameel.fos_au.interfaces.RetrofitInterface
import com.peter_kameel.fos_au.data.repository.RoomRepository
import com.peter_kameel.fos_au.data.util.ShareTAG
import com.peter_kameel.fos_au.data.util.SharedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class SyncService : JobService() {

    private val repository by lazy {
        RetrofitRepository(RetrofitInterface.invoke())
    }
    private val room by lazy { RoomRepository(application) }

    override fun onStartJob(params: JobParameters?): Boolean {
        if (SharedPrefs.readSharedBoolean(this, ShareTAG().syncServiceBoolean, false)) {
            CoroutineScope(Main).launch {
                CoroutineHelper.io3Jobs(
                    { room.getUnSyncSemesters() }, //job1 get the un sync Semesters from room DB
                    { it?.let { it1 -> repository.syncSemester(it1) } }, //job2 Sync the list of Semesters on server
                    { T, S ->
                        if (S != null && T != null) {
                            if (!S.error) {
                                for (item in T) {
                                    room.updateSyncSemesters(item.id)
                                }
                            }
                        }
                    }).join()
                //job3 if Sync Success will update sync boolean value in room and join the job until finish

                CoroutineHelper.io3Jobs(
                    { room.getUnSyncCourses() }, //job1 get the un sync Courses from room DB
                    { it?.let { it1 -> repository.syncCourse(it1) } }, //job2 Sync the list of Courses on server
                    // Check if Sync is Failed service will finished else job3 will work
                    { T, S ->
                        if (S != null && T != null) {
                            if (!S.error) {
                                for (item in T) {
                                    room.updateSyncCourses(item.id)
                                }
                            } else {
                                launch(Main) { jobFinished(params, false) }
                            }
                        }
                    }).join()
                //job3 if Sync Success will update sync boolean value in room and join the job until finish

                //end the service
                jobFinished(params, false)
            }
        } else {
            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates
    }

}

