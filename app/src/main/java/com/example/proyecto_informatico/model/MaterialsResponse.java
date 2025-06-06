package com.example.proyecto_informatico.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;

public class MaterialsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("content")
    private List<Material> content;
    @SerializedName("filters")
    private Filters filters;

    @SerializedName("pagination")
    private Pagination pagination;

    // Getters y setters
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<Material> getContent() {
        return content;
    }
    public void setContent(List<Material> content) {
        this.content = content;
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


    public class Material {

        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("professor")
        private String professor;

        // En el JSON la clave es "file_url", pero en Java conviene nombrarlo camelCase:
        @SerializedName("file_url")
        private String fileUrl;

        // En el JSON "semester" viene como string ("6"), por eso lo mapeamos a String.
        @SerializedName("semester")
        private String semester;

        // Ídem para "unit":
        @SerializedName("unit")
        private String unit;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("is_bookmarked")
        private boolean isBookmarked;

        // ====== Getters y Setters ======
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
    }
    public class Filters {
        @SerializedName("search")
        private String search;
        @SerializedName("semester")
        private String semester;
        @SerializedName("unit")
        private String unit;

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
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
    }
    public class Pagination {
        @SerializedName("current_page")
        private int currentPage;
        @SerializedName("total_pages")
        private int totalPages;

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

}
