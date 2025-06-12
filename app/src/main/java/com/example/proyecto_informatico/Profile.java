package com.example.proyecto_informatico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Profile extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton btnMenu, btnProfile, btnHome, btnSearch, btnAdd;
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Ajuste edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout_profile);

        // Botón de menú (menu1)
        btnMenu = findViewById(R.id.menu1);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Botón editar perfil
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Botones de navegación inferior
        btnProfile = findViewById(R.id.nav_profile);
        btnHome = findViewById(R.id.nav_home);
        btnSearch = findViewById(R.id.nav_search);
        btnAdd = findViewById(R.id.nav_add);

        btnProfile.setOnClickListener(v -> {
            // Ya estamos en Perfil
        });
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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
