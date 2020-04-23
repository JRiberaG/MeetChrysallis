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
import com.example.meetchrysallis.API.ApiService.TimeService;
import com.example.meetchrysallis.API.ApiWorldTime;
import com.example.meetchrysallis.Activities.EventoDetalladoActivity;
import com.example.meetchrysallis.Activities.MainActivity;
import com.example.meetchrysallis.Adapters.RecyclerEventoAdapter;
import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Comunidad;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Models.Time;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.sql.Timestamp;
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
    private RecyclerEventoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private TextView tvNoEventos;

    private EventoService eventoService = Api.getApi().create(EventoService.class);

    private Timestamp timestamp = new Timestamp(0);

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

        recogerIdsLayout();

        capturarTiempo();

////        final EventoService eventoService = Api.getApi().create(EventoService.class);
//        final Call<List<Evento>> eventosCall = eventoService.getEventos();
//        eventosCall.enqueue(new Callback<List<Evento>>() {
//            @Override
//            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
//                switch(response.code()){
//                    case 200:
//                    case 204:
//                        ArrayList<Evento> eventosRecogidos = (ArrayList<Evento>)response.body();
//
//                        actualizarEventosDisponibles(eventosRecogidos);
//
////                        TextView tvNoEventos = view.findViewById(R.id.fragment_home_tvNoEventos);
////                        recycler = view.findViewById(R.id.recyclerHome);
//
//                        actualizarAdapter();
//
//                        configurarSwipe(eventosCall);
//
////                        swipeRefreshLayout = view.findViewById(R.id.fragment_home_pullToRefresh);
////                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
////                            @Override
////                            public void onRefresh() {
////                                eventosCall.clone().enqueue(new Callback<List<Evento>>() {
////                                    @Override
////                                    public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
////                                        switch(response.code()){
////                                            case 200:
////                                            case 204:
////                                                ArrayList<Evento> eventosRecogidos = (ArrayList<Evento>)response.body();
////                                                eventosDelSocio.clear();
////
////                                                actualizarEventosDisponibles(eventosRecogidos);
////                                                /*//Buscamos de entre todos los eventos recogidos y guardamos sólo aquellos
////                                                //que le vaya a interesar al socio
////                                                if(eventosRecogidos != null){
////                                                    //Cogemos las comunidades interesadas del socio
////                                                    if (MainActivity.socio != null){
////                                                        for(Comunidad comunidad : MainActivity.socio.getComunidadesInteres()){
////                                                            for(Evento eve : eventosRecogidos){
////                                                                if (eve.getIdComunidad() == comunidad.getId())
////                                                                    eventosDelSocio.add(eve);
////                                                            }
////                                                        }
////                                                    }
////                                                }*/
////
////                                                adapter.notifyDataSetChanged();
////                                                swipeRefreshLayout.setRefreshing(false);
////
////                                                break;
////                                            default:
////                                                CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
////                                                break;
////                                        }
////                                    }
////
////                                    @Override
////                                    public void onFailure(Call<List<Evento>> call, Throwable t) {
////                                        CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
////                                    }
////                                });
////                            }
////                        });
//
//                        if(eventosDelSocio != null){
//                            if(adapter.getItemCount() <= 0){
//                                tvNoEventos.setVisibility(View.VISIBLE);
//                                recycler.setVisibility(View.GONE);
//                            }
//                            else{
//                                tvNoEventos.setVisibility(View.GONE);
//                                recycler.setVisibility(View.VISIBLE);
//
//                                configurarListenerAdapter();
////                                adapter.setOnItemClickListener(new RecyclerEventoAdapter.OnItemClickListener() {
////                                    @Override
////                                    public void onItemClick(int position) {
////                                        Evento e = eventosDelSocio.get(position);
////                                        //Hacemos una búsqueda exhaustiva del evento
////                                        Call<Evento> eventoCall = eventoService.getEventos(e.getId());
////
////                                        DialogProgress dp = new DialogProgress(context);
////                                        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando_evento));
////
////                                        eventoCall.clone().enqueue(new Callback<Evento>() {
////                                            @Override
////                                            public void onResponse(Call<Evento> call, Response<Evento> response) {
////                                                switch(response.code()){
////                                                    case 200:
////                                                    case 204:
////                                                        Evento eventoExhaustivo = response.body();
////
////                                                        Intent intent = new Intent(context, EventoDetalladoActivity.class);
////                                                        intent.putExtra("evento", eventoExhaustivo);
////                                                        startActivity(intent);
////                                                        break;
////                                                    default:
////                                                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
////                                                        break;
////                                                }
////
////                                                ad.dismiss();
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<Evento> call, Throwable t) {
////                                                CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
////                                                ad.dismiss();
////                                            }
////                                        });
////                                    }
////                                });
//                            }
//                        }
//                        break;
//                    default:
//                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
//                        break;
//                }
//            }
//            @Override
//            public void onFailure(Call<List<Evento>> call, Throwable t) {
//                //CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
//                CustomToast.mostrarInfo(context, getLayoutInflater(), t.toString());
//            }
//        });
    }

    private void capturarTiempo() {
        DialogProgress dp = new DialogProgress(context);
        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));

        TimeService timeService = ApiWorldTime.getApi().create(TimeService.class);
        Call<Time> timeCall = timeService.getTime();
        timeCall.enqueue(new Callback<Time>() {
            @Override
            public void onResponse(Call<Time> call, Response<Time> response) {
                if (response.code() == 200) {
                    Time tiempo = response.body();
                    timestamp = tiempo.getDatetime();

                } else {
                    timestamp = Utils.capturarTimestampActual();
                }

                ad.dismiss();
                cargarEventos();
            }

            @Override
            public void onFailure(Call<Time> call, Throwable t) {
                timestamp = Utils.capturarTimestampActual();
                ad.dismiss();
                cargarEventos();
            }
        });
    }

    private void cargarEventos() {
        //        final EventoService eventoService = Api.getApi().create(EventoService.class);
        final Call<List<Evento>> eventosCall = eventoService.getEventos();
        eventosCall.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                switch(response.code()){
                    case 200:
                    case 204:
                        ArrayList<Evento> eventosRecogidos = (ArrayList<Evento>)response.body();

                        actualizarEventosDisponibles(eventosRecogidos);

                        actualizarAdapter();

                        configurarSwipe(eventosCall);

                        if(eventosDelSocio != null){
                            if(adapter.getItemCount() <= 0){
                                tvNoEventos.setVisibility(View.VISIBLE);
                                recycler.setVisibility(View.GONE);
                            }
                            else{
                                tvNoEventos.setVisibility(View.GONE);
                                recycler.setVisibility(View.VISIBLE);

                                configurarListenerAdapter();
                            }
                        }
                        break;
                    default:
                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message(), true);
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                CustomToast.mostrarInfo(context, getLayoutInflater(), t.toString(), true);
            }
        });

    }

    private void configurarListenerAdapter() {
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

//                                actualizarAdapter();
                                break;
                            default:
                                CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message(), true);
                                break;
                        }

                        ad.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Evento> call, Throwable t) {
                        CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                        ad.dismiss();
                    }
                });
            }
        });
    }

    private void configurarSwipe(final Call<List<Evento>> eventosCall) {
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

                                actualizarEventosDisponibles(eventosRecogidos);

                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);

                                break;
                            default:
                                CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message(), true);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Evento>> call, Throwable t) {
                        CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                    }
                });
            }
        });
    }

    private void actualizarAdapter() {
        recycler.setLayoutManager(new GridLayoutManager(context,1));
        adapter = new RecyclerEventoAdapter(eventosDelSocio, context);
        recycler.setAdapter(adapter);
    }

    private void recogerIdsLayout() {
        tvNoEventos = view.findViewById(R.id.fragment_home_tvNoEventos);
        recycler = view.findViewById(R.id.recyclerHome);
        swipeRefreshLayout = view.findViewById(R.id.fragment_home_pullToRefresh);
    }

    private void actualizarEventosDisponibles(ArrayList<Evento> eventosRecogidos) {
        final DialogProgress dp = new DialogProgress(context);
        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));
        //Buscamos de entre todos los eventos recogidos y guardamos sólo aquellos
        //que le vaya a interesar al socio
        if(eventosRecogidos != null){
            //Cogemos las comunidades interesadas del socio
            if (MainActivity.socio != null){
                for(Comunidad comunidad : MainActivity.socio.getComunidadesInteres()){
                    for(Evento eve : eventosRecogidos){
                        if (eve.getIdComunidad() == comunidad.getId()){
                            if (eve.getFecha_limite() != null) {

//                                recogerEventosTimestamp(eve);
                                if (eve.getFecha_limite().after(timestamp)){
//                                    recogerEventoAPartirDeFecha(eve);
                                    comprobarEvento(eve);
                                }
                            } else {
                                if (eve.getFecha().after(timestamp)) {
                                    comprobarEvento(eve);
                                }
//                                }
//                                    recogerEventoAPartirDeFecha(eve, Utils.capturarTimestampActual(), false);
//                                }
                            }
                        }
                    }
                }
            }
        }
        ad.dismiss();
    }

//    private void recogerEventosTimestamp(final Evento eve) {
////        final boolean[] recogida = {false};
////        final Timestamp[] tsReal = new Timestamp[1];
//
//        if (timestamp == null) {
//            TimeService timeService = ApiWorldTime.getApi().create(TimeService.class);
//            Call<Time> timeCall = timeService.getTime();
//            timeCall.enqueue(new Callback<Time>() {
//                @Override
//                public void onResponse(Call<Time> call, Response<Time> response) {
//                    if (response.code() == 200) {
//                        Time tiempo = response.body();
//                        timestamp = new Timestamp(0);
//                        timestamp = tiempo.getDatetime();
//
//                        if (timestamp != null) {
//                            recogerEventoAPartirDeFecha(eve, timestamp, true);
////                            if (eve.getFecha_limite().after(tsReal[0])){
////                                recogerEventoAPartirDeFecha(eve, tsReal[0]);
////                            }
////                            recogida[0] = true;
//                        } else {
//                            recogerEventoAPartirDeFecha(eve, Utils.capturarTimestampActual(), true);
//                        }
//
//                    } else {
//                        //salió mal
//                        recogerEventoAPartirDeFecha(eve, Utils.capturarTimestampActual(), true);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Time> call, Throwable t) {
//                    // salió mal
//                    recogerEventoAPartirDeFecha(eve, Utils.capturarTimestampActual(), true);
//                }
//            });
//        } else {
//            recogerEventoAPartirDeFecha(eve, timestamp, true);
//        }
//    }

//    private void recogerEventoAPartirDeFecha(Evento eve, Timestamp ts) {
//        //Se añade a la lista si el evento es después a la fecha actual (no se ha celebrado)
//        if (eve.getFecha().after(ts)) {
//            boolean asistido = false;
//            Iterator ite = eve.getAsistencia().iterator();
//            while (!asistido && ite.hasNext()) {
//                Asistir asistir = (Asistir)ite.next();
//                if (asistir.getIdSocio() == MainActivity.socio.getId()) {
//                    asistido = true;
//                }
//            }
//            if (!asistido) {
//                eventosDelSocio.add(eve);
//            }
//        }
//    }
//    private void recogerEventoAPartirDeFecha(Evento eve, Timestamp ts, boolean fechaLimite) {
//
//        if (fechaLimite) {
//            if (eve.getFecha_limite().after(ts)){
//                comprobarEvento(eve);
//            }
//        } else {
//            if (eve.getFecha().after(ts)) {
//                comprobarEvento(eve);
//            }
//        }
//        //Se añade a la lista si el evento es después a la fecha actual (no se ha celebrado)
//        if (eve.getFecha().after(ts)) {
////            boolean asistido = false;
////            Iterator ite = eve.getAsistencia().iterator();
////            while (!asistido && ite.hasNext()) {
////                Asistir asistir = (Asistir)ite.next();
////                if (asistir.getIdSocio() == MainActivity.socio.getId()) {
////                    asistido = true;
////                }
////            }
////            if (!asistido) {
////                eventosDelSocio.add(eve);
////            }
//        }
//    }

    private void comprobarEvento(Evento eve) {
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