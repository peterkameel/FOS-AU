package com.peter_kameel.fos_au.interfaces

import android.view.View
import com.peter_kameel.fos_au.pojo.SemesterEntity

interface AdaptersListener {

    // fun for Course Adapter
    fun onSemesterClickButton(view: View, semester: SemesterEntity)

}