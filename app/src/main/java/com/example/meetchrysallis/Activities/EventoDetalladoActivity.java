package com.example.meetchrysallis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.AsistirService;
import com.example.meetchrysallis.API.ApiService.ComentarioService;
import com.example.meetchrysallis.API.ApiService.EventoService;
import com.example.meetchrysallis.Adapters.RecyclerCommentsAdapter;
import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Comentario;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoDetalladoActivity extends AppCompatActivity {

    private Button btnMultifuncion;
    private Button btnComentar;
    private ImageView expand;
    private RatingBar ratingBar;

    private boolean asistido = false;
    private boolean valorado = false;
    private boolean expandido = false;
    private boolean eventoPasado = false;

    private Evento evento;
    private Asistir asistir;

    private ComentarioService comentarioService = Api.getApi().create(ComentarioService.class);
    private AsistirService asistirService       = Api.getApi().create(AsistirService.class);

    private TextView tvNumComentarios;
    private TextView tvValoracionMedia;
    private LinearLayout linearRatingMedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detallado);

        evento = (Evento)getIntent().getExtras().getSerializable("evento");

        // --------------- ELEMENTOS DEL LAYOUT ---------------
        TextView tvTitulo           = findViewById(R.id.eve_det_tvNombre);
        TextView tvFecha            = findViewById(R.id.eve_det_tvFecha);
        TextView tvUbicacion        = findViewById(R.id.eve_det_tvUbicacion);
        TextView tvFechaLimite      = findViewById(R.id.eve_det_tvFechaLimite);
        TextView tvComunidad        = findViewById(R.id.eve_det_tvComunidad);
        tvValoracionMedia  = findViewById(R.id.eve_det_tvStars);
        tvNumComentarios            = findViewById(R.id.eve_det_tvNumComments);
        TextView tvDescripcion      = findViewById(R.id.eve_det_tvDescripcion);
        TextView tvNoComments       = findViewById(R.id.eve_det_tvNoComments);

        LinearLayout linearFechaLim     = findViewById(R.id.eve_det_linearlayout_fechaLim);
        linearRatingMedio  = findViewById(R.id.eve_det_linearLayout_ratingMedio);
        LinearLayout linearRatear       = findViewById(R.id.eve_det_linearLayout_rating);
        LinearLayout linearComentarios  = findViewById(R.id.eve_det_linearLayoutComentarios);

        btnComentar     = findViewById(R.id.eve_det_btnComentar);
        btnMultifuncion = findViewById(R.id.eve_det_btnAsistire);

        ratingBar = findViewById(R.id.eve_det_ratingBar);

        RecyclerView recyclerComments = findViewById(R.id.eve_det_recyclerComments);
        //------------------------------------------------------------------------------

        // Hacemos llamada a API para recoger los comentarios del evento
        cargarComentariosAPI();

        // A los elementos del layout les asignamos los los valores correspondientes del evento
        asignarDatos(tvTitulo, tvFecha, tvUbicacion, tvFechaLimite, linearRatear, linearRatingMedio, tvNumComentarios, linearFechaLim, tvComunidad, tvValoracionMedia, tvDescripcion);

        configurarBotonExpand(linearComentarios, recyclerComments, tvNoComments);
        configurarBotonComentar(recyclerComments, tvNoComments);
        configurarBotonMultifuncion();
    }

    private void asignarDatos(TextView tvTitulo, TextView tvFecha, TextView tvUbicacion, TextView tvFechaLimite, LinearLayout linearRatear, LinearLayout linearRatingMedio, TextView tvNumComentarios, LinearLayout linearFechaLim, TextView tvComunidad, TextView tvValoracionMedia, TextView tvDescripcion) {
        //titulo
        tvTitulo.setText(evento.getTitulo());

        //fecha
        tvFecha.setText(Utils.formateadorFechas(evento.getFecha()));

        //ubicacion
        tvUbicacion.setText(evento.getUbicacion());

        //fecha límite
        if(evento.getFecha_limite() != null) {
            tvFechaLimite.setText(Utils.formateadorFechas(evento.getFecha_limite()));
        } else {
            linearFechaLim.setVisibility(View.GONE);
        }

        //nombre comunidad
        tvComunidad.setText(evento.getComunidad().getNombre());

        // Si el evento aun no se ha celebrado, se esconde la valoración media
        if (evento.getFecha().after(Utils.capturarTimestampActual())) {
            linearRatear.setVisibility(View.GONE);
            linearRatingMedio.setVisibility(View.GONE);

        // Si ya se ha celebrado...
        } else {
            eventoPasado = true;

            comprobarValoracionMedia();
        }

        // Buscamos si el socio está dentro de los asistentes y si ha realizado ya la valoración
        Iterator iterator = evento.getAsistencia().iterator();
        while (iterator.hasNext() && !asistido) {
            Asistir a = (Asistir)iterator.next();
            if (a.getIdSocio() == MainActivity.socio.getId()) {
                asistido = true;
                asistir = a;

                comprobarValoracion(a);
            }
        }

        comprobarAsistido();

        //núm comentarios
        if(evento.getComentarios() != null)
            tvNumComentarios.setText(String.valueOf(evento.getComentarios().size()));
        else
            tvNumComentarios.setText("0");

        //descripción
        tvDescripcion.setText(evento.getDescripcion());
    }

    private void comprobarValoracionMedia() {
        if(evento.getValoracionMedia() == 0){
            linearRatingMedio.setVisibility(View.GONE);
        } else {
            linearRatingMedio.setVisibility(View.VISIBLE);
            tvValoracionMedia.setText(Utils.formatearFloat(evento.getValoracionMedia()));
        }
    }

    private void cargarComentariosAPI() {
        //comentarioService = Api.getApi().create(ComentarioService.class);
        Call<ArrayList<Comentario>> comentariosCall = comentarioService.getComentariosByEvento(evento.getId());

        DialogProgress dp = new DialogProgress(EventoDetalladoActivity.this);
        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando_comentarios));

        comentariosCall.clone().enqueue(new Callback<ArrayList<Comentario>>() {
            @Override
            public void onResponse(Call<ArrayList<Comentario>> call, Response<ArrayList<Comentario>> response) {
                switch(response.code()){
                    case 200:
                    case 400:
                        ArrayList<Comentario> comentarios;
                        comentarios = response.body();
                        evento.setComentarios(comentarios);
                        if(evento.getComentarios() != null)
                            tvNumComentarios.setText(String.valueOf(evento.getComentarios().size()));
                        else
                            tvNumComentarios.setText("0");
                        break;
                    default:
                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
                        break;
                }
                ad.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Comentario>> call, Throwable t) {
                CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
                ad.dismiss();
            }
        });
    }

    private void configurarBotonExpand(final LinearLayout linearComentarios, final RecyclerView recyclerComments, final TextView tvNoComments) {
        expand = findViewById(R.id.eve_det_ivExpand);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandido){
                    //tvNoComments.setVisibility(View.GONE);
                    //recyclerComments.setVisibility(View.GONE);
                    linearComentarios.setVisibility(View.GONE);
                    expand.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    expandido = false;
                }
                else{
                    linearComentarios.setVisibility(View.VISIBLE);
                    refrescarComentarios(tvNoComments, recyclerComments);

                    expand.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    expandido = true;
                }
            }
        });
    }

    private void configurarBotonComentar(final RecyclerView recyclerComments, final TextView tvNoComments) {
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventoDetalladoActivity.this);
                final View view = getLayoutInflater().inflate(R.layout.dialog_comment, null);
                builder.setView(view)
                        .setTitle(getResources().getString(R.string.nuevo_comentario))
                        .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.enviar), null);
                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText etComent = view.findViewById(R.id.dialog_coment_etComment);
                                CheckBox cbMostrarNombre = view.findViewById(R.id.dialog_coment_cbNombre);
                                String comment = etComent.getText().toString();
                                boolean mostrarNombre = cbMostrarNombre.isChecked();

                                int maxCaracteres = 140;
                                if (comment.length() > maxCaracteres || comment.length() < 5){
                                    if (comment.length() > maxCaracteres){
                                        CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),getResources().getString(R.string.error_comentario_excede));
                                    } else {
                                        CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),getResources().getString(R.string.error_comentario_demasiado_corto));
                                    }
                                }
                                else{
                                    short idEvento = evento.getId();
                                    int idSocio = MainActivity.socio.getId();
                                    int id = (evento.getComentarios().size() + 1);

                                    String body = comment;
                                    final Comentario c = new Comentario(idEvento, idSocio, id, mostrarNombre, Utils.capturarTimestampActual(), true, body);

                                    Call<Comentario> comentarioCall = comentarioService.insertComentario(c);
                                    comentarioCall.clone().enqueue(new Callback<Comentario>() {
                                        @Override
                                        public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                                            switch(response.code()){
                                                case 200:
                                                case 201:
                                                case 204:
                                                    CustomToast.mostrarSuccess(EventoDetalladoActivity.this, getLayoutInflater(), getResources().getString(R.string.comentario_anadido));
                                                    // Añadimos, al comentario recien creado, el socio para que pete al actualizar el recycler
                                                    c.setSocio(MainActivity.socio);
                                                    // Añadimos el comentario al evento
                                                    evento.getComentarios().add(c);
                                                    refrescarComentarios(tvNoComments, recyclerComments);
                                                    // Actualizamos, del header, el # de comentarios
                                                    tvNumComentarios.setText(String.valueOf(evento.getComentarios().size()));
                                                    break;
                                                default:
                                                    CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Comentario> call, Throwable t) {
                                            CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
                                        }
                                    });

                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        //----------------------------------------------------------------
    }

    private void configurarBotonMultifuncion() {
        btnMultifuncion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!eventoPasado) {
                    if(asistido){
                        DialogProgress dp = new DialogProgress(EventoDetalladoActivity.this);
                        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));

                        // Llamamos a la API para eliminar el asistir
                        Call<Asistir> asistirCall = asistirService.deleteAsistir(MainActivity.socio.getId(), evento.getId());
                        asistirCall.clone().enqueue(new Callback<Asistir>() {
                            @Override
                            public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                                switch(response.code()){
                                    case 200:
                                    case 201:
                                        //marcamos como no asistido y cambiamos colores y texto del boton
                                        asistido = false;
                                        comprobarAsistido();
                                        break;
                                    default:
                                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
                                        break;
                                }
                                ad.dismiss();
                            }

                            @Override
                            public void onFailure(Call<Asistir> call, Throwable t) {
                                ad.dismiss();
                                CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));

                            }
                        });

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EventoDetalladoActivity.this);
                        final View view = getLayoutInflater().inflate(R.layout.dialog_asistir, null);
                        builder.setView(view)
                                .setTitle(getResources().getString(R.string.confirmar_asistencia))
                                .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton(getResources().getString(R.string.confirmar), null);
                        AlertDialog dialog = builder.create();

                        //Rellenamos de valores el spinner
                        ArrayList<Short> spinnerArray = rellenarSpinner();
                        //Cogemos su id del layout
                        final Spinner spinner = view.findViewById(R.id.asistir_spinner);
                        //Creamos un adaptador
                        ArrayAdapter<Short> spinnerAdapter = new ArrayAdapter<>(EventoDetalladoActivity.this,
                                android.R.layout.simple_spinner_item, spinnerArray);
                        //Indicamos el tipo de DropDown
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Asignamos el adaptador
                        spinner.setAdapter(spinnerAdapter);

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(final DialogInterface dialog) {
                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(spinner.getSelectedItem() != null){
                                            int idSocio = MainActivity.socio.getId();
                                            short idEvento = evento.getId();
                                            short numAsistentes = (short) spinner.getSelectedItem();
                                            Asistir asistir = new Asistir(idSocio, idEvento, numAsistentes);

                                            Call<Asistir> asistirCall = asistirService.insertAsistir(asistir);
                                            asistirCall.clone().enqueue(new Callback<Asistir>() {
                                                @Override
                                                public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                                                    switch(response.code()){
                                                        case 200:
                                                        case 201:
                                                            //marcamos como asistido y cambiamos colores y texto del boton
                                                            asistido = true;
                                                            comprobarAsistido();
                                                            break;
                                                        default:
                                                            CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Asistir> call, Throwable t) {
                                                    CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                        dialog.show();
                    }
                // Si el evento ya se celebró ...
                } else {
                    // Si el socio no lo ha valorado aun ...
                    if (!valorado) {
                        // Si el ratingBar tiene algún valor ...
                        if (ratingBar.getRating() > 0 ){
                            // Mensaje del Dialog
                            String msj = getResources().getString(R.string.msj_enviar_valoracion1) + " " +
                                    (int)ratingBar.getRating() + "/" + ratingBar.getNumStars() + ".\n" +
                                    getResources().getString(R.string.msj_enviar_valoracion2);

                            // Creamos un Dialog para confirmar que el usuario quiere enviar esa valoracion
                            AlertDialog.Builder builder = new AlertDialog.Builder(EventoDetalladoActivity.this);
                            builder.setTitle(getResources().getString(R.string.confirmar_valoracion))
                                    .setMessage(msj)
                                    .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.si_enviar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Modificamos la valoración del evento
                                            asistir.setValoracion((byte) ratingBar.getRating());
                                            // Llamamos a la API para updatear el asistir
                                            Call<Asistir> asistirCall = asistirService.updateAsistir(MainActivity.socio.getId(), evento.getId(), asistir);
                                            asistirCall.enqueue(new Callback<Asistir>() {
                                                @Override
                                                public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                                                    switch(response.code()){
                                                        case 204:
                                                            valorado = true;
                                                            CustomToast.mostrarSuccess(EventoDetalladoActivity.this, getLayoutInflater(), getResources().getString(R.string.valoracion_enviada));
                                                            ratingBar.setEnabled(false);
                                                            comprobarValoracion(asistir);

                                                            modificarValoracionMedia();
                                                            break;
                                                        default:
                                                            CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Asistir> call, Throwable t) {
                                                    CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
                                                }
                                            });
                                        }
                                    })
                                    .create();
                            builder.show();
                        }
                    }
                }
            }
        });
    }

    private void modificarValoracionMedia() {
        int personas = 0;
        float valoracionTotal = 0;
        float result;
        for (Asistir as : evento.getAsistencia()){
            valoracionTotal += as.getValoracion();
            personas++;
        }
        result = valoracionTotal / personas;
        evento.setValoracionMedia(result);



        DialogProgress dp = new DialogProgress(EventoDetalladoActivity.this);
        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));

        // llamada a la api para actualizar evento
        EventoService eventoService = Api.getApi().create(EventoService.class);

        //FIXME
        //  Funciona, cumple su cometido pero se va por el onFailure
        //short id, String titulo, Timestamp fecha, String ubicacion, String descripcion, @Nullable Timestamp fecha_limite, byte idComunidad, float valoracionMedia
        Evento e = new Evento(evento.getId(), evento.getTitulo(), evento.getFecha(), evento.getUbicacion(), evento.getDescripcion(), evento.getFecha_limite(), evento.getIdComunidad(), evento.getIdAdmin(), evento.getValoracionMedia());
        Call<Evento> callEvento = eventoService.updateEvento(e.getId(), e);
        callEvento.enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                switch(response.code()){
                    case 200:
                        Toast.makeText(EventoDetalladoActivity.this, "Se actualizó la valoracion media del evento", Toast.LENGTH_SHORT).show();
                        comprobarValoracionMedia();
                        break;
                    case 404:
                        // not found
                        Toast.makeText(EventoDetalladoActivity.this, "Error: not found", Toast.LENGTH_SHORT).show();
                    default:
                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
                        break;
                }
                ad.dismiss();
            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                ad.dismiss();
                Toast.makeText(EventoDetalladoActivity.this, "Error: onFailure", Toast.LENGTH_SHORT).show();
            }
        });
//        callEvento.enqueue(new Callback<Evento>() {
//            @Override
//            public void onResponse(Call<Evento> call, Response<Evento> response) {
//                switch(response.code()){
//                    case 200:
//                        // ok, se actualizó el evento
//                        System.out.println("se actualizó el evento");
//                        comprobarValoracionMedia();
//                        break;
//                    case 404:
//                        // not found
//                        System.out.println("not found");
//                    default:
//                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
//                        break;
//                }
//                ad.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<Evento> call, Throwable t) {
//                System.out.println("error onFailure");
//                // no se pudo enviar la call y por tanto, no se actualizó la valoración media
//                //CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
//                ad.dismiss();
//            }
//        });
    }

    private void refrescarComentarios(TextView noComments, RecyclerView recycler) {
        if(evento.getComentarios().isEmpty() || evento.getComentarios() == null){
            noComments.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
        else{
            noComments.setVisibility(View.GONE);

            recycler.setLayoutManager(new GridLayoutManager(EventoDetalladoActivity.this,1));
            RecyclerCommentsAdapter recyclerAdapter = new RecyclerCommentsAdapter(evento.getComentarios(), EventoDetalladoActivity.this);
            recycler.setAdapter(recyclerAdapter);

            recycler.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Comprueba que el evento no se haya realizado y que el socio haya asistido o no al evento para así poder cambiar el estilo
     * al botón y también poder habilitarle o no el botón para realizar comentarios
     */
    private void comprobarAsistido() {
        // Si el evento no se ha celebrado
        if (!eventoPasado) {
            if(asistido){
                btnMultifuncion.setText(getResources().getString(R.string.no_asistir));
                btnMultifuncion.setBackground(this.getResources().getDrawable(R.drawable.selector_custom_button_red));
            }
            else{
                btnMultifuncion.setText(getResources().getString(R.string.asistire));
                btnMultifuncion.setBackground(this.getResources().getDrawable(R.drawable.selector_custom_button_green));
            }
        // Si ya se celebró
        } else {
            btnMultifuncion.setText(getResources().getString(R.string.valorar));
            btnMultifuncion.setBackground(this.getResources().getDrawable(R.drawable.custom_button_ratear));
        }

        // Si ha asistido podrá comentar, de lo contrario no
        if (asistido) {
            btnComentar.setVisibility(View.VISIBLE);
        } else {
            btnComentar.setVisibility(View.GONE);
        }
    }

    private void comprobarValoracion(Asistir a) {
        // Si es mayor que 0 significa que ya realizó una valoración
        if (a.getValoracion() > 0) {
            valorado = true;
            // Inhabilitamos el ratingBar y marcamos la valoración que el socio envió
            ratingBar.setRating(a.getValoracion());
            ratingBar.setEnabled(false);
            // Cambiamos el el textView para mayor comodidad del usuario
            TextView tv = findViewById(R.id.eve_det_tvValoraElEvento);
            tv.setText(R.string.gracias_por_valorar);
        } else {
            ratingBar.setRating(0);
            ratingBar.setEnabled(true);
        }
    }

    private ArrayList<Short> rellenarSpinner() {
        ArrayList<Short> list = new ArrayList<Short>();
        for(int i=1; i<11; i++){
            list.add((short) i);
        }

        return list;
    }
}
