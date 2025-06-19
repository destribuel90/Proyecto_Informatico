package com.example.proyecto_informatico.model;

import com.google.gson.annotations.SerializedName;

public class MaterialUpdateRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("semester")
    private String semester;

    @SerializedName("unit")
    private String unit;

    // Para el PDF opcional
    @SerializedName("file_base64")
    private String fileBase64;

    @SerializedName("file_extension")
    private String fileExtension;

    // getters & setters...
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getFileBase64() { return fileBase64; }
    public void setFileBase64(String fileBase64) { this.fileBase64 = fileBase64; }

    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
}
