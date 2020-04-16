package com.example.meetchrysallis.Fragments;

import android.app.AlertDialog;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.EventoService;
import com.example.meetchrysallis.Activities.EventoDetalladoActivity;
import com.example.meetchrysallis.Activities.MainActivity;
import com.example.meetchrysallis.Adapters.RecyclerEventoAdapter;
import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Comunidad;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private Context context;
    private ArrayList<Evento> eventosDelSocio = new ArrayList<>();
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    //Constructor
    public HomeFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EventoService eventoService = Api.getApi().create(EventoService.class);
        final Call<List<Evento>> eventosCall = eventoService.getEventos();
        eventosCall.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                switch(response.code()){
                    case 200:
                    case 204:
                        ArrayList<Evento> eventosRecogidos = (ArrayList<Evento>)response.body();

                        actualizarRecycler(eventosRecogidos);

                        TextView tvNoEventos = view.findViewById(R.id.fragment_home_tvNoEventos);
                        recycler = view.findViewById(R.id.recyclerHome);

                        recycler.setLayoutManager(new GridLayoutManager(context,1));
                        final RecyclerEventoAdapter adapter = new RecyclerEventoAdapter(eventosDelSocio);
                        recycler.setAdapter(adapter);

                        swipeRefreshLayout = view.findViewById(R.id.fragment_home_pullToRefresh);
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                eventosCall.clone().enqueue(new Callback<List<Evento>>() {
                                    @Override
                                    public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                                        switch(response.code()){
                                            case 200:
                                            case 204:
                                                ArrayList<Evento> eventosRecogidos = (ArrayList<Evento>)response.body();
                                                eventosDelSocio.clear();

                                                actualizarRecycler(eventosRecogidos);
                                                /*//Buscamos de entre todos los eventos recogidos y guardamos sólo aquellos
                                                //que le vaya a interesar al socio
                                                if(eventosRecogidos != null){
                                                    //Cogemos las comunidades interesadas del socio
                                                    if (MainActivity.socio != null){
                                                        for(Comunidad comunidad : MainActivity.socio.getComunidadesInteres()){
                                                            for(Evento eve : eventosRecogidos){
                                                                if (eve.getIdComunidad() == comunidad.getId())
                                                                    eventosDelSocio.add(eve);
                                                            }
                                                        }
                                                    }
                                                }*/

                                                adapter.notifyDataSetChanged();
                                                swipeRefreshLayout.setRefreshing(false);

                                                break;
                                            default:
                                                CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Evento>> call, Throwable t) {
                                        CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                    }
                                });
                            }
                        });

                        if(eventosDelSocio != null){
                            if(adapter.getItemCount() <= 0){
                                // FIXME
                                //  Si no hay eventos disponibles no se muestra error cuando sí debería.
                                //  En el layout, antes estaba el linear del error fuera del swipeRefresh
                                //  pero queremos que se actualice al hacer swipe, asi que tiene uqe estar dentro
                                tvNoEventos.setVisibility(View.VISIBLE);
                                recycler.setVisibility(View.GONE);
                            }
                            else{
                                tvNoEventos.setVisibility(View.GONE);
                                recycler.setVisibility(View.VISIBLE);

                                adapter.setOnItemClickListener(new RecyclerEventoAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Evento e = eventosDelSocio.get(position);
                                        //Hacemos una búsqueda exhaustiva del evento
                                        Call<Evento> eventoCall = eventoService.getEventos(e.getId());

                                        DialogProgress dp = new DialogProgress(context);
                                        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando_evento));

                                        eventoCall.clone().enqueue(new Callback<Evento>() {
                                            @Override
                                            public void onResponse(Call<Evento> call, Response<Evento> response) {
                                                switch(response.code()){
                                                    case 200:
                                                    case 204:
                                                        Evento eventoExhaustivo = response.body();

                                                        Intent intent = new Intent(context, EventoDetalladoActivity.class);
                                                        intent.putExtra("evento", eventoExhaustivo);
                                                        startActivity(intent);
                                                        break;
                                                    default:
                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                                                        break;
                                                }

                                                ad.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<Evento> call, Throwable t) {
                                                CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                                                ad.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
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

    private void actualizarRecycler(ArrayList<Evento> eventosRecogidos) {
        //Buscamos de entre todos los eventos recogidos y guardamos sólo aquellos
        //que le vaya a interesar al socio
        if(eventosRecogidos != null){
            //Cogemos las comunidades interesadas del socio
            if (MainActivity.socio != null){
                for(Comunidad comunidad : MainActivity.socio.getComunidadesInteres()){
                    for(Evento eve : eventosRecogidos){
                        if (eve.getIdComunidad() == comunidad.getId()){
                            //Se añade a la lista si el evento es después a la fecha actual (no se ha celebrado)
                            if (eve.getFecha().after(Utils.capturarTimestampActual())) {
                                boolean asistido = false;
                                Iterator ite = eve.getAsistencia().iterator();
                                while (!asistido && ite.hasNext()) {
                                    Asistir asistir = (Asistir)ite.next();
                                    if (asistir.getIdSocio() == MainActivity.socio.getId()) {
                                        asistido = true;
                                    }
                                }
                                if (!asistido) {
                                    eventosDelSocio.add(eve);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}