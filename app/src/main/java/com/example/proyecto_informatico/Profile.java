package com.example.proyecto_informatico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Importar la clase Button
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {

    ImageButton btnProfile, btnHome, btnSearch, btnAdd;
    private Button btnEditProfile; // Declarar el botón de editar perfil

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Configurar el padding con los insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Asignar y programar el botón de editar perfil
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            // Crea un Intent para abrir la actividad EditProfileActivity
            // Asegúrate de que tu actividad se llame EditProfileActivity
            Intent intent = new Intent(Profile.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Asignar los botones de navegación
        btnProfile = findViewById(R.id.nav_profile);
        btnHome = findViewById(R.id.nav_home);
        btnSearch = findViewById(R.id.nav_search);
        btnAdd = findViewById(R.id.nav_add);

        // Programar los clics de los botones de navegación
        // Mejorar la lógica del botón de perfil para evitar reiniciar la misma actividad
        btnProfile.setOnClickListener(v -> {
            // No hacer nada si ya estamos en la actividad Profile, o mostrar un Toast
            // Toast.makeText(Profile.this, "Ya estás en tu perfil", Toast.LENGTH_SHORT).show();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            // Flags para limpiar el back stack y llevar al usuario a Home
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finaliza la actividad actual para que no se acumule
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, Search.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, ButtonCreate.class);
            startActivity(intent);
        });
    }
}
