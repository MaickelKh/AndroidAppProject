package be.heh.projetmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import at.favre.lib.crypto.bcrypt.BCrypt
import be.heh.projetmobile.db.MyDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var db: MyDB
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        sessionManager = SessionManager(applicationContext)

        val registerButton = findViewById<Button>(R.id.button_registerNow)
        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        db = Room.databaseBuilder(
            applicationContext,
            MyDB::class.java, "MyDataBase"
        ).build()

        val emailInput = findViewById<EditText>(R.id.login_emailInput)
        val passwordInput = findViewById<EditText>(R.id.login_passwordInput)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val user = db.userDao().getUserByEmail(email)
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            if (user.function.equals("Désactivé")) {
                                passwordInput.error = "Ce compte est désactivé"
                            } else if (BCrypt.verifyer().verify(password.toCharArray(), user.pwd).verified) {
                                sessionManager.userLogin(user.id.toString(), user.function, user.first_name)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                passwordInput.error = "Email ou mot de passe incorrect"
                            }
                        } else {
                            passwordInput.error = "Email ou mot de passe incorrect"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}