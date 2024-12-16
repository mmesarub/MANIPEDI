package com.mmr.reservamanipedi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity2 : AppCompatActivity() {

    // Declarar las vistas
    private lateinit var nombreField: EditText
    private lateinit var telefonoField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    // Firebase Auth y Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicializar las vistas
        nombreField = findViewById(R.id.nombre)
        telefonoField = findViewById(R.id.editTextPhone)
        emailField = findViewById(R.id.TextEmail)
        passwordField = findViewById(R.id.TextAddress)
        loginButton = findViewById(R.id.button1)
        registerButton = findViewById(R.id.button2)

        // Establecer el modo por defecto (ocultar todos los campos excepto los necesarios)
        setMode("none")

        // Configurar el botón de inicio de sesión
        loginButton.setOnClickListener {
            setMode("login")
            loginUser() // Llamar al método de inicio de sesión
        }

        // Configurar el botón de registro
        registerButton.setOnClickListener {
            setMode("register")
            registerUser() // Llamar al método de registro
        }
    }

    // Función para configurar el modo de la vista
    private fun setMode(mode: String) {
        when (mode) {
            "login" -> {
                nombreField.visibility = View.GONE
                telefonoField.visibility = View.GONE
                emailField.visibility = View.VISIBLE
                passwordField.visibility = View.VISIBLE
            }
            "register" -> {
                nombreField.visibility = View.VISIBLE
                telefonoField.visibility = View.VISIBLE
                emailField.visibility = View.VISIBLE
                passwordField.visibility = View.VISIBLE
            }
            "none" -> {
                // Ocultar todos los campos inicialmente
                nombreField.visibility = View.GONE
                telefonoField.visibility = View.GONE
                emailField.visibility = View.GONE
                passwordField.visibility = View.GONE
            }
        }
    }

    // Función para iniciar sesión
    private fun loginUser() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    redirectToMain()
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para registrar un usuario nuevo
    private fun registerUser() {
        val nombre = nombreField.text.toString()
        val telefono = telefonoField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardar datos adicionales en Firestore
                    val userId = auth.currentUser?.uid
                    val user = hashMapOf(
                        "nombre" to nombre,
                        "telefono" to telefono,
                        "email" to email
                    )

                    if (userId != null) {
                        firestore.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                redirectToMain()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para redirigir a la actividad principal
    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Evitar que el usuario regrese al login
    }
}