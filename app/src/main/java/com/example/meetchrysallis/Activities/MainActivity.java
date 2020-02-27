package com.example.meetchrysallis.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.meetchrysallis.Fragments.BuscarFragment;
import com.example.meetchrysallis.Fragments.HomeFragment;
import com.example.meetchrysallis.Fragments.PerfilFragment;
import com.example.meetchrysallis.Models.Administrador;
import com.example.meetchrysallis.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

//Tutorial Navigation Bottom Bar https://www.youtube.com/watch?v=tPV8xA7m-iw

public class MainActivity extends AppCompatActivity {

    private List<Administrador> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Iniciamos la Activity del login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);

        BottomNavigationView botNavBar = findViewById(R.id.bottom_nav_bar);
        botNavBar.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_perfil:
                            selectedFragment = new PerfilFragment();
                            break;
                        case R.id.nav_buscar:
                            selectedFragment = new BuscarFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if (resultCode == Activity.RESULT_CANCELED){
                //System.exit(0);
                finish();
            }
            else if(resultCode == Activity.RESULT_FIRST_USER)
                //System.exit(0);
                finish();
        }
    }
}
