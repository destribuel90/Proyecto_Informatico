package com.example.proyecto_informatico.network;

import com.example.proyecto_informatico.model.AuthResponse;
import com.example.proyecto_informatico.model.LoginRequest;
import com.example.proyecto_informatico.model.MaterialsResponse;
import com.example.proyecto_informatico.model.RegisterResponse;
import com.example.proyecto_informatico.model.SignUpRequest;
import com.example.proyecto_informatico.model.User;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int id);
    @POST("users")
    Call<User> createUser(@Body User user);
    @POST("login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);
    @POST("logout")
    Call<Void> logoutUser();
    @POST("register")
    Call<RegisterResponse> registerUser(@Body SignUpRequest req);



    @GET("materials")
    Call<MaterialsResponse<List<MaterialsResponse.Material>>> getAllMaterials(
            @Query("search") String search,
            @Query("semester") String semester,
            @Query("unit") Integer unit,
            @Query("page") Integer page
    );

    @GET("materials/{id}")
    Call<MaterialsResponse<MaterialsResponse.Material>> getMaterialById(@Path("id") int id);
    @Multipart
    @POST("materials")
    Call<Material> createMaterial(
            @Part("title") RequestBody title,
            @Part MultipartBody.Part file,
            @Part("semester") RequestBody semester,
            @Part("unit") RequestBody unit,
            @Part("user_id") RequestBody userId

    );

}