package be.heh.projetmobile.db.material;

import androidx.room.*;

@Dao
public interface MaterialDao {
    @Query("SELECT * FROM MaterialTable")
    fun getMaterial(): List<MaterialRecord>

    @Query("SELECT * FROM MaterialTable WHERE available = 1")
    fun getAvailableMaterial(): List<MaterialRecord>

    @Query("SELECT * FROM MaterialTable WHERE available = 0")
    fun getUnavailableMaterial(): List<MaterialRecord>

    @Insert
    fun insertMaterial(vararg listCategories: MaterialRecord)
}
