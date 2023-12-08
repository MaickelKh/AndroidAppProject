package be.heh.projetmobile.db

import androidx.room.*
import be.heh.projetmobile.db.UserRecord
@Database(entities = [(UserRecord::class)], version = 1)
abstract class MyDB : RoomDatabase()
{
    abstract fun userDao(): UserDao
}