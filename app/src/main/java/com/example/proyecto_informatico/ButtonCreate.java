package com.example.proyecto_informatico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ButtonCreate extends AppCompatActivity {

    // Constants
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESC_LENGTH = 500;
    private static final String PDF_MIME = "application/pdf";

    // UI Components
    private EditText editTitle, editSemester, editUnit, editDescription;
    private FrameLayout previewContainer;
    private ImageView imgPreview;
    private ImageButton btnRemoveFile;

    private Uri selectedFileUri;
    private ApiService service;
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    showFilePreview(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = RetrofitClient.getApiService();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_button_create);

        initializeViews();
        setupTextWatchers();
        setupFileButtons();
        setupCreateButton();


        setupWindowInsets();
    }

    private void initializeViews() {
        editTitle = findViewById(R.id.edit_title);
        editSemester = findViewById(R.id.edit_semester);
        editUnit = findViewById(R.id.edit_unit);
        editDescription = findViewById(R.id.edit_description);
        previewContainer = findViewById(R.id.preview_container);
        imgPreview = findViewById(R.id.img_preview);
        btnRemoveFile = findViewById(R.id.btn_remove_file);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });
    }

    private void setupTextWatchers() {
        TextView charCounterTitle = findViewById(R.id.char_counter_title);
        editTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TITLE_LENGTH)});
        editTitle.addTextChangedListener(createTextWatcher(charCounterTitle, MAX_TITLE_LENGTH));

        TextView charCounterDesc = findViewById(R.id.char_counter_description);
        editDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_DESC_LENGTH)});
        editDescription.addTextChangedListener(createTextWatcher(charCounterDesc, MAX_DESC_LENGTH));
    }

    private TextWatcher createTextWatcher(TextView counter, int maxLength) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                counter.setText(String.format("%d/%d", s.length(), maxLength));
            }
        };
    }

    private void setupFileButtons() {
        Button btnUpload = findViewById(R.id.btn_upload_file);
        btnUpload.setOnClickListener(v -> filePickerLauncher.launch(PDF_MIME));

        btnRemoveFile.setOnClickListener(v -> {
            selectedFileUri = null;
            previewContainer.setVisibility(View.GONE);
        });
    }

    private void showFilePreview(Uri uri) {
        previewContainer.setVisibility(View.VISIBLE);
        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r")) {
            if (pfd == null) return;

            try (PdfRenderer renderer = new PdfRenderer(pfd)) {
                PdfRenderer.Page page = renderer.openPage(0);
                try {
                    Bitmap bmp = Bitmap.createBitmap(
                            page.getWidth(),
                            page.getHeight(),
                            Bitmap.Config.ARGB_8888
                    );
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    imgPreview.setImageBitmap(bmp);
                } finally {
                    page.close();
                }
            }
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, "Error loading PDF preview", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupCreateButton() {
        Button btnCreate = findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(v -> {
            if (!validateForm()) return;
            uploadMaterial();
        });
    }

    private boolean validateForm() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editTitle.getText().toString().trim().isEmpty()) {
            editTitle.setError("Title is required");
            return false;
        }

        if (editSemester.getText().toString().trim().isEmpty()) {
            editSemester.setError("Semester is required");
            return false;
        }

        if (editUnit.getText().toString().trim().isEmpty()) {
            editUnit.setError("Unit is required");
            return false;
        }

        return true;
    }

    private void uploadMaterial() {
        // Get user ID from SharedPreferences
        SharedPreferences prefs = ButtonCreate.this.getSharedPreferences("mi_prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        Log.d("ButtonCreate", "User  ID: " + userId);



        try (InputStream is = getContentResolver().openInputStream(selectedFileUri)) {
            if (is == null) {
                Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
                return;
            }

            File temp = File.createTempFile("upload", ".pdf", getCacheDir());
            try (FileOutputStream fos = new FileOutputStream(temp)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            }

            // Create request parts
            RequestBody titleBody = RequestBody.create(
                    editTitle.getText().toString(),
                    MediaType.parse("text/plain")
            );
            RequestBody semesterBody = RequestBody.create(
                    editSemester.getText().toString(),
                    MediaType.parse("text/plain")
            );
            RequestBody unitBody = RequestBody.create(
                    editUnit.getText().toString(),
                    MediaType.parse("text/plain")
            );

            RequestBody userIdBody = RequestBody.create(
                    userId,
                    MediaType.parse("text/plain")
            );

            RequestBody fileBody = RequestBody.create(temp, MediaType.get(PDF_MIME));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    "file",
                    getFileName(selectedFileUri),
                    fileBody
            );

            Call<MaterialsResponse.Material> call = service.createMaterial(
                    titleBody,
                    filePart,
                    semesterBody,
                    unitBody,
                    userIdBody
            );

            call.enqueue(new Callback<MaterialsResponse.Material>() {
                @Override
                public void onResponse(Call<MaterialsResponse.Material> call,
                                       Response<MaterialsResponse.Material> response) {
                    handleUploadResponse(response);
                    temp.delete(); // Clean up temp file
                }

                @Override
                public void onFailure(Call<MaterialsResponse.Material> call, Throwable t) {
                    Toast.makeText(ButtonCreate.this,
                            "Upload failed: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    temp.delete(); // Clean up temp file
                }
            });

        } catch (IOException | SecurityException e) {
            Toast.makeText(this, "File processing error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void handleUploadResponse(Response<MaterialsResponse.Material> response) {
        if (response.isSuccessful()) {
            resetForm();
            Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show();
        } else {
            String errorMsg = "Server error: " + response.code();
            if (response.errorBody() != null) {
                try {
                    errorMsg += " - " + response.errorBody().string();
                } catch (IOException e) {
                    // Ignore
                }
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void resetForm() {
        editTitle.setText("");
        editSemester.setText("");
        editUnit.setText("");
        editDescription.setText("");
        selectedFileUri = null;
        previewContainer.setVisibility(View.GONE);
    }

    private String getFileName(Uri uri) {
        String name = "document.pdf";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex);
                }
            }
        } catch (SecurityException e) {
            // Fallback to default name
        }
        return name;
    }
}