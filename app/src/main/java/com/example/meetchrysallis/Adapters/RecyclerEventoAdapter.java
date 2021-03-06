package com.example.meetchrysallis.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

public class RecyclerEventoAdapter extends RecyclerView.Adapter<RecyclerEventoAdapter.ViewHolder>{
    private ArrayList<Evento> eventos;
    private OnItemClickListener onItemClickListener;
    private Context ctx;

    public RecyclerEventoAdapter(ArrayList<Evento> eventos, Context ctx) {
        this.eventos = eventos;
        this.ctx = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_evento, null, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.asignarDatos(eventos.get(position));
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    //Interface creada per poder utilizar un OnItemClickListener manual
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView titulo;
        TextView ubicacion;
        TextView fecha;
        TextView asistentes;

        public ViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imagen = itemView.findViewById(R.id.cv_imagen);
            titulo = itemView.findViewById(R.id.cv_titulo);
            ubicacion = itemView.findViewById(R.id.cv_ubicacion);
            fecha = itemView.findViewById(R.id.cv_fecha);
            asistentes = itemView.findViewById(R.id.cv_asistentes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(pos);
                        }
                    }
                }
            });
        }

        public void asignarDatos(Evento evento) {
            Utils.asignarImagenComunidad(ctx, imagen, evento.getIdComunidad());
            titulo.setText(evento.getTitulo());
            ubicacion.setText(evento.getUbicacion());

            if(evento.getFecha() != null)
                fecha.setText(Utils.formateadorFechas(evento.getFecha()));
            else
                fecha.setText("");

            if(evento.getAsistencia() != null) {
                int numAsistentes = 0;
                for (Asistir a : evento.getAsistencia()) {
                    numAsistentes += a.getNumAsistentes();
                }
                asistentes.setText(String.valueOf(numAsistentes));
            }
            else {
                asistentes.setText("0");
            }
        }
    }
}
