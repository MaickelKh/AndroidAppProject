package be.heh.projetmobile.db

import androidx.room.*
import be.heh.projetmobile.db.UserRecord
@Dao
interface UserDao {
    @Query("SELECT * FROM UserTable")
    fun get(): List<UserRecord>

}
