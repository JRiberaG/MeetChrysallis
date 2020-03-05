package com.example.meetchrysallis.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.EventoService;
import com.example.meetchrysallis.Activities.EventoDetalladoActivity;
import com.example.meetchrysallis.Adapters.RecyclerAdapter;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private Context context;
    private ArrayList<Evento> eventos;

    //Constructor
    public HomeFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO: recuperar eventos de la base de datos
        /*
        //Lista de testeo: he creado eventos para comprobar que va bien el recycler y cardview ---
        ArrayList<Evento> eventosPrueba = new ArrayList<Evento>();
        inicializarEventos(eventosPrueba);
        //----------------------------------------------------------------------------------------*/
        EventoService eventoService = Api.getApi().create(EventoService.class);
        Call<List<Evento>> eventosCall = eventoService.getEventos();
        eventosCall.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                switch(response.code()){
                    case 200:
                    case 204:
                        eventos = (ArrayList<Evento>)response.body();

                        TextView tv_noEventos = getView().findViewById(R.id.fragment_home_tvNoEventos);

                        RecyclerView recycler = getView().findViewById(R.id.recyclerHome);
                        recycler.setLayoutManager(new GridLayoutManager(context,1));
                        RecyclerAdapter adapter = new RecyclerAdapter(eventos);
                        recycler.setAdapter(adapter);

                        if(adapter.getItemCount() <= 0){
                            tv_noEventos.setVisibility(View.VISIBLE);
                        }
                        else{
                            tv_noEventos.setVisibility(View.GONE);

                            //final ArrayList<Evento> eventos2 = eventos;
                            adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Evento e = eventos.get(position);
                                    Intent intent = new Intent(context, EventoDetalladoActivity.class);
                                    intent.putExtra("evento", e);
                                    startActivity(intent);
                                }
                            });
                        }
                        break;
                    default:
                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                //CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                CustomToast.mostrarInfo(context, getLayoutInflater(), t.toString());
            }
        });
    }
}
