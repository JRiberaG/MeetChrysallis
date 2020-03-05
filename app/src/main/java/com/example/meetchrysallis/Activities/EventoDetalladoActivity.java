package com.example.meetchrysallis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

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
        TextView tvNumComentarios = findViewById(R.id.eve_det_tvNumComments);
        TextView tvDescripcion = findViewById(R.id.eve_det_tvDescripcion);

        LinearLayout ratingMedio = findViewById(R.id.eve_det_linearLayout_ratingMedio);
        LinearLayout ratingEstrellas = findViewById(R.id.eve_det_linearLayout_rating);

        btnComentar = findViewById(R.id.eve_det_btnComentar);
        btnAsistir = findViewById(R.id.eve_det_btnAsistire);
        //------------------------------------------------------------------------------

        tvTitulo.setText(evento.getTitulo());
        //FIXME: parsear la fecha --> tvFecha.setText(evento.getFecha());
        tvUbicacion.setText(evento.getUbicacion());
        if(evento.getFecha_limite() != null)
            //FIXME: parsear la fecha --> tvFechaLimite.setText(evento.getFecha_limite());
            tvComunidad.setText(evento.getComunidad().getNombre());
        if(evento.getValoracionMedia() == 0)
            ratingMedio.setVisibility(View.GONE);
        else
            //FIXME: formatear este float
            tvValoracionMedia.setText(String.valueOf(evento.getValoracionMedia()));
        tvNumComentarios.setText(evento.getComentario().size());
        tvDescripcion.setText(evento.getDescripcion());

        //TODO:
        //  - si el evento ya ha finalizado activar el lineaLayoutRatingEstrellas
        //  - comprobar que el socio no no se haya inscrito ya.
        //      En caso de haberlo hecho, cambiar el botón a NO ASISTIR

        comprobarAsistido(evento.getAsistir().contains(MainActivity.socio));

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
                    //  Añadir a la base de datos la asistencia del socio
                    asistido = true;
                    comprobarAsistido(asistido);
                }
            }
        });

        // ---------------------- BOTÓN EXPAND (COMENTARIOS) ----------------------
        expand = findViewById(R.id.eve_det_ivExpand);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvNoComments = findViewById(R.id.eve_det_tvNoComments);
                ListView lvComments = findViewById(R.id.eve_det_lvComments);
                if(expandido){
                    tvNoComments.setVisibility(View.GONE);
                    CustomToast.mostrarWarning(EventoDetalladoActivity.this, getLayoutInflater(), "Se han escondido los comentarios");
                    expand.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    expandido = false;
                }
                else{
                    //TODO:
                    //  - Programar el recuperar los comentarios de la base de datos
                    //  - Programar el ArrayAdapter para mostrarlos en una ListView
                    ArrayList<String> comentarios = new ArrayList<>();
                    if(comentarios.isEmpty()){
                        tvNoComments.setVisibility(View.VISIBLE);
                        lvComments.setVisibility(View.GONE);
                    }
                    else{
                        tvNoComments.setVisibility(View.GONE);
                        lvComments.setVisibility(View.VISIBLE);
                    }
                    CustomToast.mostrarInfo(EventoDetalladoActivity.this, getLayoutInflater(), "Se han mostrado los comentarios");
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

                                //FIXME: indicar el número máx de carácteres
                                int maxCaracteres = 5;
                                if (comment.length() > maxCaracteres){
                                    CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),"El  comentario excede el número máximo de carácteres permitidos");
                                }
                                else{
                                    //TODO: añadir el comentario
                                    dialog.dismiss();
                                    CustomToast.mostrarInfo(EventoDetalladoActivity.this,getLayoutInflater(),"Comentario añadido");
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        //----------------------------------------------------------------

        LinearLayout linearRatingMedio = findViewById(R.id.eve_det_linearLayout_ratingMedio);
        LinearLayout linearRating = findViewById(R.id.eve_det_linearLayout_rating);
        //Si el evento no se ha realizado, esconder el layout de ratingMedio
        //linearRatingMedio.setVisibility(View.GONE);
        //Si el evento no se ha realizado y el usuario no ha participado, escoonder el layout
        //linearRating.setVisibility(View.GONE);
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
}
