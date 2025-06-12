package com.example.proyecto_informatico;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity implements MaterialAdapter.OnItemClickListener{
    private String searchTerm = null;
    private String semester = null;
    private Integer unit = null;
    private Integer page = null;
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
        recyclerMaterial = findViewById(R.id.recyclerViewCards);
        recyclerMaterial.setLayoutManager(new LinearLayoutManager(this));
        materialAdapter = new MaterialAdapter(materialList, this);
        recyclerMaterial.setAdapter(materialAdapter);
        searchInput = findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 1) Chequea si vino del editor (IME_ACTION_DONE, SEARCH, SEND…)
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_SEND) {
                    searchTerm = v.getText().toString();
                    fetchMaterialData();
                    return true;  // consumes el evento
                }
                // 2) O chequea la tecla física Enter
                if (event != null
                        && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    fetchMaterialData();
                    return true;
                }
                return false; // deja que otros listeners lo manejen
            }
        });
    }



    private void fetchMaterialData() {
        ApiService api = RetrofitClient.getApiService();

        // 1) Usamos el genérico List<Material> para que Retrofit/Gson sepa qué deserializar
        Call<MaterialsResponse<List<MaterialsResponse.Material>>> call =
                api.getAllMaterials(searchTerm, semester, unit, page);

        call.enqueue(new Callback<MaterialsResponse<List<MaterialsResponse.Material>>>() {
            @Override
            public void onResponse(
                    Call<MaterialsResponse<List<MaterialsResponse.Material>>> call,
                    Response<MaterialsResponse<List<MaterialsResponse.Material>>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    // 2) Obtenemos el payload tipado como List<Material>
                    List<MaterialsResponse.Material> lista = response.body().getPayload();

                    // 3) Actualizamos nuestra materialList y notificamos al adapter
                    materialList.clear();
                    materialList.addAll(lista);
                    materialAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(
                            Search.this,
                            "Error al obtener datos: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<MaterialsResponse<List<MaterialsResponse.Material>>> call,
                    Throwable t
            ) {
                Toast.makeText(
                        Search.this,
                        "Fallo en la petición: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
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