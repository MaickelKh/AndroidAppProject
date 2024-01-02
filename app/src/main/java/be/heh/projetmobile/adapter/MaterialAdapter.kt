package be.heh.projetmobile.adapter;
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import be.heh.projetmobile.R
import be.heh.projetmobile.db.material.MaterialDao
import be.heh.projetmobile.db.material.MaterialRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.MalformedURLException
import java.net.URL

class MaterialAdapter(private var materials: MutableList<MaterialRecord>, private val context: Context, private val materialDao: MaterialDao) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialName: TextView = itemView.findViewById(R.id.materialNameText)
        val availableIcon: ImageButton = itemView.findViewById(R.id.availableIcon)
        val trashButton: ImageButton = itemView.findViewById(R.id.trashButton)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.material_item, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        holder.materialName.text = material.brand + " " + material.name
        if (material.available == 0) {
            holder.availableIcon.setImageResource(R.drawable.baseline_cancel_24)
        } else if (material.available == 1) {
            holder.availableIcon.setImageResource(R.drawable.baseline_check_box_24)
        }

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Modifier les informations sur le matériel")

            val layout = LayoutInflater.from(context).inflate(R.layout.material_edit, null)
            builder.setView(layout)

            val brandEditText = layout.findViewById<EditText>(R.id.brandEditText)
            val nameEditText = layout.findViewById<EditText>(R.id.nameEditText)
            val typeEditText = layout.findViewById<EditText>(R.id.typeEditText)
            val refEditText = layout.findViewById<EditText>(R.id.refEditText)
            val makerEditText = layout.findViewById<EditText>(R.id.makerEditText)
            val availableCheckBox = layout.findViewById<CheckBox>(R.id.availableCheckBox)

            brandEditText.setText(material.brand)
            nameEditText.setText(material.name)
            typeEditText.setText(material.type)
            refEditText.setText(material.ref)
            makerEditText.setText(material.maker)
            availableCheckBox.isChecked = material.available == 1

            val makerLink = layout.findViewById<TextView>(R.id.makerLink)
            makerLink.text = "Cliquez ici pour visitez la page du constructeur"
            makerLink.setOnClickListener {
                try {
                    val url = URL(makerEditText.text.toString())
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                    context.startActivity(intent)
                } catch (e: MalformedURLException) {
                    Toast.makeText(context, "URL non valide", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setPositiveButton("Enregistrer") { dialog, which ->
                material.brand = brandEditText.text.toString()
                material.name = nameEditText.text.toString()
                material.type = typeEditText.text.toString()
                material.ref = refEditText.text.toString()
                material.maker = makerEditText.text.toString()
                material.available = if (availableCheckBox.isChecked) 1 else 0

                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        materialDao.updateMaterial(material)
                    }
                    withContext(Dispatchers.Main) {
                        notifyItemChanged(position)
                    }
                }
            }
            builder.setNegativeButton("Annuler", null)
            builder.show()
        }

        holder.trashButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
            builder.setMessage("Êtes-vous sûr de vouloir supprimer ce matériel ?")

            builder.setPositiveButton("Oui") { dialog, which ->
                // Supprimez l'utilisateur de la base de données
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        materialDao.deleteMaterial(material)
                    }
                    withContext(Dispatchers.Main) {
                        materials.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }
            builder.setNegativeButton("Non", null)
            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return materials.size
    }

    fun setMaterials(materials: List<MaterialRecord>) {
        this.materials = materials.toMutableList()
        notifyDataSetChanged()
    }
}
