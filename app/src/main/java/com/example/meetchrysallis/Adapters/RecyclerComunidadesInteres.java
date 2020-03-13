package com.example.meetchrysallis.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.Models.Comunidad;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

public class RecyclerComunidadesInteres extends RecyclerView.Adapter<RecyclerComunidadesInteres.ViewHolder>{
    private Context context;
    private ArrayList<Comunidad> comunidades;

    public RecyclerComunidadesInteres(ArrayList<Comunidad> comunidades, Context context) {
        this.comunidades = comunidades;
        this.context = context;
    }

    @Override
    public RecyclerComunidadesInteres.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerComunidadesInteres.ViewHolder holder, int position) {
        holder.asignarDatos(comunidades.get(position));
    }

    @Override
    public int getItemCount() {
        return comunidades.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(android.R.id.text1);
        }

        public void asignarDatos(Comunidad c) {
            nombre.setText(c.getNombre());
            nombre.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }
}

