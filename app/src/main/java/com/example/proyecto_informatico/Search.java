package com.example.proyecto_informatico;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_informatico.adapter.MaterialAdapter;
import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity implements MaterialAdapter.OnItemClickListener{
    private String searchTerm = null;
    private String semester = null;
    private Integer unit = null;
    private Integer page = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private int totalPages = 1;
    private LinearLayoutManager layoutManager;
    ChipGroup chipGroup;
    private RecyclerView recyclerMaterial;
    private MaterialAdapter materialAdapter;
    private static final String PREFS_NAME = "mi_prefs";
    private static final String TOKEN_KEY = "TOKEN_KEY";
    private EditText searchInput;
    private List<MaterialsResponse.Material> materialList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1) Inicializas el layoutManager de campo y lo usas
        layoutManager = new LinearLayoutManager(this);
        recyclerMaterial = findViewById(R.id.recyclerViewCards);
        recyclerMaterial.setLayoutManager(layoutManager);
        materialAdapter = new MaterialAdapter(materialList, this);
        recyclerMaterial.setAdapter(materialAdapter);

        // 2) Configuras el listener de scroll
        recyclerMaterial.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount   = layoutManager.getItemCount();
                int firstVisiblePos  = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage
                        && (visibleItemCount + firstVisiblePos) >= totalItemCount
                        && firstVisiblePos >= 0
                        && currentPage < totalPages) {
                    loadMoreItems();
                }
            }
        });

        // EditText de búsqueda
        searchInput = findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_SEND) {
                searchTerm = v.getText().toString().trim();
                currentPage = 1;
                fetchMaterialData();
                handled = true;
            } else if (event != null
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                searchTerm = v.getText().toString().trim();
                currentPage = 1;
                fetchMaterialData();
                handled = true;
            }
            return handled;
        });

        // 3) Evita redeclarar chipGroup: usa el campo
        chipGroup = findViewById(R.id.chip_group_filters);
        List<String> filtros = Arrays.asList("Semestre", "Unidad");
        for (String filtro : filtros) {
            Chip chip = new Chip(this);
            chip.setText(filtro);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            chip.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked) {
                    showNumberInputDialog(filtro, chip);
                } else {
                    if ("Semestre".equals(filtro)) {
                        semester = null;
                    } else {
                        unit = null;
                    }
                    currentPage = 1;
                    fetchMaterialData();
                }
            });

            chipGroup.addView(chip);
        }


    }

    private void loadMoreItems() {
        isLoading = true;
        currentPage++;
        fetchMaterialData();
    }

    private void showNumberInputDialog(String tipoFiltro, Chip chip) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Introduce un número para " + tipoFiltro);

        new AlertDialog.Builder(this)
                .setTitle("Filtro: " + tipoFiltro)
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String texto = input.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        int valor = Integer.parseInt(texto);
                        if ("Semestre".equals(tipoFiltro)) {
                            semester = String.valueOf(valor);
                        } else if ("Unidad".equals(tipoFiltro)) {
                            unit = valor;
                        }
                        currentPage = 1;
                        fetchMaterialData();
                    } else {
                        chip.setChecked(false);
                        Toast.makeText(this, "Debes introducir un número", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    chip.setChecked(false);
                    dialog.cancel();
                })
                .show();
    }


    private void fetchMaterialData() {
        ApiService api = RetrofitClient.getApiService();
        Call<MaterialsResponse<List<MaterialsResponse.Material>>> call = api.getAllMaterials(searchTerm, semester, unit, currentPage);

        call.enqueue(new Callback<MaterialsResponse<List<MaterialsResponse.Material>>>() {
            @Override
            public void onResponse(Call<MaterialsResponse<List<MaterialsResponse.Material>>> call,
                                   Response<MaterialsResponse<List<MaterialsResponse.Material>>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    MaterialsResponse<List<MaterialsResponse.Material>> resp = response.body();

                    // Obtener paginación del modelo
                    MaterialsResponse.Pagination pageInfo = resp.getPagination();
                    if (pageInfo != null) {
                        currentPage = pageInfo.getCurrentPage();
                        totalPages = pageInfo.getTotalPages();
                        isLastPage = (currentPage >= totalPages);
                    }

                    List<MaterialsResponse.Material> lista = resp.getPayload();
                    if (currentPage == 1) {
                        materialList.clear();
                    }
                    materialList.addAll(lista);
                    materialAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Search.this,
                            "Error al obtener datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MaterialsResponse<List<MaterialsResponse.Material>>> call, Throwable t) {
                isLoading = false;
                Toast.makeText(Search.this,
                        "Fallo en la petición: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(MaterialsResponse.Material material, int position){
        String publicationId = String.valueOf(material.getId());
        Uri itemUri = Uri.parse("https://srv852844.hstgr.cloud/items/" + publicationId);
        Intent intent = new Intent(Intent.ACTION_VIEW, itemUri);
        intent.putExtra(ItemPublication.EXTRA_PUBLICATION_ID, publicationId);
        intent.setPackage(getPackageName());
        startActivity(intent);
    }
}