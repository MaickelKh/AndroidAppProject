package be.heh.projetmobile.db

import androidx.room.*
import be.heh.projetmobile.db.material.MaterialDao
import be.heh.projetmobile.db.material.MaterialRecord
import be.heh.projetmobile.db.user.UserDao
import be.heh.projetmobile.db.user.UserRecord

@Database(entities = [UserRecord::class, MaterialRecord::class], version = 2)
abstract class MyDB : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun materialDao(): MaterialDao
}