package com.example.proyecto_informatico.model;

import com.google.gson.annotations.SerializedName;

public class MaterialsResponse<T> {

    @SerializedName("status")
    private String status;

    @SerializedName(value = "content", alternate = { "data" })
    private T payload;

    @SerializedName("filters")
    private Filters filters;

    @SerializedName("pagination")
    private Pagination pagination;

    // ----- Getters & Setters -----

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * Subclase para filtros opcionales en paginación.
     */
    public static class Filters {
        @SerializedName("search")
        private String search;

        @SerializedName("semester")
        private String semester;

        @SerializedName("unit")
        private String unit;

        // Getters & Setters omitted for brevity
    }

    public static class Pagination {
        @SerializedName("current_page")
        private int currentPage;

        @SerializedName("total_pages")
        private int totalPages;

        // Getters & Setters omitted for brevity
    }

    /**
     * Modelo Material como clase estática.
     */
    public static class Material {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("professor")
        private String professor;

        @SerializedName(value = "file_url", alternate = { "file_path" })
        private String fileUrl;

        @SerializedName("semester")
        private String semester;

        @SerializedName("unit")
        private String unit;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("is_bookmarked")
        private boolean isBookmarked;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProfessor() {
            return professor;
        }

        public void setProfessor(String professor) {
            this.professor = professor;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public boolean isBookmarked() {
            return isBookmarked;
        }

        public void setBookmarked(boolean bookmarked) {
            isBookmarked = bookmarked;
        }
// Getters & Setters omitted for brevity
    }
}