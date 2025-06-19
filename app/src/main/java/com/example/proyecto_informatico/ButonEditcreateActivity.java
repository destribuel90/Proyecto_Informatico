package com.example.proyecto_informatico;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.proyecto_informatico.model.MaterialUpdateRequest;
import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;
import com.example.proyecto_informatico.util.PdfThumbnailTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ButonEditcreateActivity extends AppCompatActivity {
    public static final String EXTRA_PUBLICATION_ID = "extra_publication_id";
    private static final int PICK_PDF_REQUEST = 2;
    private static final String TAG = "EditPublication";

    private ImageButton btnClose, btnUploadFile, btnRemoveFile;
    private TextView charCounterTitle, charCounterDescription;
    private EditText editTitle, editSemester, editUnit, editDescription;
    private FrameLayout previewContainer;
    private ImageView imgPreview;
    private ProgressBar progressBar;

    private String itemId;
    private Uri selectedPdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate iniciado");

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_buton_editcreate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        RetrofitClient.init(this);

        // Bind views
        btnClose = findViewById(R.id.btn_close);
        editTitle = findViewById(R.id.edit_title);
        charCounterTitle = findViewById(R.id.char_counter_title);
        editSemester = findViewById(R.id.edit_semester);
        editUnit = findViewById(R.id.edit_unit);
        editDescription = findViewById(R.id.edit_description);
        charCounterDescription = findViewById(R.id.char_counter_description);
        btnUploadFile = findViewById(R.id.btn_upload_file);
        btnRemoveFile = findViewById(R.id.btn_remove_file);
        imgPreview = findViewById(R.id.img_preview);
        previewContainer = findViewById(R.id.preview_container);
        findViewById(R.id.btn_save).setOnClickListener(v -> updatePublication());

        progressBar.setVisibility(View.GONE);

        // Listeners
        btnClose.setOnClickListener(v -> finish());
        btnUploadFile.setOnClickListener(v -> openFilePicker());
        btnRemoveFile.setOnClickListener(v -> removeSelectedFile());

        editTitle.addTextChangedListener(new LengthWatcher(charCounterTitle, 100));
        editDescription.addTextChangedListener(new LengthWatcher(charCounterDescription, 500));

        // Get ID
        itemId = getIntent().getStringExtra(EXTRA_PUBLICATION_ID);
        if (TextUtils.isEmpty(itemId)) {
            Toast.makeText(this, "ID de publicación no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPublicationDetails(itemId);
    }

    private void loadPublicationDetails(String id) {
        progressBar.setVisibility(View.VISIBLE);
        int publicationId = Integer.parseInt(id);
        ApiService service = RetrofitClient.getApiService();
        service.getMaterialById(publicationId).enqueue(new Callback<MaterialsResponse<Material>>() {
            @Override
            public void onResponse(Call<MaterialsResponse<Material>> call, Response<MaterialsResponse<Material>> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ButonEditcreateActivity.this,
                            "Error cargando datos (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Material m = response.body().getPayload();
                editTitle.setText(m.getTitle());
                editSemester.setText(m.getSemester());
                editUnit.setText(m.getUnit());
                // Actualiza contadores iniciales
                charCounterTitle.setText(m.getTitle().length() + "/100");

                // Cargar preview PDF
                new PdfThumbnailTask(ButonEditcreateActivity.this, imgPreview)
                        .execute("http://31.220.22.166:8080/storage/" + m.getFileUrl());
                previewContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<MaterialsResponse<Material>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ButonEditcreateActivity.this,
                        "Fallo de red: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    private void removeSelectedFile() {
        selectedPdfUri = null;
        previewContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
            if (selectedPdfUri != null) {
                Toast.makeText(this, "PDF seleccionado", Toast.LENGTH_SHORT).show();
                new PdfThumbnailTask(this, imgPreview)
                        .execute(selectedPdfUri.toString());
                previewContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updatePublication() {
        String title = editTitle.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();
        String sem = editSemester.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Título y descripción son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialUpdateRequest req = new MaterialUpdateRequest();
        req.setTitle(title);
        req.setDescription(desc);
        req.setSemester(sem);
        req.setUnit(unit);

        if (selectedPdfUri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(selectedPdfUri);
                byte[] data = readBytes(is);
                String base64 = encodeToBase64(data);
                req.setFileBase64(base64);
                String ext = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(getContentResolver().getType(selectedPdfUri));
                req.setFileExtension(ext);
            } catch (Exception e) {
                Log.e(TAG, "Error leyendo PDF", e);
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService service = RetrofitClient.getApiService();
        service.updateMaterial(Integer.parseInt(itemId), req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(ButonEditcreateActivity.this,
                                    "Publicación actualizada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ButonEditcreateActivity.this,
                                    "Error al actualizar ("+response.code()+")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ButonEditcreateActivity.this,
                                "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Helpers para conversión de bytes y Base64
    private byte[] readBytes(InputStream input) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private String encodeToBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private static class LengthWatcher implements TextWatcher {
        private final TextView counter;
        private final int max;

        LengthWatcher(TextView counter, int max) {
            this.counter = counter;
            this.max = max;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            counter.setText(s.length() + "/" + max);
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
