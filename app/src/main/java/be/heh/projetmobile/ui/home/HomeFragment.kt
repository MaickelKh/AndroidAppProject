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
import be.heh.projetmobile.databinding.FragmentHomeBinding
import be.heh.projetmobile.db.MyDB
import be.heh.projetmobile.db.material.MaterialDao
import be.heh.projetmobile.db.material.MaterialRecord
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.DialogInterface
import androidx.activity.OnBackPressedCallback
import be.heh.projetmobile.LoginActivity
import be.heh.projetmobile.SessionManager
import be.heh.projetmobile.db.user.UserDao
import kotlinx.android.synthetic.main.fragment_home.HomeUserName
import kotlinx.android.synthetic.main.fragment_home.countUser
import kotlinx.android.synthetic.main.fragment_home.textHelloName

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager // Add this line
    private lateinit var materialDao: MaterialDao
    private lateinit var userDao: UserDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext()) // Initialize the sessionManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Room.databaseBuilder(
            requireContext(),
            MyDB::class.java, "MyDataBase"
        ).fallbackToDestructiveMigration().build()
        materialDao = db.materialDao()
        userDao = db.userDao()

        HomeUserName.text = sessionManager.getUserName()
        textHelloName.text = "Bonjour " + sessionManager.getUserName() + " !"

        lifecycleScope.launch(Dispatchers.IO) {
            val usersCount = userDao.getUsers().size
            withContext(Dispatchers.Main) {
                countUser.text = usersCount.toString()
            }
        }

        binding.buttonHomeLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showLogoutConfirmationDialog()
            }
        })

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

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Déconnexion")
        builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")

        builder.setPositiveButton("Oui") { dialog, _ ->
            sessionManager.userLogout()
            navigateToLoginActivity()
            dialog.dismiss()
        }

        builder.setNegativeButton("Non") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}