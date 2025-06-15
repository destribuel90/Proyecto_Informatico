package com.example.proyecto_informatico.model;

import com.google.gson.annotations.SerializedName;

public class LogoutResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Constructor vacío (necesario para Retrofit/Gson)
    public LogoutResponse() {}

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
