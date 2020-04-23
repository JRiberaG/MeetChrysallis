package com.example.meetchrysallis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.AsistirService;
import com.example.meetchrysallis.API.ApiService.ComentarioService;
import com.example.meetchrysallis.API.ApiService.EventoService;
import com.example.meetchrysallis.API.ApiService.TimeService;
import com.example.meetchrysallis.API.ApiWorldTime;
import com.example.meetchrysallis.Adapters.RecyclerCommentsAdapter;
import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Comentario;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Models.Time;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoDetalladoActivity extends AppCompatActivity {

    private Context ctx = EventoDetalladoActivity.this;
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
        ImageView ivBandera        = findViewById(R.id.eve_det_ivImagen);

        TextView tvTitulo           = findViewById(R.id.eve_det_tvNombre);
        TextView tvFecha            = findViewById(R.id.eve_det_tvFecha);
        TextView tvUbicacion        = findViewById(R.id.eve_det_tvUbicacion);
        TextView tvFechaLimite      = findViewById(R.id.eve_det_tvFechaLimite);
        TextView tvComunidad        = findViewById(R.id.eve_det_tvComunidad);
        tvValoracionMedia           = findViewById(R.id.eve_det_tvStars);
        tvNumComentarios            = findViewById(R.id.eve_det_tvNumComments);
        TextView tvDescripcion      = findViewById(R.id.eve_det_tvDescripcion);
        TextView tvNoComments       = findViewById(R.id.eve_det_tvNoComments);

        LinearLayout linearFecha            = findViewById(R.id.eve_det_linear_fecha);
        LinearLayout linearUbicacion        = findViewById(R.id.eve_det_linear_ubicacion);
        LinearLayout linearFechaLim         = findViewById(R.id.eve_det_linear_fechaLim);
        LinearLayout linearComunidad        = findViewById(R.id.eve_det_linear_comunidad);
        linearRatingMedio                   = findViewById(R.id.eve_det_linear_ratingMedio);
        LinearLayout linearNumComentarios   = findViewById(R.id.eve_det_linear_numComentarios);
        LinearLayout linearHeadComments     = findViewById(R.id.eve_det_linear_header_comentarios);
        LinearLayout linearComentarios      = findViewById(R.id.eve_det_linearLayoutComentarios);
        LinearLayout linearRatear           = findViewById(R.id.eve_det_linearLayout_rating);

        btnComentar     = findViewById(R.id.eve_det_btnComentar);
        btnMultifuncion = findViewById(R.id.eve_det_btnAsistire);
        ImageButton ibBack  = findViewById(R.id.eve_det_btnBack);

        ratingBar = findViewById(R.id.eve_det_ratingBar);

        RecyclerView recyclerComments = findViewById(R.id.eve_det_recyclerComments);
        //------------------------------------------------------------------------------

        // Hacemos llamada a API para recoger los comentarios del evento
        cargarComentariosAPI();

        // A los elementos del layout les asignamos los los valores correspondientes del evento
        asignarDatos(ivBandera, tvTitulo, tvFecha, tvUbicacion, tvFechaLimite, linearRatear, linearRatingMedio, tvNumComentarios, linearFechaLim, tvComunidad, tvValoracionMedia, tvDescripcion, linearUbicacion, ibBack);

        configurarListenersLayouts(linearFecha, linearFechaLim, linearComunidad, linearNumComentarios);

        configurarLayoutExpand(linearComentarios, recyclerComments, tvNoComments, linearHeadComments);
        configurarBotonComentar(recyclerComments, tvNoComments);
        configurarBotonMultifuncion();
    }

    private void configurarListenersLayouts(LinearLayout linearFecha, LinearLayout linearFechaLim, LinearLayout linearComunidad, LinearLayout linearNumComentarios) {
        linearFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.info_fecha), false);
            }
        });

        linearFechaLim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.info_fechaLim), false);
            }
        });

        linearComunidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.info_comunidad), false);
            }
        });

        linearRatingMedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.info_ratingMedio), false);
            }
        });

        linearNumComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.mostrarInfo(ctx, getLayoutInflater(), getResources().getString(R.string.info_numComents), false);
            }
        });
    }

    private void asignarDatos(ImageView ivBandera, TextView tvTitulo, TextView tvFecha, TextView tvUbicacion, TextView tvFechaLimite, LinearLayout linearRatear, LinearLayout linearRatingMedio, TextView tvNumComentarios, LinearLayout linearFechaLim, TextView tvComunidad, TextView tvValoracionMedia, TextView tvDescripcion, LinearLayout linearUbicacion, ImageButton ibBack) {
        configurarIbBack(ibBack);

//        asignarBandera(imgBandera);
        Utils.asignarImagenComunidad(ctx, ivBandera, evento.getIdComunidad());

        //titulo
        tvTitulo.setText(evento.getTitulo());

        //fecha
        tvFecha.setText(Utils.formateadorFechas(evento.getFecha()));

        //ubicacion
        tvUbicacion.setText(evento.getUbicacion());
        configurarIntentMaps(tvUbicacion, linearUbicacion);

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

//    private void asignarBandera(ImageView imgBandera) {
//        switch(evento.getIdComunidad()){
//            case 1:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_andalucia_rounded));
//                break;
//            case 2:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_aragon_rounded));
//                break;
//            case 3:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_islas_canarias_rounded));
//                break;
//            case 4:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_cantabria_rounded));
//                break;
//            case 5:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_castilla_leon_rounded));
//                break;
//            case 6:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_castilla_mancha_rounded));
//                break;
//            case 7:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_catalunya_rounded));
//                break;
//            case 8:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_ceuta_rounded));
//                break;
//            case 9:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_melilla_rounded));
//                break;
//            case 10:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_comunidad_madrid_rounded));
//                break;
//            case 11:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_navarra_rounded));
//                break;
//            case 12:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_comunitat_valenciana_rounded));
//                break;
//            case 13:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_extremadura_rounded));
//                break;
//            case 14:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_galicia_rounded));
//                break;
//            case 15:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_islas_baleares_rounded));
//                break;
//            case 16:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_la_rioja_rounded));
//                break;
//            case 17:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_pais_vasco_rounded));
//                break;
//            case 18:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_asturias_rounded));
//                break;
//            case 19:
//                imgBandera.setImageDrawable(getResources().getDrawable(R.drawable.bandera_region_murcia_rounded));
//                break;
//        }
//    }

    private void configurarIbBack(ImageButton ibBack) {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configurarIntentMaps(TextView tvUbicacion, LinearLayout linearUbicacion) {
        tvUbicacion.setText(Html.fromHtml("<u>" + evento.getUbicacion() + "</u>"));
        tvUbicacion.setTextColor(getResources().getColor(R.color.hyperlink));

        linearUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String map = "http://maps.google.com/maps?q=" + evento.getUbicacion().replace(" ", "+");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(map));
                startActivity(intent);
            }
        });
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
                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message(), true);
                        break;
                }
                ad.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Comentario>> call, Throwable t) {
                CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db), true);
                ad.dismiss();
            }
        });
    }

    private void configurarLayoutExpand(final LinearLayout linearComentarios, final RecyclerView recyclerComments, final TextView tvNoComments, final LinearLayout linearHeadComments) {
        expand = findViewById(R.id.eve_det_ivExpand);
        linearHeadComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandido){
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

//        expand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(expandido){
//                    //tvNoComments.setVisibility(View.GONE);
//                    //recyclerComments.setVisibility(View.GONE);
//                    linearComentarios.setVisibility(View.GONE);
//                    expand.setImageResource(R.drawable.ic_expand_more_black_24dp);
//                    expandido = false;
//                }
//                else{
//                    linearComentarios.setVisibility(View.VISIBLE);
//                    refrescarComentarios(tvNoComments, recyclerComments);
//
//                    expand.setImageResource(R.drawable.ic_expand_less_black_24dp);
//                    expandido = true;
//                }
//            }
//        });
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
                                final boolean mostrarNombre = cbMostrarNombre.isChecked();

                                int maxCaracteres = 140;
                                if (comment.length() > maxCaracteres || comment.length() < 5){
                                    if (comment.length() > maxCaracteres){
                                        CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),getResources().getString(R.string.error_comentario_excede), true);
                                    } else {
                                        CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),getResources().getString(R.string.error_comentario_demasiado_corto), true);
                                    }
                                }
                                else{
                                    final short idEvento = evento.getId();
                                    final int idSocio = MainActivity.socio.getId();
                                    final int id = (evento.getComentarios().size() + 1);

                                    final String body = comment;

                                    // Llamamos a la API WorldTimeAPI para recoger el tiempo real
                                    TimeService timeService = ApiWorldTime.getApi().create(TimeService.class);
                                    Call<Time> timeCall = timeService.getTime();
                                    timeCall.enqueue(new Callback<Time>() {
                                        @Override
                                        public void onResponse(Call<Time> call, Response<Time> response) {
                                            if (response.code() == 200) {
                                                Time tiempo = response.body();

                                                // Se recogió bien el DateTime y se envía por parámetro
                                                if (tiempo != null) {
                                                    enviarComentario(tiempo.getDatetime(), idEvento, idSocio, id, mostrarNombre, body, tvNoComments, recyclerComments);
                                                }
                                                // Hubo un error y el comentario tendrá el DateTIme capturado del móvil
                                                else {
                                                    enviarComentario(Utils.capturarTimestampActual(), idEvento, idSocio, id, mostrarNombre, body, tvNoComments, recyclerComments);
                                                }


                                            } else {
                                                System.err.println(response.code() + " - " + response.message());
                                                // Hubo un error y el comentario tendrá el DateTIme capturado del móvil
                                                enviarComentario(Utils.capturarTimestampActual(), idEvento, idSocio, id, mostrarNombre, body, tvNoComments, recyclerComments);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Time> call, Throwable t) {
                                            // Hubo un error y el comentario tendrá el DateTIme capturado del móvil
                                            System.err.println(t.toString());
                                            enviarComentario(Utils.capturarTimestampActual(), idEvento, idSocio, id, mostrarNombre, body, tvNoComments, recyclerComments);
                                        }
                                    });



//                                    final Comentario c = new Comentario(idEvento, idSocio, id, mostrarNombre, Utils.capturarTimestampActual(), true, body);
//
//                                    Call<Comentario> comentarioCall = comentarioService.insertComentario(c);
//                                    comentarioCall.clone().enqueue(new Callback<Comentario>() {
//                                        @Override
//                                        public void onResponse(Call<Comentario> call, Response<Comentario> response) {
//                                            switch(response.code()){
//                                                case 200:
//                                                case 201:
//                                                case 204:
//                                                    CustomToast.mostrarSuccess(EventoDetalladoActivity.this, getLayoutInflater(), getResources().getString(R.string.comentario_anadido));
//                                                    // Añadimos, al comentario recien creado, el socio para que pete al actualizar el recycler
//                                                    c.setSocio(MainActivity.socio);
//                                                    // Añadimos el comentario al evento
//                                                    evento.getComentarios().add(c);
//                                                    refrescarComentarios(tvNoComments, recyclerComments);
//                                                    // Actualizamos, del header, el # de comentarios
//                                                    tvNumComentarios.setText(String.valueOf(evento.getComentarios().size()));
//                                                    break;
//                                                default:
//                                                    CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
//                                                    break;
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<Comentario> call, Throwable t) {
//                                            CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
//                                        }
//                                    });

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

    private void enviarComentario(Timestamp datetime, short idEvento, int idSocio, int id, boolean mostrarNombre, String body, final TextView tvNoComments, final RecyclerView recyclerComments){
        final Comentario c = new Comentario(idEvento, idSocio, id, mostrarNombre, datetime, true, body);

        Call<Comentario> comentarioCall = comentarioService.insertComentario(c);
        comentarioCall.clone().enqueue(new Callback<Comentario>() {
            @Override
            public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                switch(response.code()){
                    case 200:
                    case 201:
                    case 204:
                        CustomToast.mostrarSuccess(EventoDetalladoActivity.this, getLayoutInflater(), getResources().getString(R.string.comentario_anadido), false);
                        // Añadimos, al comentario recien creado, el socio para que pete al actualizar el recycler
                        c.setSocio(MainActivity.socio);
                        // Añadimos el comentario al evento
                        evento.getComentarios().add(c);
                        refrescarComentarios(tvNoComments, recyclerComments);
                        // Actualizamos, del header, el # de comentarios
                        tvNumComentarios.setText(String.valueOf(evento.getComentarios().size()));
                        break;
                    default:
                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message(), true);
                        break;
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db), true);
            }
        });
    }

    private void configurarBotonMultifuncion() {
        btnMultifuncion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!eventoPasado) {
                    if(asistido){
                        mostrarDialogConfirmacion();

//                        llamadaApiDesapuntarse();
//                        DialogProgress dp = new DialogProgress(EventoDetalladoActivity.this);
//                        final AlertDialog ad = dp.setProgressDialog(getResources().getString(R.string.cargando));
//
//                        // Llamamos a la API para eliminar el asistir
//                        Call<Asistir> asistirCall = asistirService.deleteAsistir(MainActivity.socio.getId(), evento.getId());
//                        asistirCall.clone().enqueue(new Callback<Asistir>() {
//                            @Override
//                            public void onResponse(Call<Asistir> call, Response<Asistir> response) {
//                                switch(response.code()){
//                                    case 200:
//                                    case 201:
//                                        mostrarDialogConfirmacion();
//                                        //marcamos como no asistido y cambiamos colores y texto del boton
////                                        asistido = false;
////                                        comprobarAsistido();
//                                        break;
//                                    default:
//                                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message());
//                                        break;
//                                }
//                                ad.dismiss();
//                            }
//
//                            @Override
//                            public void onFailure(Call<Asistir> call, Throwable t) {
//                                ad.dismiss();
//                                CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db));
//                            }
//                        });

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
                                                            CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message(), true);
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Asistir> call, Throwable t) {
                                                    CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db), true);
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
                                                            CustomToast.mostrarSuccess(EventoDetalladoActivity.this, getLayoutInflater(), getResources().getString(R.string.valoracion_enviada), false);
                                                            ratingBar.setEnabled(false);
                                                            comprobarValoracion(asistir);

                                                            modificarValoracionMedia();
                                                            break;
                                                        default:
                                                            CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message(), true);
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Asistir> call, Throwable t) {
                                                    CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db), true);
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

    private void llamadaApiDesapuntarse() {
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

//                        mostrarDialogConfirmacion();
                        //marcamos como no asistido y cambiamos colores y texto del boton
                                        asistido = false;
                                        comprobarAsistido();
                        break;
                    default:
                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message(), true);
                        break;
                }
                ad.dismiss();
            }

            @Override
            public void onFailure(Call<Asistir> call, Throwable t) {
                ad.dismiss();
                CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), getString(R.string.error_conexion_db), true);
            }
        });
    }

    private void mostrarDialogConfirmacion() {
//        final boolean[] res = {true};
        AlertDialog.Builder builder = new AlertDialog.Builder(EventoDetalladoActivity.this);
        builder.setTitle(getResources().getString(R.string.desea_desapuntarse_evento))
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.si_desapuntarse), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        res[0] = true;
                        llamadaApiDesapuntarse();
//                        asistido = false;
//                        comprobarAsistido();
                    }
                })
                .create()
                .show();

//        return res[0];
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
        Evento e = new Evento(evento.getId(), evento.getTitulo(), evento.getFecha(), evento.getUbicacion(), evento.getDescripcion(), evento.getFecha_limite(), evento.getIdComunidad(), evento.getIdAdmin(), evento.getValoracionMedia());
        Call<Evento> callEvento = eventoService.updateEvento(e.getId(), e);
        callEvento.enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                switch(response.code()){
                    case 200:
                        System.out.println("Se actualizó la valoracion media del evento");
                        break;
                    case 404:
                        // not found
                        System.out.println("Error: not found");
                    default:
                        CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), response.code() + " - " + response.message(), true);
                        break;
                }
                comprobarValoracionMedia();
                ad.dismiss();
            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                ad.dismiss();
                comprobarValoracionMedia();
                System.err.println("Error: onFailure: " + t.getMessage());
            }
        });
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
