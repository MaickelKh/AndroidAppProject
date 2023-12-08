package be.heh.projetmobile

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.room.Room
import be.heh.projetmobile.db.MyDB
import be.heh.projetmobile.db.User
import be.heh.projetmobile.db.UserRecord
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide() // Hide the action bar
    }
    fun xmlClickEvent(v: View) {
        when (v.id) {
            register_button.id -> InsertUserDB()
        }
    }
    private fun InsertUserDB() {
        val u = User(
            0, login_firstNameInput.text.toString(),
            login_lastNameInput.text.toString(),
            login_emailInput.text.toString(),
            login_passwordInput.text.toString(),
            "Super Admin"
        )
        AsyncTask.execute({
            val db = Room.databaseBuilder(
                applicationContext,
                MyDB::class.java, "MyDataBase"
            ).build()
            val dao = db.userDao()
            val u1 = UserRecord(0, u.first_name,u.last_name,u.function, u.pwd, u.email,)
            dao.insertUser(u1)
        })
        Toast.makeText(this,u.toString(),Toast.LENGTH_LONG).show()
    }
}