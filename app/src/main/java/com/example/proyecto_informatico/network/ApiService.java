package com.example.proyecto_informatico.network;

import com.example.proyecto_informatico.model.Material;
import com.example.proyecto_informatico.model.User;

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
    @POST("users/login")
    Call<User> loginUser(@Body User user);
    @GET("materials")
    Call<List<Material>> getMaterials();
    @GET("materials/{id}")
    Call<Material> getMaterialById(@Path("id") int id);
    @POST("materials")
    Call<Material> createMaterial(@Body Material material);
}