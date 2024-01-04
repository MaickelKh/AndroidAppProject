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
import androidx.activity.OnBackPressedCallback
import be.heh.projetmobile.LoginActivity
import be.heh.projetmobile.SessionManager
import be.heh.projetmobile.db.user.UserDao
import kotlinx.android.synthetic.main.fragment_home.HomeUserName
import kotlinx.android.synthetic.main.fragment_home.button_homeAdd
import kotlinx.android.synthetic.main.fragment_home.button_homeGive
import kotlinx.android.synthetic.main.fragment_home.button_homeGiveBack
import kotlinx.android.synthetic.main.fragment_home.countLoaned
import kotlinx.android.synthetic.main.fragment_home.countUser
import kotlinx.android.synthetic.main.fragment_home.registerUser
import kotlinx.android.synthetic.main.fragment_home.textHelloName
import kotlinx.android.synthetic.main.fragment_home.textUserFunction

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var materialDao: MaterialDao
    private lateinit var userDao: UserDao
    private var currentAction: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Room.databaseBuilder(
            requireContext(),
            MyDB::class.java, "MyDataBase"
        ).build()
        materialDao = db.materialDao()
        userDao = db.userDao()

        HomeUserName.text = sessionManager.getUserName()
        textUserFunction.text = sessionManager.getUserRole()
        textHelloName.text = "Bonjour " + sessionManager.getUserName() + " !"

        val userProfile = sessionManager.getUserRole()
        if (userProfile == "Basic") {
            button_homeAdd.visibility = View.GONE
            registerUser.visibility = View.GONE
        } else if (userProfile == "RW") {
            registerUser.visibility = View.GONE
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val usersCount = userDao.getUsers().size
            val materialLoaned = materialDao.getUnavailableMaterial().size
            withContext(Dispatchers.Main) {
                countUser.text = usersCount.toString()
                countLoaned.text = materialLoaned.toString()
            }
        }

        binding.buttonHomeLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showLogoutConfirmationDialog()
                }
            })

        button_homeGive.setOnClickListener {
            currentAction = "give"
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scanner le QR Code")
            integrator.setOrientationLocked(false)
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }

        button_homeGiveBack.setOnClickListener {
            currentAction = "giveBack"
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scanner le QR Code")
            integrator.setOrientationLocked(false)
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }

        binding.buttonHomeAdd.setOnClickListener {
            currentAction = "add"
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
                if (currentAction == "add") {
                    val parts = result.contents.split(";").map { it.trim() }
                    // if (parts.size == 5 && parts.all { it.isNotEmpty() }) {
                    if (parts.size >= 5) {
                        val newArticle = MaterialRecord(
                            id = 0,
                            name = parts[0],
                            type = parts[1],
                            brand = parts[2],
                            ref = parts[3],
                            maker = parts[4],
                            available = 1
                        )
                        lifecycleScope.launch(Dispatchers.IO) {
                            materialDao.insertMaterial(newArticle)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Article ajouté: " + result.contents,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "QR Code invalide ou imcomplet", Toast.LENGTH_LONG).show()
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val parts = result.contents.split(";").map { it.trim() }
                        val scannedRef = parts[3]
                        val scannedName = parts[0]
                        val material = materialDao.getMaterialByRef(scannedRef)
                            ?: materialDao.getMaterialByName(scannedName)
                        if (material != null) {
                            if (currentAction == "give") {
                                if (material.available == 0) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Erreur : l'article est déjà emprunté",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    material.available = 0
                                    materialDao.updateMaterial(material)
                                    val materialLoaned = materialDao.getUnavailableMaterial().size
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Mise à jour de l'article: " + material.name,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        countLoaned.text = materialLoaned.toString()
                                    }
                                }
                            } else if (currentAction == "giveBack") {
                                if (material.available == 1) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Erreur : l'article n'était pas emprunté",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    material.available = 1
                                    materialDao.updateMaterial(material)
                                    val materialLoaned = materialDao.getUnavailableMaterial().size
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Mise à jour de l'article: " + material.name,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        countLoaned.text = materialLoaned.toString()
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Matériel inexistant", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
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