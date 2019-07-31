package com.beyondthecode.kotlinmessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.beyondthecode.kotlinmessenger.R
import com.beyondthecode.kotlinmessenger.messages.LatestMessagesActivity
import com.beyondthecode.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


private const val TAG = "RegisterActivity"

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
                    Toast.makeText(this, "Error al crear usuario:  ${it.message}", Toast.LENGTH_SHORT).show()
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
                Log.d(TAG, "Finalmente grabamos el usuario en la bd de Firebase!")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d(TAG, "Error al setear el valor en la bd: ${it.message}")
            }
    }
}
