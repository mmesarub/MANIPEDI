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

class LoginActivity : AppCompatActivity() {

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

        // Establecer el modo por defecto (ninguno)
        setMode("none")

        // Configurar el botón de inicio de sesión
        loginButton.setOnClickListener {
            setMode("login")
        }

        // Configurar el botón de registro
        registerButton.setOnClickListener {
            setMode("register")
        }
    }

    /**
     * Configura la visibilidad de los campos según el modo seleccionado
     * @param mode Puede ser "login", "register" o "none"
     */
    private fun setMode(mode: String) {
        when (mode) {
            "login" -> {
                // Mostrar solo los campos para el inicio de sesión
                nombreField.visibility = View.GONE
                telefonoField.visibility = View.GONE
                emailField.visibility = View.VISIBLE
                passwordField.visibility = View.VISIBLE
                // Configurar la acción del botón de login
                loginButton.setOnClickListener { loginUser() }
            }
            "register" -> {
                // Mostrar los campos para el registro
                nombreField.visibility = View.VISIBLE
                telefonoField.visibility = View.VISIBLE
                emailField.visibility = View.VISIBLE
                passwordField.visibility = View.VISIBLE
                // Configurar la acción del botón de registro
                registerButton.setOnClickListener { registerUser() }
            }
            else -> {
                // Ocultar todos los campos
                nombreField.visibility = View.GONE
                telefonoField.visibility = View.GONE
                emailField.visibility = View.GONE
                passwordField.visibility = View.GONE
            }
        }
    }

    // Función para iniciar sesión con correo y contraseña
    private fun loginUser() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Iniciar sesión con Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si el inicio de sesión es exitoso, redirigir al MainActivity
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    redirectToMain()
                } else {
                    // Si el inicio de sesión falla, mostrar el error
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para registrar un nuevo usuario
    private fun registerUser() {
        val nombre = nombreField.text.toString()
        val telefono = telefonoField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Registrar usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardar los datos en Firestore
                    saveUserData(nombre, telefono, email)
                } else {
                    // Si el registro falla, mostrar el error
                    Toast.makeText(this, "Error al registrar usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para guardar los datos del usuario en Firestore
    private fun saveUserData(nombre: String, telefono: String, email: String) {
        // Cambiar HashMap a HashMap<String, Any>
        val user = hashMapOf<String, Any>(
            "nombre" to nombre,
            "telefono" to telefono,
            "email" to email
        )

        // Referencia al documento único
        val usuariosDocRef = firestore.collection("usuarios").document("unico_documento")

        // Obtener el documento
        usuariosDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Si el documento existe, obtener el campo 'usuarios' (lista de usuarios)
                    val usuariosList = document.get("usuarios") as? ArrayList<HashMap<String, Any>> ?: arrayListOf()

                    // Añadir el nuevo usuario a la lista
                    usuariosList.add(user)

                    // Actualizar el documento con la nueva lista de usuarios
                    usuariosDocRef.update("usuarios", usuariosList)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            refreshPage() // Refrescar la página después de registrar el usuario
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Si el documento no existe, crearlo y añadir el primer usuario
                    val usuariosList = arrayListOf(user)
                    usuariosDocRef.set(mapOf("usuarios" to usuariosList))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            refreshPage() // Refrescar la página después de registrar el usuario
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener documento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para redirigir al MainActivity
    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finalizar la actividad de login
    }

    // Función para refrescar la página (recrear la actividad)
    private fun refreshPage() {
        // Llama a recreate() para recrear la actividad y refrescarla
        recreate()
    }
}

