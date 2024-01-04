package be.heh.projetmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import at.favre.lib.crypto.bcrypt.BCrypt
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
        supportActionBar?.hide()

        val loginButton = findViewById<Button>(R.id.button_loginNow)
        loginButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

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

        if (firstName.isEmpty()) {
            firstNameInput.error = "Le prénom est requis"
            return
        }
        if (lastName.isEmpty()) {
            lastNameInput.error = "Le nom est requis"
            return
        }
        if (email.isEmpty()) {
            emailInput.error = "L'email est requis"
            return
        }
        if (password.isEmpty()) {
            passwordInput.error = "Le mot de passe est requis"
            return
        }
        if (passwordConfirm.isEmpty()) {
            passwordConfirmInput.error = "La confirmation du mot de passe est requise"
            return
        }

        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.*])(?=\\S+$).{8,}$"
        val passwordMatcher = Regex(passwordPattern)
        if (!passwordMatcher.matches(password)) {
            passwordInput.error = "Le mot de passe doit contenir au moins 8 caractères, dont au moins une lettre minuscule, une lettre majuscule, un chiffre et un caractère spécial"
            return
        }

        if (password != passwordConfirm) {
            passwordConfirmInput.error = "Les mots de passe ne correspondent pas"
            return
        }

        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                MyDB::class.java, "MyDataBase"
            ).build()
            val dao = db.userDao()

            val existingUser = dao.getUserByEmail(email)
            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    emailInput.error = "Un utilisateur avec cet email existe déjà"
                }
                return@launch
            }

            val anyUser = dao.getUsers()
            val role = if (anyUser.isEmpty()) "Admin" else "Basic"

            val u = UserRecord(0, firstName, lastName, role, hashedPassword, email)
            dao.insertUser(u)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Inscription confirmée", Toast.LENGTH_LONG).show()

                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}