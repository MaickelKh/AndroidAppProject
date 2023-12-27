package be.heh.projetmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.room.Room
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
        supportActionBar?.hide() // Hide the action bar

        // Initialize the SessionManager
        sessionManager = SessionManager(applicationContext)

        val registerButton = findViewById<Button>(R.id.button_registerNow)
        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        // Initialize the database
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

            // Query the database for a user with the entered email and password
            launch(Dispatchers.IO) {
                try {
                    val user = db.userDao().getUserByEmail(email)
                    withContext(Dispatchers.Main) {
                        if (user != null && user.pwd == password) {
                            // If a user is found and the password is correct, save the user id and role in the session
                            sessionManager.userLogin(user.id.toString(), user.function, user.first_name)

                            // Navigate to the main activity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Show an error message if the email or password is incorrect
                            passwordInput.error = "Incorrect email or password"
                        }
                    }
                } catch (e: Exception) {
                    // Print the exception to the console
                    e.printStackTrace()
                }
            }
        }
    }
}