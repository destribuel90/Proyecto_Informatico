package com.example.proyecto_informatico.network;

import com.example.proyecto_informatico.model.AuthResponse;
import com.example.proyecto_informatico.model.LoginRequest;
import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.model.User;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;
public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int id);
    @POST("users")
    Call<User> createUser(@Body User user);
    @POST("login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);
    @GET("materials")
    Call<MaterialsResponse> getMaterials();
    @GET("materials/{id}")
    Call<MaterialsResponse> getMaterialById(@Path("id") int id);
    @POST("materials")
    Call<Material> createMaterial(@Body Material material);
}