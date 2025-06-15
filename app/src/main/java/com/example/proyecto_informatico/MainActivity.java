package com.example.proyecto_informatico;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_informatico.adapter.MaterialAdapter;
import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;
import com.example.proyecto_informatico.model.MaterialsResponse.Pagination;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MaterialAdapter.OnItemClickListener {
    ImageButton btnProfile, btnHome, btnSearch, btnAdd;
    private RecyclerView recyclerMaterial;
    private MaterialAdapter materialAdapter;
    private List<Material> materialList = new ArrayList<>();

    // Paginación
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private int totalPages = 1;

    private LinearLayoutManager layoutManager;

    private String searchTerm = null;
    private String semester = null;
    private Integer unit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Aplica insets de statusBar y navegación al root
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Padding: left, top=statusBar, right, bottom=navBar
            v.setPadding(
                    v.getPaddingLeft(),
                    sysBars.top,
                    v.getPaddingRight(),
                    sysBars.bottom
            );
            return insets;
        });
        btnProfile = findViewById(R.id.nav_profile);
        btnHome = findViewById(R.id.nav_home);
        btnSearch = findViewById(R.id.nav_search);
        btnAdd = findViewById(R.id.nav_add);
        // Inicializar vistas y RecyclerView...
        recyclerMaterial = findViewById(R.id.recyclerViewCards);
        layoutManager = new LinearLayoutManager(this);
        recyclerMaterial.setLayoutManager(layoutManager);
        materialAdapter = new MaterialAdapter(materialList, this);
        recyclerMaterial.setAdapter(materialAdapter);
        btnProfile.setOnClickListener(v -> { startActivity(new Intent(MainActivity.this, Profile.class)); });
        btnHome.setOnClickListener(v -> { startActivity(new Intent(MainActivity.this, MainActivity.class)); });
        btnSearch.setOnClickListener(v -> { startActivity(new Intent(MainActivity.this, Search.class)); });
        btnAdd.setOnClickListener(v -> { startActivity(new Intent(MainActivity.this, ButtonCreate.class)); });

        // Scroll infinito
        recyclerMaterial.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0 &&
                        currentPage < totalPages) {
                    loadMoreItems();
                }
            }
        });

        fetchMaterialData();

        // Navegación botones...
    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage++;
        fetchMaterialData();
    }

    private void fetchMaterialData() {
        ApiService api = RetrofitClient.getApiService();
        Call<MaterialsResponse<List<Material>>> call = api.getAllMaterials(searchTerm, semester, unit, currentPage);

        call.enqueue(new Callback<MaterialsResponse<List<Material>>>() {
            @Override
            public void onResponse(Call<MaterialsResponse<List<Material>>> call,
                                   Response<MaterialsResponse<List<Material>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    MaterialsResponse<List<Material>> resp = response.body();

                    // Obtener paginación del modelo
                    Pagination pageInfo = resp.getPagination();
                    if (pageInfo != null) {
                        currentPage = pageInfo.getCurrentPage();
                        totalPages = pageInfo.getTotalPages();
                        isLastPage = (currentPage >= totalPages);
                    }

                    List<Material> lista = resp.getPayload();
                    if (currentPage == 1) {
                        materialList.clear();
                    }
                    materialList.addAll(lista);
                    materialAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Error al obtener datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MaterialsResponse<List<Material>>> call, Throwable t) {
                isLoading = false;
                Toast.makeText(MainActivity.this,
                        "Fallo en la petición: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Material material, int position) {
        String id = String.valueOf(material.getId());
        Uri uri = Uri.parse("https://srv852844.hstgr.cloud/items/" + id);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(ItemPublication.EXTRA_PUBLICATION_ID, id);
        intent.setPackage(getPackageName());
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearAppCache();
    }
    private void clearAppCache() {
        try {
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


}