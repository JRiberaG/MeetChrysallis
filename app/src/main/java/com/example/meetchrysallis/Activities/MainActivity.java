package com.example.meetchrysallis.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.meetchrysallis.Fragments.HomeFragment;
import com.example.meetchrysallis.Fragments.MisEventosFragment;
import com.example.meetchrysallis.Fragments.OpcionesFragment;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.Others.Utils;
import com.example.meetchrysallis.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;


//TODO:
//  Pendiente de hacer:
//      - Evento Detallado:
//          - Descargar los documentos que el evento pueda tener
//      - Completar traducciones
//      - Notificaciones
//      - Arreglar error ComunidadeInteres

//Tutorial Navigation Bottom Bar https://www.youtube.com/watch?v=tPV8xA7m-iw
public class MainActivity extends AppCompatActivity {

    private Context ctx = MainActivity.this;

    public static Socio socio = null;
    public static String idioma = "";

    private static BottomNavigationView botNavBar;

    private long backPressedTime;

    private static String nombreFragment;
    private FragmentManager fm = getSupportFragmentManager();


    private static int selectedFragmentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Antes de cargar el layout, configuramos el idioma
        configurarIdioma();
        Utils.configurarIdioma(ctx, idioma);

        // Cargamos el layout
        setContentView(R.layout.activity_main);

//        pedirPermisos();

        //Iniciamos la Activity del login
        Intent intent = new Intent(ctx, LoginActivity.class);
        startActivityForResult(intent, 1);

        try {
            socio = (Socio)getIntent().getExtras().getSerializable("socio");
        } catch (Exception e) {
            // Error al recoger el extra
            System.err.println(e.toString());
        }

        botNavBar = findViewById(R.id.bottom_nav_bar);
        botNavBar.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            nombreFragment = HomeFragment.class.getName();
                            selectedFragmentId = 1;
                            break;
                        case R.id.nav_eventos:
                            nombreFragment = MisEventosFragment.class.getName();
                            selectedFragmentId = 2;
                            break;
                        case R.id.nav_opciones:
                            nombreFragment = OpcionesFragment.class.getName();
                            selectedFragmentId = 3;
                            break;
                    }
                    refrescar();
                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Venimos de la Activity Login
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null) {
                    socio = (Socio)data.getExtras().getSerializable("socio");

                    cargarFragment(new HomeFragment(this));
                }
            } else {
                finish();
            }
        }
    }

    /**
     * Pide permisos de escritura, lectura y de Internet.
     */
    private void pedirPermisos() {
//        if (Build.VERSION.SDK_INT >= 23) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
//            }
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//            }
//            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//            }
        }

            /*
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        3);
                }*/
//        }
    }

    /**
     * Lee el fichero config.cfg y recoge el idioma guardado. En caso de no existir el archivo,
     * el idioma predefinido será el castellano y se creará un nuevo fichero config.cfg
     */
    private void configurarIdioma() {
        //Cogemos el idioma
        File fileConfig = new File(getExternalFilesDir(null).getPath() + File.separator + "config.cfg");
        idioma = Archivador.leerConfig(fileConfig);

        //Si la variable está vacía ("") significa que no hay archivo de config
        if (idioma.equals("")) {
            //Asignamos el castellano como lengua predefinida
            idioma = "es";
            //Creamos el archivo de config con el idioma predefinido
            Archivador.guardarConfig(fileConfig, idioma);
        }
    }

    public void refrescar(){
        switch(selectedFragmentId) {
            case 1:
                cargarFragment(new HomeFragment(MainActivity.this));
                break;
            case 2:
                cargarFragment(new MisEventosFragment(MainActivity.this));
                break;
            case 3:
                cargarFragment(new OpcionesFragment(MainActivity.this));
                break;
        }

        botNavBar.getMenu().findItem(R.id.nav_home).setTitle(R.string.nav_home);
        botNavBar.getMenu().findItem(R.id.nav_eventos).setTitle(R.string.nav_eventos);
        botNavBar.getMenu().findItem(R.id.nav_opciones).setTitle(R.string.nav_opciones);
    }

    private void cargarFragment(Fragment fragment) {
        fm.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(nombreFragment)
            .commitAllowingStateLoss();
    }

    /**
     * Método para cuando el usuario le dé al botón hacía atrás
     */
    @Override
    public void onBackPressed(){
        //Si le da al botón en un lapso inferior a 1,5 segundos, sale de la Activity (y de la app)
        if(backPressedTime + 1500 > System.currentTimeMillis()){
            finish();
        }
        //Sino, enseña un Snackbar avisándole que para salir tendrá pulsar dos veces seguidas
        else {
            RelativeLayout rl = findViewById(R.id.relative_main_layout);
            Snackbar sb = Snackbar.make(rl, getResources().getString(R.string.pulsar_para_salir), Snackbar.LENGTH_SHORT);
            sb.setBackgroundTint(getResources().getColor(R.color.md_blue_grey_600));
            sb.setTextColor(getResources().getColor(R.color.md_text));
            sb.show();
        }

        //Capturamos el tiempo actual en milisegundos para poder activar la funcion de salir al pulsar dos veces
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refrescar();
    }
}
