package com.example.proyecto_informatico.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_informatico.R;
import com.example.proyecto_informatico.model.MaterialsResponse.Material;

import java.util.List;

/**
 * Adaptador para mostrar una lista de materiales en un RecyclerView.
 */
public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private final List<Material> materialList;
    private final OnItemClickListener listener;

    /**
     * Interfaz para manejar eventos de clic en los items.
     */
    public interface OnItemClickListener {
        void onItemClick(Material material, int position);
    }

    public MaterialAdapter(List<Material> materialList, OnItemClickListener listener) {
        this.materialList = materialList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new MaterialViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material material = materialList.get(position);
        holder.bind(material);
    }

    @Override
    public int getItemCount() {
        int size = materialList.size();
        return size;
    }

    /**
     * ViewHolder que representa cada elemento de la lista.
     */
    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtLabel;
        TextView txtUnidad;
        TextView txtSemestre;
        RatingBar rating;
        OnItemClickListener listener;

        MaterialViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            txtUnidad = itemView.findViewById(R.id.txtUnidad);
            txtSemestre = itemView.findViewById(R.id.txtSemestre);
//
            this.listener = listener;
        }

        /**
         * Vincula los datos del material y el listener de clic.
         */
        void bind(final Material material) {
            txtLabel.setText(material.getTitle());
            txtUnidad.setText("Unidad " + material.getUnit());
            txtSemestre.setText("Semestre " + material.getSemester());
//            rating.setRating(material.getRating());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(material, getAdapterPosition());
                }
            });
        }
    }
} // Fin de la clase MaterialAdapter
