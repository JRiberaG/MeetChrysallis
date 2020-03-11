package com.example.meetchrysallis.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.meetchrysallis.Fragments.HomeFragment;
import com.example.meetchrysallis.Fragments.MisEventosFragment;
import com.example.meetchrysallis.Fragments.OpcionesFragment;
import com.example.meetchrysallis.Models.Socio;
import com.example.meetchrysallis.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

//Tutorial Navigation Bottom Bar https://www.youtube.com/watch?v=tPV8xA7m-iw

public class MainActivity extends AppCompatActivity {

    public static Socio socio = null;

    private BottomNavigationView botNavBar;
    private ArrayList<Integer> menuIds;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pedirPermisos();

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


    @Override
    public void onBackPressed(){
        if(backPressedTime + 1500 > System.currentTimeMillis())
            finish();
        else {
            RelativeLayout rl = findViewById(R.id.relative_main_layout);
            Snackbar sb = Snackbar.make(rl, "Pulse otra vez pasa salir", Snackbar.LENGTH_SHORT);
            sb.setBackgroundTint(getResources().getColor(R.color.md_light_blue_700));
            sb.setTextColor(getResources().getColor(R.color.md_text));
            sb.show();
        }

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
            else
                finish();
        }
    }

    /**
     * Pide permisos de escritura, lectura y de Internet.
     */
    private void pedirPermisos() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET},
                2);
    }
}
