package com.example.proyecto_informatico;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_informatico.model.Material;
import com.example.proyecto_informatico.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ImageButton btnProfile, btnHome, btnSearch, btnAdd;
    private RecyclerView recyclerMaterial;
    private MaterialAdapter materialAdapter;
    private List<Material> materialList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnProfile = findViewById(R.id.nav_profile);
        btnHome = findViewById(R.id.nav_home);
        btnSearch = findViewById(R.id.nav_search);
        btnAdd = findViewById(R.id.nav_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerMaterial = findViewById(R.id.recyclerViewCards);
        recyclerMaterial.setLayoutManager(new LinearLayoutManager(this));
        materialAdapter = new MaterialAdapter(materialList);
        recyclerMaterial.setAdapter(materialAdapter);

        fetchMaterialData();
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            startActivity(intent);
        });
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Search.class);
            startActivity(intent);
        });
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ButtonCreate.class);
            startActivity(intent);
        });
    }
    private void fetchMaterialData() {
        Call<List<Material>> call = RetrofitClient.getApiService().getMaterials();
        call.enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    materialList.clear();
                    materialList.addAll(response.body());
                    materialAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Error al obtener datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Material>> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Fallo en la petición: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        }
    }