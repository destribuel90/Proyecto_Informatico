package com.example.proyecto_informatico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_informatico.network.RetrofitClient;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetrofitClient.init(getApplicationContext());


        // 1) Abrimos SharedPreferences con el mismo nombre que usaste al guardar
        SharedPreferences prefs = getSharedPreferences("mi_prefs", MODE_PRIVATE);

        // 2) Leemos el token (por defecto toma cadena vacía si no existe)
        String token = prefs.getString("TOKEN_KEY", "");

        if (!token.isEmpty()) {
            // 3a) Si hay token, vamos directo a MainActivity (pantalla principal)
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            // 3b) Si no hay token (""), vamos a la pantalla de login
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
        }

        // 4) Cerramos SplashActivity para que no quede en el back stack
        finish();
    }
}
