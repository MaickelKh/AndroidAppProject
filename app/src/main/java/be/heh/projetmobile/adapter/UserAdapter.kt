package be.heh.projetmobile.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.heh.projetmobile.R
import be.heh.projetmobile.db.user.UserDao
import be.heh.projetmobile.db.user.UserRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserAdapter(private val users: MutableList<UserRecord>, private val context: Context, private val userDao: UserDao) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userItem: LinearLayout = view.findViewById(R.id.user_item)
        val userName: TextView = view.findViewById(R.id.user_name)
        val userFunction: TextView = view.findViewById(R.id.user_function)
        val cancelIcon: ImageView = view.findViewById(R.id.cancel_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.userName.text = user.first_name + " " + user.last_name
        holder.userFunction.text = user.function

        holder.userItem.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choisissez une nouvelle fonction")

            val roles = arrayOf("Admin", "Basic", "RW", "Désactivé")
            val spinner = Spinner(context)
            spinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, roles)
            builder.setView(spinner)

            builder.setPositiveButton("OK") { dialog, which ->
                val newRole = spinner.selectedItem.toString()

                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        userDao.updateUserFunction(user.email, newRole)
                    }
                }

                user.function = newRole
                holder.userFunction.text = newRole
            }

            builder.setNegativeButton("Annuler", null)

            builder.show()
        }

        holder.cancelIcon.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
            builder.setMessage("Êtes-vous sûr de vouloir supprimer cet utilisateur ?")

            builder.setPositiveButton("Oui") { dialog, which ->
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        userDao.deleteUser(user)
                    }

                    withContext(Dispatchers.Main) {
                        users.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }

            builder.setNegativeButton("Non", null)
            builder.show()
        }
    }

    override fun getItemCount() = users.size
}