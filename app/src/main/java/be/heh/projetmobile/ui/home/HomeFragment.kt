package be.heh.projetmobile.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import be.heh.projetmobile.ManualAddActivity
import be.heh.projetmobile.R
import be.heh.projetmobile.databinding.FragmentHomeBinding
import be.heh.projetmobile.db.MyDB
import be.heh.projetmobile.db.material.Material
import be.heh.projetmobile.db.material.MaterialDao
import be.heh.projetmobile.db.material.MaterialRecord
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Since we didn't alter the table, there's nothing else to do here.
    }
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var materialDao: MaterialDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Room.databaseBuilder(
            requireContext(),
            MyDB::class.java, "MyDataBase"
        ).fallbackToDestructiveMigration().build()
        materialDao = db.materialDao()

        binding.buttonHomeAdd.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Ajouter un article")
            builder.setMessage("Souhaitez-vous ajouter un article manuellement ou par QR Code ?")

            builder.setPositiveButton("Manuellement") { dialog, _ ->
                val intent = Intent(requireContext(), ManualAddActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }

            builder.setNegativeButton("Par QR Code") { dialog, _ ->
                val integrator = IntentIntegrator.forSupportFragment(this)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                integrator.setPrompt("Scanner le QR Code")
                integrator.setOrientationLocked(false)
                integrator.setCameraId(0)
                integrator.setBeepEnabled(true)
                integrator.setBarcodeImageEnabled(true)
                integrator.initiateScan()
                dialog.dismiss()
            }
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
    if (result != null) {
        if (result.contents == null) {
            Toast.makeText(context, "Scan annulé", Toast.LENGTH_LONG).show()
        } else {
            // Parse the QR code content
            val parts = result.contents.split(";").map { it.trim() }
            val newArticle = MaterialRecord(
                id = 0,
                name = parts[1],
                type = parts[2],
                brand = parts[3],
                ref = parts[4].toInt(),
                maker = parts[5],
                available = parts[6].toInt()
            )
            // Add the new article to the database on a background thread
            lifecycleScope.launch(Dispatchers.IO) {
                materialDao.insertMaterial(newArticle)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Article ajouté: " + result.contents, Toast.LENGTH_LONG).show()
                }
            }
        }
    } else {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
}