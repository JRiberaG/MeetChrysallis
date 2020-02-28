package com.example.meetchrysallis.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.Activities.EventoDetalladoActivity;
import com.example.meetchrysallis.Adapters.RecyclerAdapter;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    private Context context;

    public HomeFragment(Context ctx) {
        this.context = ctx;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Lista de testeo: he creado eventos para comprobar que va bien el recycler y cardview ---
        ArrayList<Evento> eventosPrueba = new ArrayList<Evento>();
        inicializarEventos(eventosPrueba);
        //----------------------------------------------------------------------------------------

        RecyclerView recycler = getView().findViewById(R.id.recyclerHome);
        recycler.setLayoutManager(new GridLayoutManager(context,1));
        RecyclerAdapter adapter = new RecyclerAdapter(eventosPrueba);
        recycler.setAdapter(adapter);

        final ArrayList<Evento> eventosPrueba2 = eventosPrueba;
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Evento e = eventosPrueba2.get(position);
//                Toast.makeText(context, "ITEM CLICKADO! - " + e.getDescripcion(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, EventoDetalladoActivity.class);
                startActivity(intent);
            }
        });
    }



    // ================ BORRAR ================
    private void inicializarEventos(ArrayList<Evento> eventosPrueba) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = "2014-02-01",
                dateString1 = "2018-10-20",
                dateString2 = "2014-03-24",
                dateString3 = "2014-08-11";
        Date d = null,
                d1 = null,
                d2 = null,
                d3 = null;
        try {
            d = sdf.parse(dateString);
            d1 = sdf.parse(dateString1);
            d2 = sdf.parse(dateString2);
            d3 = sdf.parse(dateString3);
        } catch (ParseException e) {}
        eventosPrueba.add(new Evento((short)1, "Quedada masiva en polideportivo", d, "Barcelona", "descripcionPrueba", null, null, null, null, null, null));
        eventosPrueba.add(new Evento((short)1, "Quedada - Playa", d1, "Castelledefdels", "dqwijdwq", null, null, null, null, null, null));
        eventosPrueba.add(new Evento((short)1, "Quedada en local", d2, "Santa Coloma", "dqwewqeqw", null, null, null, null, null, null));
        eventosPrueba.add(new Evento((short)1, "Manifestación pacífica", d3, "Hospitalet", "dwqewq", null, null, null, null, null, null));
    }
}
