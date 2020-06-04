package com.peter_kameel.fos_au.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


object CoroutineHelper {

    fun <T : Any> ioToMain(work: suspend (() -> T?), callback: ((T?) -> Unit)) =
        CoroutineScope(Main).launch {
            val data = CoroutineScope(IO).async rt@{ return@rt work() }.await()
            callback(data)
        }//end launch


    //this helper used in Sync Service
    fun <T : Any, S : Any> io3Jobs(
        job1: suspend (() -> T?),
        job2: suspend ((T?) -> S?),
        job3: (suspend (T?, S?) -> Unit)
    ) =
        CoroutineScope(Main).launch {
            val t = CoroutineScope(IO).async rt@{ return@rt job1() }.await()
            val s = CoroutineScope(IO).async rt@{ return@rt job2(t) }.await()
            CoroutineScope(IO).async rt@{ return@rt job3(t, s) }.await()
        }//end launch

}