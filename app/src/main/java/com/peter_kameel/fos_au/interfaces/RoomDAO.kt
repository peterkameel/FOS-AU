package com.peter_kameel.fos_au.interfaces

import androidx.room.*
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity

@Dao
interface RoomDAO {


    //fun used in GPA Fragment
    // insert semester fun
    @Insert
    suspend fun insertSemester(semester: SemesterEntity)

    //fun get list of semester
    @Query("Select * From Semester")
    suspend fun getSemestersAsync(): List<SemesterEntity>


    //fun used in Semester Adapter
    // insert Coarse fun
    @Insert
    suspend fun insertCoarse(coarse: CoarseEntity)

    //Update Semester
    @Update
    suspend fun updateSemester(semester: SemesterEntity)

    //Update Note Semester
    @Query("UPDATE Semester SET note = :note , sync = 0 WHERE id LIKE :id")
    suspend fun updateSemesterNote(note: String, id: Int)

    //Delete Semester
    @Delete
    suspend fun deleteSemester(semester: SemesterEntity)

    //Delete semester courses
    @Query("DELETE FROM Coarse WHERE semesterID LIKE :id ")
    suspend fun deleteSemesterCourses(id: Int)

    //fun get coarse for specific semester
    @Query("Select * From Coarse WHERE semesterID LIKE :id")
    suspend fun getCoarseAsync(id: Int): List<CoarseEntity>


    //fun used in Course Adapter
    // fun update Course
    @Update
    suspend fun editCourse(course: CoarseEntity)

    //fun delete course
    @Delete
    suspend fun deleteCourse(course: CoarseEntity)


    //fun for sync data
    @Query("Select * From Semester WHERE sync LIKE 0")
    suspend fun getUnSyncSemesters(): List<SemesterEntity>

    @Query("Select * From Coarse WHERE sync LIKE 0")
    suspend fun getUnSyncCourses(): List<CoarseEntity>

    //fun to Update Sync Variable
    @Query("UPDATE Semester SET sync = 1 WHERE id LIKE :id")
    suspend fun updateSyncSemesters(id: Int)

    @Query("UPDATE Coarse SET sync = 1 WHERE id LIKE :id")
    suspend fun updateSyncCourse(id: Int)

}