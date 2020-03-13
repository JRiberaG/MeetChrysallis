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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetchrysallis.API.Api;
import com.example.meetchrysallis.API.ApiService.AsistirService;
import com.example.meetchrysallis.API.ApiService.ComentarioService;
import com.example.meetchrysallis.Adapters.RecyclerCommentsAdapter;
import com.example.meetchrysallis.Models.Asistir;
import com.example.meetchrysallis.Models.Comentario;
import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.Others.DialogProgress;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventoDetalladoActivity extends AppCompatActivity {

    private Button btnAsistir;
    private Button btnComentar;
    private ImageView expand;

    private boolean asistido = false;
    private boolean expandido = false;

    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detallado);

        evento = (Evento)getIntent().getExtras().getSerializable("evento");

        // --------------- ELEMENTOS DEL LAYOUT ---------------
        TextView tvTitulo = findViewById(R.id.eve_det_tvNombre);
        TextView tvFecha = findViewById(R.id.eve_det_tvFecha);
        TextView tvUbicacion = findViewById(R.id.eve_det_tvUbicacion);
        TextView tvFechaLimite = findViewById(R.id.eve_det_tvFechaLimite);
        TextView tvComunidad = findViewById(R.id.eve_det_tvComunidad);
        TextView tvValoracionMedia = findViewById(R.id.eve_det_tvStars);
        final TextView tvNumComentarios = findViewById(R.id.eve_det_tvNumComments);
        TextView tvDescripcion = findViewById(R.id.eve_det_tvDescripcion);

        LinearLayout linearFechaLim = findViewById(R.id.eve_det_linearlayout_fechaLim);
        LinearLayout linearRatingMedio = findViewById(R.id.eve_det_linearLayout_ratingMedio);
        LinearLayout linearRatear = findViewById(R.id.eve_det_linearLayout_rating);

        btnComentar = findViewById(R.id.eve_det_btnComentar);
        btnAsistir = findViewById(R.id.eve_det_btnAsistire);
        //------------------------------------------------------------------------------

        //Cargamos los comentarios del evento ----------------------
        final ComentarioService comentarioService = Api.getApi().create(ComentarioService.class);
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
        //------------------------------------------------------------------


        //titulo
        tvTitulo.setText(evento.getTitulo());
        //fecha
        tvFecha.setText(Utils.formateadorFechas(evento.getFecha()));
        //ubicacion
        tvUbicacion.setText(evento.getUbicacion());
        //fecha límite
        if(evento.getFecha_limite() != null)
            tvFechaLimite.setText(Utils.formateadorFechas(evento.getFecha_limite()));
        else
            linearFechaLim.setVisibility(View.GONE);
        //nombre comunidad
        tvComunidad.setText(evento.getComunidad().getNombre());
        //TODO: si el evento no se ha realizado, esconder la valoración media (??)
        //      o no porque si no se ha realizado no es posible comentar
        //valoracion media
        if(evento.getValoracionMedia() == 0)
            linearRatingMedio.setVisibility(View.GONE);
        else
            tvValoracionMedia.setText(Utils.formatearFloat(evento.getValoracionMedia()));
        //núm comentarios
        if(evento.getComentarios() != null)
            tvNumComentarios.setText(String.valueOf(evento.getComentarios().size()));
        else
            tvNumComentarios.setText("0");
        //descripción
        tvDescripcion.setText(evento.getDescripcion());

        //TODO:
        //  - si el evento ya ha finalizado activar el lineaLayoutRatingEstrellas
        //  - comprobar que el socio no no se haya inscrito ya.
        //      En caso de haberlo hecho, cambiar el botón a NO ASISTIR

        //FIXME: descomentar y solucionar
//        comprobarAsistido(evento.getAsistir().contains(MainActivity.socio));

        // ---------------------- BOTÓN ASISTIR ----------------------
        btnAsistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(asistido){
                    //TODO:
                    //  Eliminar de la base de datos la asistencia del socio
                    asistido = false;
                    comprobarAsistido(asistido);
                }
                else{
                    //TODO:
                    //  Mostrar alert y añadir a la base de datos

                    AlertDialog.Builder builder = new AlertDialog.Builder(EventoDetalladoActivity.this);
                    final View view = getLayoutInflater().inflate(R.layout.dialog_asistir, null);
                    builder.setView(view)
                            .setTitle("Confirmar asistencia")
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Confirmar", null);
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
                                        Socio socio = MainActivity.socio;
                                        Asistir asistir = new Asistir(idSocio, idEvento, numAsistentes, socio, evento);

                                        AsistirService asistirService = Api.getApi().create(AsistirService.class);
                                        Call<Asistir> asistirCall = asistirService.insertAsistir(asistir);
                                        asistirCall.clone().enqueue(new Callback<Asistir>() {
                                            @Override
                                            public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                                                switch(response.code()){
                                                    case 200:
                                                        CustomToast.mostrarSuccess(EventoDetalladoActivity.this, getLayoutInflater(), "OK");
                                                        //marcamos como asistido y cambiamos colores y texto del boton
                                                        asistido = true;
                                                        comprobarAsistido(asistido);
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
            }
        });
        // ----------------------------------------------------

        // ---------------------- BOTÓN EXPAND (COMENTARIOS) ----------------------
        expand = findViewById(R.id.eve_det_ivExpand);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tvNoComments = findViewById(R.id.eve_det_tvNoComments);
                final RecyclerView recyclerComments = findViewById(R.id.eve_det_recyclerComments);
                if(expandido){
                    tvNoComments.setVisibility(View.GONE);
                    recyclerComments.setVisibility(View.GONE);
                    expand.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    expandido = false;
                }
                else{
                    cargarComentarios(tvNoComments, recyclerComments);

                    expand.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    expandido = true;
                }
            }
        });
        //--------------------------------------------------------------------------


        // ---------------------- BOTÓN COMENTAR -------------------------
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventoDetalladoActivity.this);
                final View view = getLayoutInflater().inflate(R.layout.dialog_comment, null);
                builder.setView(view)
                        .setTitle("Nuevo comentario")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Enviar", null);
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
                                if (comment.length() > maxCaracteres){
                                    CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),"El  comentario excede el número máximo de carácteres permitidos");
                                }
                                else{
                                    //TODO: añadir el comentario
                                    //short idEvento, int idSocio, int id, boolean mostrarNombre, Timestamp fecha, boolean activo, Socio socio, Evento evento, String body
                                    short idEvento = evento.getId();
                                    int idSocio = MainActivity.socio.getId();
                                    int id = (evento.getComentarios().size() + 1);
                                    Date date = new Date();
                                    long time = date.getTime();
                                    Timestamp timestamp = new Timestamp(time);
                                    boolean activo = true;
                                    Socio socio = MainActivity.socio;
                                    String body = comment;
                                    Comentario c = new Comentario(idEvento, idSocio, id, mostrarNombre, timestamp, activo, socio, evento, body);
                                    //TODO: 409 CONFLICT. Ya se ha probado:
                                    //      - Crear otro objeto ComentarioService en lugar del que ya había
                                    //      - Cambiar el ID del comentario a eventos.size() + 1
                                    Call<Comentario> comentarioCall = comentarioService.insertComentario(c);
                                    comentarioCall.clone().enqueue(new Callback<Comentario>() {
                                        @Override
                                        public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                                            switch(response.code()){
                                                case 200:
                                                case 204:
                                                    //comentario añadido
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
        //Si el evento no se ha realizado, esconder el layout de ratingMedio
        //linearRatingMedio.setVisibility(View.GONE);
        //Si el evento no se ha realizado y el usuario no ha participado, escoonder el layout
        //linearRating.setVisibility(View.GONE);
    }

    private void cargarComentarios(TextView noComments, RecyclerView recycler) {
        if(evento.getComentarios().isEmpty() || evento.getComentarios() == null){
            noComments.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
        else{
            noComments.setVisibility(View.GONE);

            recycler.setLayoutManager(new GridLayoutManager(EventoDetalladoActivity.this,1));
            RecyclerCommentsAdapter recyclerAdapter = new RecyclerCommentsAdapter(evento.getComentarios());
            recycler.setAdapter(recyclerAdapter);

            recycler.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Comprueba que el socio haya asistido o no al evento para así poder cambiar el estilo
     * al botón y también poder habilitarle o no el botón para realizar comentarios
     * @param asist      true = ha asistido, false = no ha asistido
     */
    private void comprobarAsistido(boolean asist) {
        if(asist){
            btnAsistir.setText("NO ASISTIR");
            btnAsistir.setBackground(this.getResources().getDrawable(R.drawable.selector_custom_button_red));
            btnComentar.setVisibility(View.VISIBLE);
        }
        else{
            btnAsistir.setText("ASISTIRÉ");
            btnAsistir.setBackground(this.getResources().getDrawable(R.drawable.selector_custom_button_green));
            btnComentar.setVisibility(View.GONE);
        }
    }

    private ArrayList<Short> rellenarSpinner() {
        ArrayList<Short> list = new ArrayList<Short>();
        for(int i=1; i<11; i++){
            list.add(new Short((short) i));
        }

        return list;
    }
}
