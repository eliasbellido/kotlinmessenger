package com.beyondthecode.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnselectphoto_register.setOnClickListener {
            Toast.makeText(this,"seleccionar foto!",Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }


        btnregister_register.setOnClickListener {
            performRegister()
        }

        txtalreadyregistered_register.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Log.d(TAG, " click en registrar")
        }
    }

    var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null ){
            Toast.makeText(this,"Datos recibidos!",Toast.LENGTH_LONG).show()

            selectedPhotoUri= data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            btnselectphoto_register.alpha = 0f
            /*
            val bitmapDrawable = BitmapDrawable(bitmap)
            btnselectphoto_register.setBackgroundDrawable(bitmapDrawable)
            */
        }
    }

    private fun performRegister(){
        val username = txtusername_register.text.toString()
        val email = txtemail_register.text.toString()
        val password = txtpassword_register.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Complete los campos solicitados!",Toast.LENGTH_LONG).show()
        }else{
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "registrado correctamento con uid: ${it.result?.user?.uid}",Toast.LENGTH_SHORT).show()
                    Log.d("sd","registrado correctamento con uid: ${it.result?.user?.uid}")

                    uploadPhotoToFireStorage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun uploadPhotoToFireStorage(){
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Toast.makeText(this, "Se subió correctamente: ${it.metadata?.path}", Toast.LENGTH_LONG).show()
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG,"File location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                Toast.makeText(this, "Failed to upload image to storage: ${it.message}", Toast.LENGTH_LONG).show()
            }


    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, txtusername_register.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved user to Firebaase!")
            }
    }
}

class User(val uid:String, val username:String, val profileImageUrl:String)