package be.heh.projetmobile.db

import androidx.room.*
@Entity(tableName = "UserTable")
data class UserRecord(
    @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id: Int=0,
    @ColumnInfo(name="first_name") var first_name : String,
    @ColumnInfo(name="last_name") var last_name : String,
    @ColumnInfo(name="function") var function : String,
    @ColumnInfo(name="pwd") var pwd: String,
    @ColumnInfo(name="email") var email: String
)