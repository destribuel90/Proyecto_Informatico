package com.example.proyecto_informatico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;

import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ImageButton btnProfile, btnHome, btnSearch, btnAdd;
    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "mi_prefs";
    private static final String TOKEN_KEY = "TOKEN_KEY";
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
        ApiService api = RetrofitClient.getApiService();
        Call<MaterialsResponse> call = api.getMaterials();

        call.enqueue(new Callback<MaterialsResponse>() {
            @Override
            public void onResponse(Call<MaterialsResponse> call, Response<MaterialsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 1) Obtenemos el objeto completo
                    MaterialsResponse mr = response.body();

                    // 2) Extraemos la lista real de Material
                    List<Material> lista = mr.getContent();

                    // 3) Actualizamos nuestra materialList y notificamos al adapter
                    materialList.clear();
                    materialList.addAll(lista);
                    materialAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Error al obtener datos: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<MaterialsResponse> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Fallo en la petición: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }


}