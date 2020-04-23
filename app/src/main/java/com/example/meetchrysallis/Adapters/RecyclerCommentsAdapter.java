package com.example.meetchrysallis.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.Models.Comentario;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

public class RecyclerCommentsAdapter extends RecyclerView.Adapter<RecyclerCommentsAdapter.ViewHolder>{
    private ArrayList<Comentario> listComentarios;
    private Context context;

    public RecyclerCommentsAdapter(ArrayList<Comentario> listComentarios, Context context) {
        this.listComentarios = listComentarios;
        this.context = context;
    }

    @Override
    public RecyclerCommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_comment, null, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerCommentsAdapter.ViewHolder holder, int position) {
        holder.asignarDatos(listComentarios.get(position));
    }

    @Override
    public int getItemCount() {
        return listComentarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvAnonimo;
        TextView tvFecha;
        TextView tvComentario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre        = itemView.findViewById(R.id.item_comentario_tvNombre);
            tvAnonimo       = itemView.findViewById(R.id.item_comentario_tvAnonimo);
            tvFecha         = itemView.findViewById(R.id.item_comentario_tvFecha);
            tvComentario    = itemView.findViewById(R.id.item_comentario_tvComent);
        }

        public void asignarDatos(Comentario coment) {
            if(coment.isMostrarNombre()){
                tvNombre.setText(coment.getSocio().getNombre() + " " + coment.getSocio().getApellidos());
//                tvNombre.setTypeface(tvNombre.getTypeface(), Typeface.BOLD);
                tvNombre.setVisibility(View.VISIBLE);
                tvAnonimo.setVisibility(View.GONE);
            }
            else{
                tvNombre.setVisibility(View.GONE);
                tvAnonimo.setVisibility(View.VISIBLE);
//                tvNombre.setText(context.getResources().getString(R.string.anonimo));
////                tvNombre.setTypeface(tvNombre.getTypeface(), Typeface.ITALIC);
////                tvNombre.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/raleway_lightitalic.ttf"));
//                Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/raleway_lightitalic.ttf");
//                tvNombre.setTypeface(typeFace);
            }

            tvFecha.setText(Utils.formateadorFechas(coment.getFecha()));
            tvComentario.setText(coment.getBody());
        }
    }
}
