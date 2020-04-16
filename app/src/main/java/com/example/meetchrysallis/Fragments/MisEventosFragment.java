package com.example.meetchrysallis.Fragments;

import android.app.AlertDialog;
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

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.AsistirService;
import com.example.meetchrysallis.API.ApiService.EventoService;
import com.example.meetchrysallis.Activities.EventoDetalladoActivity;
import com.example.meetchrysallis.Activities.MainActivity;
import com.example.meetchrysallis.Adapters.RecyclerEventoAdapter;
import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisEventosFragment extends Fragment {

    private Context context;
    private View view;

    private ArrayList<Evento> eventos;
    private ArrayList<Asistir> asistirsSocio;

    private EventoService eventoService = Api.getApi().create(EventoService.class);

    public MisEventosFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_eventos, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * PASOS:
         *  1- Recoger todos los asistirs que haya en la BD del socio
         *  2- De cada asistir, recoger la ID del evento y añadirla a una lista de shorts
         *  3- Recoger todos los eventos (simples) de la base de datos
         *  4- Una vez cogidos todos los eventos y guardados en un arrayList, recoger aquellos
         *      que coincidan las IDS con las de los asistirs
         *
         *      Ejemplo:
         *          El socio asistió/rá a los eventos con ID 2, 6, 7. Buscar en la lista de eventos
         *          aquellos que tengan esos IDs y guardarlos en una lista aparte para poder trabajar mejor
         *          con ellos.
         */
        recogerAsistirs();
    }

    private void recogerAsistirs() {
        DialogProgress dp = new DialogProgress(context);
        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));

        AsistirService asistirService = Api.getApi().create(AsistirService.class);
        Call<ArrayList<Asistir>> callAsistirs = asistirService.getAsistirBySocio(MainActivity.socio.getId());
        callAsistirs.enqueue(new Callback<ArrayList<Asistir>>() {
            @Override
            public void onResponse(Call<ArrayList<Asistir>> call, Response<ArrayList<Asistir>> response) {
                switch(response.code()) {
                    case 200:
                    case 202:
                    case 204:
                        asistirsSocio = response.body();
                        ArrayList<Short> idsEventos = new ArrayList<>();

                        for (Asistir asis : asistirsSocio) {
                            idsEventos.add(asis.getIdEvento());
                        }

                        recogerEventos(idsEventos);
                        break;
                    case 404:
                        // No se encontró ningún asistir
                        break;
                    default:
                        CustomToast.mostrarWarning(context, getLayoutInflater(), response.code() + " - " + response.message());
                        break;
                }
                ad.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Asistir>> call, Throwable t) {
                CustomToast.mostrarInfo(context, getLayoutInflater(), getString(R.string.error_conexion_db));
                ad.dismiss();
            }
        });
    }

    private void recogerEventos(final ArrayList<Short> idsEventos) {
        Call<List<Evento>> callEventos = eventoService.getEventos();
        callEventos.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                switch(response.code()) {
                    case 200:
                    case 202:
                    case 204:
                        eventos = (ArrayList<Evento>) response.body();
                        ArrayList<Evento> eventosSocio = new ArrayList<>();

                        for(Evento evento : eventos) {
                            for (Short idEvento : idsEventos) {
                                if (evento.getId() == idEvento) {
                                    eventosSocio.add(evento);
                                }
                            }
                        }

                        // Pasamos a separar los eventos en funcion de si se realizaron o no
                        ArrayList<Evento> eventosAsistidos = new ArrayList<>();
                        ArrayList<Evento> eventosPendientes = new ArrayList<>();

                        for (Evento evento : eventosSocio) {
                            // Si la fecha del evento es anterior a la actual (ya pasó)
                            // se añade a una lista
                            if (evento.getFecha().before(Utils.capturarTimestampActual())) {
                                eventosAsistidos.add(evento);
                            }
                            // Si el evento no se ha celebrado aun, se guarda en otra lista
                            else {
                                eventosPendientes.add(evento);
                            }
                        }

                        actualizarRecyclerPendientes(eventosPendientes);
                        actualizarRecyclerAsistidos(eventosAsistidos);
                        break;
                    case 404:
                        // No se encontraron eventos (???)
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

    private void actualizarRecyclerAsistidos(final ArrayList<Evento> eventosAsistidos) {
        RecyclerView recycler = view.findViewById(R.id.recyclerEventosAsistidos);

        recycler.setLayoutManager(new GridLayoutManager(context,1));
        RecyclerEventoAdapter adapter = new RecyclerEventoAdapter(eventosAsistidos);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerEventoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                llamarApiEvento(eventosAsistidos, position);
            }
        });
    }

    private void actualizarRecyclerPendientes(final ArrayList<Evento> eventosPendientes) {
        RecyclerView recycler = view.findViewById(R.id.recyclerEventosPendientes);

        recycler.setLayoutManager(new GridLayoutManager(context,1));
        RecyclerEventoAdapter adapter = new RecyclerEventoAdapter(eventosPendientes);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerEventoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                llamarApiEvento(eventosPendientes, position);
            }
        });
    }

    private void llamarApiEvento(ArrayList<Evento> listaEventos, int position) {
        // Recogemos el evento clickado y llamamos a la api para recuperar el evento al completo
        Evento eventoSimple = listaEventos.get(position);
        Call<Evento> eventoCall = eventoService.getEventos(eventoSimple.getId());

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
}
