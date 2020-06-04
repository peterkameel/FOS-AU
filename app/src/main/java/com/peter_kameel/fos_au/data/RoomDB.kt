package com.peter_kameel.fos_au.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.peter_kameel.fos_au.interfaces.RoomDAO
import com.peter_kameel.fos_au.pojo.CoarseEntity
import com.peter_kameel.fos_au.pojo.SemesterEntity

@Database(entities = [SemesterEntity::class, CoarseEntity::class], version = 1)
abstract class RoomDB : RoomDatabase() {

    //Dao
    abstract fun roomdao(): RoomDAO

    companion object {
        private var roomDBinstance: RoomDB? = null

        //fun to check if db not created ??
        fun getroomdb(context: Context): RoomDB? {
            if (roomDBinstance == null) {
                synchronized(RoomDB::class.java) {
                    if (roomDBinstance == null) {
                        roomDBinstance = Room.databaseBuilder(
                            context.applicationContext,
                            RoomDB::class.java,
                            "roomdb"
                        ).build()
                    }//End if
                }//End synchronized
            }//End if
            return roomDBinstance
        }//End fun

        fun room(context: Context): RoomDAO {
            return RoomDB.getroomdb(context)!!.roomdao()
        }
    }


}