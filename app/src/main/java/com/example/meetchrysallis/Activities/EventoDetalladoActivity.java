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

import com.example.meetchrysallis.Others.CustomToast;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

public class EventoDetalladoActivity extends AppCompatActivity {

    private Button asistir;
    private boolean asistido = false;
    private boolean expandido = false;
    private ImageView expand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_detallado);

        //comprobar que el socio no haya marcado su asistencia a ese evento ya
        //algo así como:
        //      asistido = comprobarAsistencia(MainActivity.socio);
        asistir = findViewById(R.id.eve_det_btnAsistire);
        comprobarAsistido(asistido);

        asistir.setOnClickListener(new View.OnClickListener() {
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
        Button btnComentar = findViewById(R.id.eve_det_btnComentar);
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

                                //TODO:
                                //  - Indicar el número máx de carácteres
                                int maxCaracteres = 5;
                                if (comment.length() > maxCaracteres){
                                    CustomToast.mostrarWarning(EventoDetalladoActivity.this,getLayoutInflater(),"El  comentario excede el número máximo de carácteres permitidos");
                                }
                                else{
                                    //TODO:
                                    // - Si mostrarNombre = true que cuando se vaya a añadir el comentario
                                    //  se muestre el nombre del socio
                                    //  Si es false, que el autor salga como 'anónimo'
                                    if (mostrarNombre){

                                    }
                                    else{

                                    }
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
     * al botón
     * @param asistido      true = ha asistido, false = no ha asistido
     */
    private void comprobarAsistido(boolean asistido) {
        if(asistido){
            asistir.setText("NO ASISTIR");
            asistir.setBackground(this.getResources().getDrawable(R.drawable.selector_custom_button_red));
        }
        else{
            asistir.setText("ASISTIRÉ");
            asistir.setBackground(this.getResources().getDrawable(R.drawable.selector_custom_button_green));
        }
    }
}
