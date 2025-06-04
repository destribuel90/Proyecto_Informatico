package com.example.proyecto_informatico;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_informatico.R;
import com.example.proyecto_informatico.model.Material;

import java.util.List;
public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>  {
    private List<Material> materialList;
    public MaterialAdapter(List<Material> materialList){
        this.materialList = materialList;
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCover;
        TextView txtLabel;
        TextView txtDescription;
        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            txtDescription = itemView.findViewById(R.id.txtDescription);

        }
    }
    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material material = materialList.get(position);
        holder.txtLabel.setText(material.getTitle());
        holder.txtDescription.setText(material.getSemester());
    }
    @Override
    public int getItemCount() {
        return materialList.size();
    }
}
