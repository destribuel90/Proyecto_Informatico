package com.example.proyecto_informatico;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto_informatico.model.AuthResponse;
import com.example.proyecto_informatico.model.LogoutResponse;
import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.model.User;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton btnMenu, btnProfile, btnHome, btnSearch, btnAdd;
    private Button btnEditProfile;
    TextView userName;

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
        userName = findViewById(R.id.user_name);

        // Inicializar DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout_profile);

        // Botón de menú (menu1)
        btnMenu = findViewById(R.id.menu1);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        NavigationView navView = findViewById(R.id.drawer_menu_profile);

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout_profile) {
                logout();
            }
            else if (id == R.id.nav_mode_defaul_color) {
                colorDefault();
            }
            else if (id == R.id.nav_mode_light_color) {
                colorLight();
            }
            else if (id == R.id.nav_mode_night_color) {
                colorNight();
            }
            // else if (id == …) { … }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
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
        SharedPreferences prefs =
                Profile.this.getSharedPreferences("mi_prefs", Context.MODE_PRIVATE);
        String userName1 = prefs.getString("user_name", null);
        userName.setText(userName1);
//        setUserData();
    }
//    public void setUserData() {
//        SharedPreferences prefs = Profile.this.getSharedPreferences("mi_prefs", Context.MODE_PRIVATE);
//        String userId = prefs.getString("user_id", null);
//        ApiService service = RetrofitClient.getApiService();
//        service.getUserById(Integer.parseInt(userId)).enqueue(new Callback<User>(){
//            @Override
//            public void onResponse(Call <User> call, Response<MaterialsResponse<MaterialsResponse.Material>> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    Log.e(TAG, "Error en respuesta: Código HTTP " + response.code());
//                    Toast.makeText(Profile.this, "Error al cargar datos (" + response.code() + ")", Toast.LENGTH_SHORT).show();
//                    finish();
//                    return;
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<MaterialsResponse<MaterialsResponse.Material>> call, Throwable t) {
//                Log.e(TAG, "Fallo en la llamada API: " + t.getMessage(), t);
//                Toast.makeText(Profile.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//        });
//    }
    public void logout() {
        ApiService service = RetrofitClient.getApiService();
        Call<LogoutResponse> call = service.logoutUser();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call,
                                   Response<LogoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LogoutResponse body = response.body();

                    // Comprueba el campo "status"
                    if ("success".equals(body.getStatus())) {
                        // 1) Limpia SharedPreferences
                        SharedPreferences prefs =
                                Profile.this.getSharedPreferences("mi_prefs", Context.MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        // 2) Redirige al MainActivity limpiando el back stack
                        Intent intent = new Intent(Profile.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        // API devolvió status distinto
                        Toast.makeText(Profile.this,
                                "No se pudo cerrar sesión: " + body.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Profile.this,
                            "Error en servidor: código " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Toast.makeText(Profile.this,
                        "Fallo de red: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure logoutUser", t);
            }
        });
    }
    private void colorDefault() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
    private void colorLight() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
    private void colorNight() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
