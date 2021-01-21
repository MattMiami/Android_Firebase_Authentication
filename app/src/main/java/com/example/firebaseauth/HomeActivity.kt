package com.example.firebaseauth

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*

class HomeActivity : AppCompatActivity() {

    val CAPTURA = 1
    val PICK = 2

    //para conectar con la base de datos
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        //Recuperar los valores del usuario
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val password = bundle?.getString("password")
        logout()

        //guardar datos de las prefs
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("password", password)
        prefs.apply()

        //Infor del usuario
        userPhoto()



        //Para guardar o edit la informacion del usuario de la base de datos
        btInfo.setOnClickListener {

            //Aqui creamos un documento asociado al usuario
            db.collection("users").document(email.toString()).set(
                hashMapOf(
                    "name" to etName.text.toString(),
                    "city" to etCity.text.toString(),
                    "phone" to etPhone.text.toString()

                )
            )
        }

        //Para mostar la info del usuario guardada en la base de datos
        btshowInfo.setOnClickListener {
            db.collection("users").document(email.toString()).get().addOnSuccessListener {
                etName.setText(it.get("name") as String?)
                etCity.setText(it.get("city") as String?)
                etPhone.setText(it.get("phone") as String?)

            }
        }

    }

//------------------------------------LogOut-------------------------------------------
    private fun logout() {

        btLogout.setOnClickListener {

            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()

            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()

        }
    }

    //------------------------------------para la foto de perfil-------------------------------------------
    private fun userPhoto() {
        btCamara.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(intent, CAPTURA)
            } catch (e: ActivityNotFoundException) {
                e.message
            }
        }

        btGaleria.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURA && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            ivUser.setImageBitmap(imageBitmap)
        }

        if (requestCode == PICK && resultCode == RESULT_OK) {
            ivUser.setImageURI(data?.data)
        }
    }


}