package be.heh.projetmobile.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.projetmobile.R
import be.heh.projetmobile.SessionManager
import be.heh.projetmobile.adapter.UserAdapter
import be.heh.projetmobile.databinding.FragmentAccountBinding
import be.heh.projetmobile.db.MyDB
import kotlinx.android.synthetic.main.fragment_account.textHelloName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sessionManager = SessionManager(requireContext()) // Initialize the sessionManage

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the user role from the session
        val userRole = sessionManager.getUserRole()

        // If the user role is "Basic", show an error message and return
        if (userRole != "Admin") {
            textHelloName.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            textHelloName.text = "Accès refusé. Vous n'avez pas les droits nécessaires pour accéder à cette page."
        } else {
            // Obtenez une instance de UserDao
            val db = Room.databaseBuilder(
                requireContext(),
                MyDB::class.java, "MyDataBase"
            ).build()
            val userDao = db.userDao()

            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(context)

            // Récupérez la liste des utilisateurs
            GlobalScope.launch {
                val users = withContext(Dispatchers.IO) {
                    userDao.getUsers().toMutableList()
                }

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = UserAdapter(users, requireContext(), userDao)
                }
            }
        }
    }
}