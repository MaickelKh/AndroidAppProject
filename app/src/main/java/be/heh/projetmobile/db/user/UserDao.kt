package be.heh.projetmobile.db.user

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM UserTable")
    fun getUsers(): List<UserRecord>
    @Query("SELECT * FROM UserTable WHERE email = :email")
    fun getUserByEmail(email: String): UserRecord
    @Insert
    fun insertUser(vararg listCategories: UserRecord)
    @Query("UPDATE UserTable SET function = :newFunction WHERE email = :email")
    fun updateUserFunction(email: String, newFunction: String)
    @Delete
    fun deleteUser(task: UserRecord)
}
