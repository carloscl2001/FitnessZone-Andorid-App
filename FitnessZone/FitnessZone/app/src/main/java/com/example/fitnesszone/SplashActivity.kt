package com.example.fitnesszone

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Buscar el ImageView en el layout por su ID.
        val imageView = findViewById<ImageView>(R.id.imageView)
        // Intentar obtener el fondo del ImageView como un AnimationDrawable.
        // Si el fondo es realmente un AnimationDrawable, iniciar la animación.
        (imageView.background as? AnimationDrawable)?.start()

        // Coroutine para manejar el retardo antes de iniciar MainActivity
        GlobalScope.launch {
            delay(5000) // Retraso de 5 segundos
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish() // Cierra esta actividad para que el usuario no pueda volver a ella
        }
    }
}