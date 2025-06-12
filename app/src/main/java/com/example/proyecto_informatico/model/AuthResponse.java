package com.example.proyecto_informatico.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    // Getter estándar
    public String getToken() {
        return token;
    }

    // Setter estándar
    public void setToken(String token) {
        this.token = token;
    }

    // Getter estándar
    public User getUser() {
        return user;
    }

    // Setter estándar
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Devuelve el ID del usuario anidado,
     * o null si no hay objeto user.
     */
    public Integer getUserId() {
        return (user != null)
                ? user.getId()
                : null;
    }
}
