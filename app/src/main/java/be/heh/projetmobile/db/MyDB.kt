package be.heh.projetmobile.db

import androidx.room.*
@Database(entities = [(UserRecord::class)], version = 1)
abstract class MyDB : RoomDatabase()
{
    abstract fun userDao(): UserDao
}