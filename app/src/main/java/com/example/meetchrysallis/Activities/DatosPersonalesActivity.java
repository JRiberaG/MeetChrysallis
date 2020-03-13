package com.example.meetchrysallis.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.R;

import java.io.File;

public class DatosPersonalesActivity extends AppCompatActivity {

    int posicionIdioma = 0; //por defecto será 0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);

        TextView tvCorreo           = findViewById(R.id.datos_personales_tvCorreo);
        TextView tvTelefono         = findViewById(R.id.datos_personales_tvTelefono);
        final TextView tvIdioma     = findViewById(R.id.datos_personales_tvIdioma);
        LinearLayout llEmail        = findViewById(R.id.datos_personales_layout_email);
        LinearLayout llTelefono     = findViewById(R.id.datos_personales_layout_telefono);
        LinearLayout llContrasenya  = findViewById(R.id.datos_personales_layout_contrasenya);
        LinearLayout llIdioma       = findViewById(R.id.datos_personales_layout_idioma);

        tvCorreo.setText(MainActivity.socio.getEmail());
        tvTelefono.setText(MainActivity.socio.getTelefono());
        tvIdioma.setText(MainActivity.idioma);
        asignarPosicionIdioma();

        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llContrasenya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] idiomas = getResources().getStringArray(R.array.idiomas);

                final AlertDialog.Builder builder = new AlertDialog.Builder(DatosPersonalesActivity.this);
                builder.setTitle(getResources().getString(R.string.seleccione_idioma))
                        .setSingleChoiceItems(idiomas, posicionIdioma, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                posicionIdioma = i;
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                asignarIdioma(tvIdioma);
                            }
                        });
                builder.create();
                builder.show();
            }
        });

//        RecyclerView recyclerComunidades = findViewById(R.id.datos_personales_recycler);
//        recyclerComunidades.setLayoutManager(new LinearLayoutManager(DatosPersonalesActivity.this));
//
//        RecyclerComunidadesInteres adapterRecycler = new RecyclerComunidadesInteres(
//                                        MainActivity.socio.getComunidadesInteres(),
//                                        DatosPersonalesActivity.this);
//        recyclerComunidades.setAdapter(adapterRecycler);
    }

    private void asignarIdioma(TextView tvIdioma) {
        //guardar en json el idioma seleccionado
        File fileConfig = new File(getExternalFilesDir(null).getPath() + File.separator + "config.cfg");
        String idiomaAbr, idioma;
        switch(posicionIdioma){
            case 0:
                idiomaAbr = "cast";
                idioma = getResources().getString(R.string.castellano);
                break;
            case 1:
                idiomaAbr = "cat";
                idioma = getResources().getString(R.string.catalan);
                break;
            default:
                idiomaAbr = "eng";
                idioma = getResources().getString(R.string.ingles);
                break;
        }
        MainActivity.idioma = idioma;
        tvIdioma.setText(MainActivity.idioma);
        //Actualizamos el fichero config
        Archivador.guardarConfig(fileConfig, idiomaAbr);
    }

    private void asignarPosicionIdioma() {
        if (MainActivity.idioma.equals(getResources().getString(R.string.castellano))){
            posicionIdioma = 0;
        }
        else if (MainActivity.idioma.equals(getResources().getString(R.string.catalan))){
            posicionIdioma = 1;
        }
        else{
            posicionIdioma = 2;
        }
    }

}
