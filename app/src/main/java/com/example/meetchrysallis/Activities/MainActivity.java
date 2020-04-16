package com.example.meetchrysallis.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
//      - Que se actualicen eventos disponibles/mis eventos cuando se inserta/elimina asistir
//      - Evento Detallado:
//          - Descargar los documentos que el evento pueda tener
//      - Programar funcionalidad 'Contactar equipo desarrolladores'
//      - Arreglar el capturar timestamp: captura la hora del movil, no la hora real
//          (la hora del movil puede ser las 12:00 pero la real 14:00)
//      - Buscar una fuente y aplicarla a los textos
//      - Mejorar las credenciales: no guardar el socio entero. Una vez logueado, llamar a la api
//          y recuperarlo entero (y almacenarlo en la MainActivity), no tal y como está hecho ahora
//      - Mejorar el fragment de mis eventos: cada vez que se accede se llaman dos veces a la API,
//          buscar un modo para que sólo llame dos veces (al abrir la app y al refrescar)
//      - Arreglar el cambio de idiomas: cuando se cambia el idioma y se vuelve a atrás,
//            la pantalla de 'Opciones' no se ve modificada, ni tampoco los textos
//            del menú

//Tutorial Navigation Bottom Bar https://www.youtube.com/watch?v=tPV8xA7m-iw
public class MainActivity extends AppCompatActivity {

    public static Socio socio = null;
    public static String idioma = "";
    public static boolean idiomaCambiado = false;

    private static BottomNavigationView botNavBar;
    //private static ArrayList<Fragment> fragments = new ArrayList<>();

    private long backPressedTime;

    private static String nombreFragment;
    private FragmentManager fm = getSupportFragmentManager();


    private static int selectedFragmentId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarIdioma();
        Utils.configurarIdioma(MainActivity.this, idioma);
        setContentView(R.layout.activity_main);

        pedirPermisos();

//        fragments.add(new HomeFragment(MainActivity.this));
//        fragments.add(new MisEventosFragment(MainActivity.this));
//        fragments.add(new OpcionesFragment(MainActivity.this));



        //Iniciamos la Activity del login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);

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
                socio = (Socio)data.getExtras().getSerializable("socio");

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment(MainActivity.this)).commit();
            }
            else{
                finish();
            }
        }
    }

    /**
     * Pide permisos de escritura, lectura y de Internet.
     */
    private void pedirPermisos() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
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
        }
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
            .commit();
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
