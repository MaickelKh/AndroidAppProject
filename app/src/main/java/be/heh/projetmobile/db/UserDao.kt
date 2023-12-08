package be.heh.projetmobile.db

import androidx.room.*
import be.heh.projetmobile.db.UserRecord
@Dao
interface UserDao {
    @Query("SELECT * FROM UserTable")
    fun get(): List<UserRecord>

    @Query("SELECT * FROM UserTable WHERE email = :email")
    fun get(email: String): UserRecord

    @Insert
    fun insertUser(vararg listCategories: UserRecord)

    @Update
    fun updatePersonne(task: UserRecord)

    @Delete
    fun deletePersonne(task: UserRecord)
}
