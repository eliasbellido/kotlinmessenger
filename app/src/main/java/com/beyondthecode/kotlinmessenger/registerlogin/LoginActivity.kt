package com.beyondthecode.kotlinmessenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beyondthecode.kotlinmessenger.R
import com.beyondthecode.kotlinmessenger.messages.LatestMessagesActivity
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
                                val intent = Intent(this, LatestMessagesActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
                            }
                    }



    }
}
