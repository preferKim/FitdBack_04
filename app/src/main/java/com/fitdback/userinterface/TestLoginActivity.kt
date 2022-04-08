package com.fitdback.userinterface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.fitdback.posedetection.CameraActivity
import com.fitdback.posedetection.R

class TestLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnjoin = findViewById<Button>(R.id.btnJoin)
        val btnAnonymousLogin = findViewById<Button>(R.id.btnAnonymousLogin)

        val toCameraIntent = Intent(this, CameraActivity::class.java)

        btnLogin.setOnClickListener {

            val email = findViewById<EditText>(R.id.areaID)
            val password = findViewById<EditText>(R.id.areaPassword)

            Log.d("tla", email.text.toString())
            Log.d("tla", password.text.toString())

            startActivity(toCameraIntent)

        }

        btnAnonymousLogin.setOnClickListener{

            Toast.makeText(this, "Anonymous Login", Toast.LENGTH_LONG).show()

        }

    }
}