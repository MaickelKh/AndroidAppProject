package be.heh.projetmobile.ui.material

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import be.heh.projetmobile.Login
import be.heh.projetmobile.Register
import be.heh.projetmobile.databinding.FragmentMaterialBinding

class MaterialFragment : Fragment() {

    private var _binding: FragmentMaterialBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
                ViewModelProvider(this).get(MaterialViewModel::class.java)

        _binding = FragmentMaterialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Définir le OnClickListener pour editButton1
        binding.editButton1.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
        }

        binding.trashButton1.setOnClickListener {
            val intent = Intent(activity, Register::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}