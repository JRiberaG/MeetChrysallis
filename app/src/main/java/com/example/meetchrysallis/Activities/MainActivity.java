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

import com.example.meetchrysallis.Fragments.HomeFragment;
import com.example.meetchrysallis.Fragments.MisEventosFragment;
import com.example.meetchrysallis.Fragments.OpcionesFragment;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.Others.Archivador;
import com.example.meetchrysallis.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

//Tutorial Navigation Bottom Bar https://www.youtube.com/watch?v=tPV8xA7m-iw
//TODO:
//      - Añadir comentario a base de datos
//      - Añadir/dar baja asistencia a base de datos
//      //////////////////////
//      - Acabar de arreglar el asistir - retrofit - api

public class MainActivity extends AppCompatActivity {

    public static Socio socio = null;
    public static String idioma = "";

    private BottomNavigationView botNavBar;
    private ArrayList<Integer> menuIds;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pedirPermisos();

        configurarIdioma();

        //Iniciamos la Activity del login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);

        menuIds = new ArrayList<>();

        botNavBar = findViewById(R.id.bottom_nav_bar);
        botNavBar.setOnNavigationItemSelectedListener(navListener);

        /*getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment(MainActivity.this)).commit();*/

        /*manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new HomeFragment(MainActivity.this)).commit();*/
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    /*if(menuIds.size() <= 1){
                        menuIds.add(menuItem.getItemId());
                    }
                    else{
                        menuIds.set(0, menuIds.get(1));
                        menuIds.set(1, menuItem.getItemId());
                    }*/

                    Fragment selectedFragment = null;
                    String nombreFragment = "";

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment(MainActivity.this);
                            nombreFragment = HomeFragment.class.getName();
                            break;
                        case R.id.nav_eventos:
                            selectedFragment = new MisEventosFragment(MainActivity.this);
                            nombreFragment = MisEventosFragment.class.getName();
                            break;
                        case R.id.nav_opciones:
                            selectedFragment = new OpcionesFragment(MainActivity.this);
                            nombreFragment = OpcionesFragment.class.getName();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).addToBackStack(nombreFragment).commit();


                    /*ft = manager.beginTransaction();
                    ft.replace(R.id.fragment_container, selectedFragment);
                    /*if (manager.getBackStackEntryCount() > 0) {
                        FragmentManager.BackStackEntry backEntry = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1);
                        if (!backEntry.getName().equals(nombreFragment))
                            ft.addToBackStack(nombreFragment);
                    } else
                        ft.addToBackStack(nombreFragment);
                    ft.commit();*/

                    return true;
                }
            };

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
     * Lee el fichero config.cfg y recoge el idioma guardado. En caso de no existir el archivo
     * el idioma predefinido será el castellano y se creará un nuevo fichero config.cfg
     */
    private void configurarIdioma() {
        //Cogemos el idioma
        File fileConfig = new File(getExternalFilesDir(null).getPath() + File.separator + "config.cfg");
        String idiomaAbr = Archivador.leerConfig(fileConfig);
        switch (idiomaAbr){
            case "eng":
                idioma = getResources().getString(R.string.ingles);
                break;
            case "cat":
                idioma = getResources().getString(R.string.catalan);
                break;
            default:
                idioma = getResources().getString(R.string.castellano);
                break;
        }

        //Si la variable está vacía ("") significa que no hay archivo de config
        if (idiomaAbr.equals("")){
            //Asignamos el castellano como lengua predefinida
            idioma = getResources().getString(R.string.castellano);
            //Creamos el archivo de config con el idioma predefinido
            Archivador.guardarConfig(fileConfig, "cast");
        }
    }
}
