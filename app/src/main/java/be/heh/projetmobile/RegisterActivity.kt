package be.heh.projetmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import be.heh.projetmobile.db.MyDB
import be.heh.projetmobile.db.user.UserRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide() // Hide the action bar

        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            register()
        }
    }
    private fun register() {
        val firstNameInput = findViewById<EditText>(R.id.register_firstNameInput)
        val lastNameInput = findViewById<EditText>(R.id.register_lastNameInput)
        val emailInput = findViewById<EditText>(R.id.register_emailInput)
        val passwordInput = findViewById<EditText>(R.id.register_passwordInput)
        val passwordConfirmInput = findViewById<EditText>(R.id.register_passwordConfirmInput)

        val firstName = firstNameInput.text.toString()
        val lastName = lastNameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val passwordConfirm = passwordConfirmInput.text.toString()

        // Check if any of the input fields are empty
        if (firstName.isEmpty()) {
            firstNameInput.error = "First name is required"
            return
        }
        if (lastName.isEmpty()) {
            lastNameInput.error = "Last name is required"
            return
        }
        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            return
        }
        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            return
        }
        if (passwordConfirm.isEmpty()) {
            passwordConfirmInput.error = "Password confirmation is required"
            return
        }

        // Check if the passwords match
        if (password != passwordConfirm) {
            passwordConfirmInput.error = "Passwords do not match"
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                MyDB::class.java, "MyDataBase"
            ).build()
            val dao = db.userDao()

            // Check if a user with the entered email already exists
            val existingUser = dao.getUserByEmail(email)
            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    emailInput.error = "A user with this email already exists"
                }
                return@launch
            }

            val anyUser = dao.getUsers()
            val role = if (anyUser.isEmpty()) "Admin" else "Basic"

            val u = UserRecord(0, firstName, lastName, role, password, email)
            dao.insertUser(u)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Inscription confirmée", Toast.LENGTH_LONG).show()

                // Redirect to the login page
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}