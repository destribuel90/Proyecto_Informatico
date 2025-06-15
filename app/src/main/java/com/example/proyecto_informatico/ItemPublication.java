package com.example.proyecto_informatico;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import android.text.TextUtils;

import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemPublication extends AppCompatActivity {
    public static final String EXTRA_PUBLICATION_ID = "extra_publication_id";
    private static final int CREATE_FILE_REQUEST = 1;
    private static final String TAG = "ItemPublication";

    private TextView publicationTitle;
    private TextView publicationDescription;
    private TextView publicationDate;
    private TextView publicationSemester;
    private TextView publicationUnit;
    private ImageButton downloadButton;
    private ImageButton shareButton;
    private ImageButton saveButton;
    private ImageButton crossButton;
    private ImageView previewImage;
    private String pdfUrl = null;
    private String pdfName = null;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate iniciado");

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_item_publication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RetrofitClient.init(this);

        publicationTitle = findViewById(R.id.publication_title);
        publicationDescription = findViewById(R.id.descripcion_publicacion);
        publicationDate = findViewById(R.id.tv_date);
        publicationSemester = findViewById(R.id.tv_semester);
        publicationUnit = findViewById(R.id.tv_unit);
        downloadButton = findViewById(R.id.btn_download);
        shareButton = findViewById(R.id.btn_share);
        saveButton = findViewById(R.id.btn_save);
        crossButton = findViewById(R.id.btn_cross);
        previewImage = findViewById(R.id.img_publicacion);

        // Deshabilitar inicial
        downloadButton.setEnabled(false);
        shareButton.setEnabled(false);
        saveButton.setEnabled(false);

        itemId = resolveItemId(getIntent());
        if (itemId != null) {
            loadPublicationDetails(itemId);
        } else {
            Toast.makeText(this, "ID de publicación no válido", Toast.LENGTH_SHORT).show();
            finish();
        }

        downloadButton.setOnClickListener(v -> {
            if (pdfUrl == null) {
                Toast.makeText(this, "PDF no disponible, revisa el servidor", Toast.LENGTH_SHORT).show();
            } else {
                crearDocumento(pdfUrl);
            }
        });
        shareButton.setOnClickListener(v -> sharePdfLink());
        saveButton.setOnClickListener(v -> saveToFavorites());
        crossButton.setOnClickListener(v -> finish());
        previewImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebviewActivity.class);
            intent.putExtra(WebviewActivity.EXTRA_URL, "https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
            startActivity(intent);
        } );
    }

    private String resolveItemId(Intent intent) {
        if (intent.hasExtra(EXTRA_PUBLICATION_ID)) {
            return intent.getStringExtra(EXTRA_PUBLICATION_ID);
        }
        Uri data = intent.getData();
        if (data != null) {
            List<String> segments = data.getPathSegments();
            if (segments.size() > 1 && "items".equals(segments.get(0))) {
                return segments.get(1);
            }
        }
        return null;
    }

    private void loadPublicationDetails(String id) {
        Log.d(TAG, "Cargando detalles para ID: " + id);
        int publicationId = Integer.parseInt(id);
        ApiService service = RetrofitClient.getApiService();

        service.getMaterialById(publicationId).enqueue(new Callback<MaterialsResponse<Material>>() {
            @Override
            public void onResponse(Call<MaterialsResponse<Material>> call, Response<MaterialsResponse<Material>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Error en respuesta: Código HTTP " + response.code());
                    Toast.makeText(ItemPublication.this, "Error al cargar datos (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Material publication = response.body().getPayload();
                publicationTitle.setText(publication.getTitle());
                publicationDate.setText(publication.getCreatedAt());
                publicationSemester.setText(publication.getSemester());
                publicationUnit.setText(publication.getUnit());

                // Se debe asegurar que el objeto JSON incluya 'fileUrl'
                pdfUrl = "http://31.220.22.166:8080/storage/" + publication.getFileUrl();
                pdfName = publication.getTitle();
                Log.d(TAG, "PDF URL recibido: " + pdfUrl);

                if (pdfUrl != null) {
                    downloadButton.setEnabled(true);
                    shareButton.setEnabled(true);
                } else {
                    Toast.makeText(ItemPublication.this, "PDF no disponible en servidor", Toast.LENGTH_LONG).show();
                }
                new PdfThumbnailTask(ItemPublication.this, previewImage)
                        .execute(pdfUrl);
                saveButton.setEnabled(true);
                Log.d(TAG, "Estados: download=" + downloadButton.isEnabled()
                        + " share=" + shareButton.isEnabled()
                        + " save=" + saveButton.isEnabled());
            }

            @Override
            public void onFailure(Call<MaterialsResponse<Material>> call, Throwable t) {
                Log.e(TAG, "Fallo en la llamada API: " + t.getMessage(), t);
                Toast.makeText(ItemPublication.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearDocumento(String fileUrl) {
        // 1) Extraer el "nombre de fichero" y la extensión de la URL
        String lastSegment = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        // lastSegment = "Ow9x8PWdBhEhQba7z0JpENiYalfePyFFg8iV00d9.pdf"
        String fileName = lastSegment;

        // 2) Obtener la extensión (texto tras el último '.')
        String extension = "";
        int dot = lastSegment.lastIndexOf('.');
        if (dot >= 0) {
            extension = lastSegment.substring(dot + 1).toLowerCase();
        }

        // 3) Mapear la extensión a un tipo MIME, o fallback a "*/*"
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (mime == null) {
            mime = "*/*";
        }

        // 4) Construir el Intent para crear el documento
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime);

        // 5) Asegurarnos de que fileName lleva la extensión
        if (dot < 0 && !extension.isEmpty()) {
            fileName += "." + extension;
        }
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        startActivityForResult(intent, CREATE_FILE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri destinoUri = data.getData();
            if (destinoUri != null && !TextUtils.isEmpty(pdfUrl)) {
                new DownloadPdfTask(this, getContentResolver(), destinoUri).execute(pdfUrl);
            }
        }
    }

    private void sharePdfLink() {
        if (TextUtils.isEmpty(pdfUrl)) {
            Toast.makeText(this, "URL de PDF no disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, pdfName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://srv852844.hstgr.cloud/items/" + itemId);
        startActivity(Intent.createChooser(shareIntent, "Compartir enlace"));
    }

    private void saveToFavorites() {
        Toast.makeText(this, "Guardado en favoritos", Toast.LENGTH_SHORT).show();
    }
}
