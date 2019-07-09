package com.beyondthecode.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnlogin_login.setOnClickListener {
           performLogin()
        }



        txtbacktoregister_login.setOnClickListener {
            finish()
        }
    }

    private fun performLogin(){
        val email = txtemail_login.text.toString()
        val clave = txtpassword_login.text.toString()

        if(email.isEmpty() || clave.isEmpty()){
            Toast.makeText(this, "Ingrese sus credenciales", Toast.LENGTH_LONG).show()
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,clave)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this,"Bienvenido ${it.result?.user?.email}",Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
                }
        }



    }
}
