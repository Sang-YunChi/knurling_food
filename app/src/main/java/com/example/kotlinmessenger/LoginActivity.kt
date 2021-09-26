package com.example.kotlinmessenger

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R.layout.activity_login
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_login)

        login_button_login.setOnClickListener {
            val email = email_edittext_register.text.toString()
            val password = password_edittext_register.text.toString()

            Log.d("Login", "로그인 시도 with email/pw: $email/***")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener()

        }
        back_to_register_textview.setOnClickListener {
            finish()
        }
    }
    }

