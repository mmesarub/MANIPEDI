package com.mmr.reservamanipedi

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.mmr.reservamanipedi.ui.theme.ReservaManiPediTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun cierre(view: View) {
        sesionoff()
    }

    private fun sesionoff() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun cita(view: View) {
        val intent = Intent(this, Escoge_cita::class.java)
        startActivity(intent)
    }
    fun recuerda(view: View) {
        val intent = Intent(this, Recordatorio::class.java)
        startActivity(intent)
    }
    fun eliminacita(view: View) {
        val intent = Intent(this, Borra_cita::class.java)
        startActivity(intent)
    }
}