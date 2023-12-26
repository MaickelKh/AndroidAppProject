package be.heh.projetmobile.adapter;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.heh.projetmobile.R
import be.heh.projetmobile.db.material.MaterialDao
import be.heh.projetmobile.db.material.MaterialRecord

class MaterialAdapter : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    private var materials: List<MaterialRecord> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.material_item, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        holder.materialName.text = material.name
    }

    override fun getItemCount(): Int {
        return materials.size
    }

    fun setMaterials(materials: List<MaterialRecord>) {
        this.materials = materials
        notifyDataSetChanged()
    }

    class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val materialName: TextView = itemView.findViewById(R.id.materialNameText)
        // Définissez ici les autres vues de votre ViewHolder
    }
}
