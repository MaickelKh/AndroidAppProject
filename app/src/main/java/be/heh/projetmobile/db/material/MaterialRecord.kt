package be.heh.projetmobile.db.material

import androidx.room.*

@Entity(tableName = "MaterialTable")
data class MaterialRecord(
    @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id: Int=0,
    @ColumnInfo(name="name") var name : String,
    @ColumnInfo(name="type") var type : String,
    @ColumnInfo(name="brand") var brand : String,
    @ColumnInfo(name="ref") var ref : Int,
    @ColumnInfo(name="maker") var maker : String,
    @ColumnInfo(name="available") var available: Int
)