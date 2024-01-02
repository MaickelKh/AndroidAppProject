package be.heh.projetmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import be.heh.projetmobile.db.MyDB
import be.heh.projetmobile.db.material.Material
import be.heh.projetmobile.db.material.MaterialRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManualAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_add)

        val db = Room.databaseBuilder(
            applicationContext,
            MyDB::class.java, "MyDataBase"
        ).build()
        val materialDao = db.materialDao()

        val nameEditText = findViewById<EditText>(R.id.name)
        val typeEditText = findViewById<EditText>(R.id.type)
        val brandEditText = findViewById<EditText>(R.id.brand)
        val referenceEditText = findViewById<EditText>(R.id.reference)
        val manufacturerUrlEditText = findViewById<EditText>(R.id.manufacturer_url)
        val addButton = findViewById<Button>(R.id.add_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val type = typeEditText.text.toString()
            val brand = brandEditText.text.toString()
            val reference = referenceEditText.text.toString()
            val makerUrl = manufacturerUrlEditText.text.toString()

            if (name.isEmpty() || type.isEmpty() || brand.isEmpty() || reference.isEmpty() || makerUrl.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                val newMaterial = MaterialRecord(name = name, type = type, brand = brand, ref = reference, maker = makerUrl, available = 1)

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        materialDao.insertMaterial(newMaterial)
                    }
                    Toast.makeText(this@ManualAddActivity, "Article ajouté", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ManualAddActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}