package com.example.tubarriolimpioapp.ui.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.tubarriolimpioapp.R
import com.example.tubarriolimpioapp.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Tema del splash sin ActionBar
        setTheme(R.style.Theme_TuBarrio_Splash)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottie = findViewById<LottieAnimationView>(R.id.lottieSplash)

        // Por si acaso, garantizamos que se reproduzca desde el inicio
        lottie.progress = 0f
        lottie.playAnimation()

        // Cuando termine la animación, pasamos al Login
        lottie.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                irALogin()
            }
        })
    }

    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}
