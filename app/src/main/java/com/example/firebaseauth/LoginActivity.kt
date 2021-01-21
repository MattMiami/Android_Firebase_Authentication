package com.example.firebaseauth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        googleLogin()
        facebookLogin()
        githubLogin()
        register()
        login()

    }

    //-----------------------------Register Email Event------------------------------------------
    private fun register() {
        btReg.setOnClickListener {
            if (etMail.text.isNotEmpty() && etPass.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(etMail.text.toString(), etPass.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //Mantiene la sesion abierta
                            session()
                            //Navega hasta HomeActivity
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Asegurese de que la contraseña contiene números",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "No dejes campos vacios", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //-----------------------------Login Email Event------------------------------------------
    private fun login() {
        btLogin.setOnClickListener {
            if (etMail.text.isNotEmpty() && etPass.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(etMail.text.toString(), etPass.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //Mantiene la sesion abierta
                            session()
                            //Navega hasta HomeActivity
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Credenciales incorrectas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "No dejes campos vacios", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //-----------------------------Save User Data------------------------------------------
    //Guarda la sesion aunque mates la aplicacion
    private fun session(): Boolean {
        val prefs: SharedPreferences =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val password = prefs.getString("password", null)
        if (email != null && password != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()

        }
        return true
    }


    //-----------------------------Autentificacion con GOOGLE------------------------------------------
    fun googleLogin() {

        val providers = arrayListOf(

            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        btGmail.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    //este metodo nos permite elegir la cuenta con la que loguearnos
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN
            )
        }


    }

    //-----------------------------Autentificacion con FACEBOOK------------------------------------------
    fun facebookLogin() {

        val providers = arrayListOf(

            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        btFace.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    //este metodo nos permite elegir la cuenta con la que loguearnos
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN
            )


        }
    }

    //-----------------------------Autentificacion con GITHUB------------------------------------------
    fun githubLogin() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.GitHubBuilder().build()
        )

        btGit.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    //este metodo nos permite elegir la cuenta con la que loguearnos
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN
            )

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Si el codigo enviado es el mismo que el recibido
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            //Podremos loguearnos
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(applicationContext, "Logueado correctamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))

            } else {

                Toast.makeText(
                    applicationContext,
                    "Error de autentificación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

}