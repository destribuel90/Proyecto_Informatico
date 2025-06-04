package com.example.proyecto_informatico.model;
import com.google.gson.annotations.SerializedName;

public class Material {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int idUser;
    @SerializedName("title")
    private String title;
    @SerializedName("semester")
    private String semester;
    @SerializedName("file_path")
    private String filePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
