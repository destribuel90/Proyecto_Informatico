package com.example.proyecto_informatico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_informatico.model.User;
import com.example.proyecto_informatico.network.RetrofitClient;
import com.example.proyecto_informatico.model.AuthResponse;
import com.example.proyecto_informatico.model.LoginRequest;
import com.example.proyecto_informatico.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    Button btnLogin;
    private static final String TAG = "Login";
    private EditText etEmail, etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RetrofitClient.init(getApplicationContext());
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            sendLogin(email, password);
        });
    }

    public void sendLogin(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<AuthResponse> call = apiService.loginUser(new LoginRequest(email, password));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    String token = authResponse.getToken();
                    String userId = String.valueOf(authResponse.getUserId());
                    // 1) Guardar el token en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("mi_prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("TOKEN_KEY", token)
                            .putString("user_id", userId)
                            .apply();

                    Log.d(TAG, "Token guardado: " + token);

                    User user = authResponse.getUser();
                    Log.d(TAG, "User: " + user.getName());
                    Log.d(TAG, "Email: " + user.getEmail());
                    Log.d(TAG, "Role: " + user.getRole());
                    Log.d(TAG, "Profile Photo Path: " + user.getProfilePhotoPath());

                    // 2) Pasar a MainActivity
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    int codigo = response.code();
                    Log.e(TAG, "Error en login. Código HTTP: " + codigo);
                    Toast.makeText(
                            Login.this,
                            "Credenciales inválidas o error del servidor (" + codigo + ")",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "Fallo en la llamada: " + t.getMessage());
                Toast.makeText(
                        Login.this,
                        "No se pudo conectar al servidor: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

}