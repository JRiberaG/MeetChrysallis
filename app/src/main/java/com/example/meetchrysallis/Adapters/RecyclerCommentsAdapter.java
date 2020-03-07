package com.example.meetchrysallis.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.Models.Comentario;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

public class RecyclerCommentsAdapter extends RecyclerView.Adapter<RecyclerCommentsAdapter.ViewHolder>{
    private ArrayList<Comentario> comentarios;

    public RecyclerCommentsAdapter(ArrayList<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public RecyclerCommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, null, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerCommentsAdapter.ViewHolder holder, int position) {
        holder.asignarDatos(comentarios.get(position));
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView fecha;
        TextView comentario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.item_comentario_tvNombre);
            fecha = itemView.findViewById(R.id.item_comentario_tvFecha);
            comentario = itemView.findViewById(R.id.item_comentario_tvComent);
        }

        public void asignarDatos(Comentario c) {
            if(c.isMostrarNombre())
                //FIXME: conseguir el nombre del socio comentante
                //nombre.setText(c.getSocio().getNombre() + " " + c.getSocio().getApellidos());
                nombre.setText("prueba prueba");
            else
                nombre.setText("Anónimo");
            //FIXME: parsear fecha --> fecha.setText(comentario.getFecha());
            fecha.setText("01/01/2000");
            comentario.setText(c.getBody());
        }
    }
}
