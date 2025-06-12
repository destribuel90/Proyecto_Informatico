package com.example.proyecto_informatico;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_informatico.model.RegisterResponse;
import com.example.proyecto_informatico.model.SignUpRequest;
import com.example.proyecto_informatico.network.ApiService;
import com.example.proyecto_informatico.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {
    private static final String TAG = "SignIn";

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Ajuste edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa Retrofit
        RetrofitClient.init(getApplicationContext());

        // Referencias de vistas
        etName             = findViewById(R.id.et_name);
        etEmail            = findViewById(R.id.et_email);
        etPassword         = findViewById(R.id.et_password);
        etConfirmPassword  = findViewById(R.id.et_password_confirm);
        btnSignUp          = findViewById(R.id.btn_register);
        tvGoToLogin        = findViewById(R.id.tv_go_to_login);

        // Navegar a Login
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, Login.class));
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            String name     = etName.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String pass     = etPassword.getText().toString();
            String passConf = etConfirmPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || passConf.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(passConf)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            sendSignUp(name, email, pass);
        });
    }

    private void sendSignUp(String name, String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        SignUpRequest req = new SignUpRequest(name, email, password);

        // Cambiamos a RegisterResponse
        Call<RegisterResponse> call = apiService.registerUser(req);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int newUserId = response.body().getData().getId();
                    Log.d(TAG, "Usuario registrado. ID = " + newUserId);

                    Toast.makeText(
                            SignIn.this,
                            "¡Registro exitoso! Ahora inicia sesión.",
                            Toast.LENGTH_LONG
                    ).show();

                    startActivity(new Intent(SignIn.this, Login.class));
                    finish();
                } else {
                    int code = response.code();
                    Log.e(TAG, "Error en registro. Código HTTP: " + code);
                    Toast.makeText(
                            SignIn.this,
                            "No se pudo registrar (" + code + ")",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
                Toast.makeText(
                        SignIn.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
