package be.heh.projetmobile.ui.material

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import be.heh.projetmobile.R
import be.heh.projetmobile.adapter.MaterialAdapter
import be.heh.projetmobile.adapter.UserAdapter
import be.heh.projetmobile.databinding.FragmentMaterialBinding
import be.heh.projetmobile.db.MyDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaterialFragment : Fragment() {

    private var _binding: FragmentMaterialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenez une instance de UserDao
        val db = Room.databaseBuilder(
            requireContext(),
            MyDB::class.java, "MyDataBase"
        ).build()
        val materialDao = db.materialDao()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val buttonHomeAvailable = view.findViewById<Button>(R.id.button_homeAvailable)
        buttonHomeAvailable.setOnClickListener {
            lifecycleScope.launch {
                val materials = withContext(Dispatchers.IO) {
                    materialDao.getMaterial()
                }
                withContext(Dispatchers.Main) {
                    val materialNames = materials.joinToString("\n") { it.name }
                    AlertDialog.Builder(requireContext())
                        .setTitle("Material Names")
                        .setMessage(materialNames)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }

        lifecycleScope.launch {
            val materials = withContext(Dispatchers.IO) {
                materialDao.getMaterial()
            }
            withContext(Dispatchers.Main) {
                recyclerView.adapter = MaterialAdapter().apply {
                    setMaterials(materials)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}