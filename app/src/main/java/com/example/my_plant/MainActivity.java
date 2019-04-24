package com.example.my_plant;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bluetooth.BluetoothSPP;
import com.example.bluetooth.BluetoothState;

import com.example.my_plant.fragments.FragmentAdd;
import com.example.my_plant.fragments.FragmentMain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("StaticFieldLeak")
    //public static BluetoothSPP bt;
    //public static String address = "98:D3:31:FB:54:46";

    FragmentAdd fragmentAdd;
    FragmentMain fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

/*
        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Попробуй обновить соединение"
                    , Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.i("Check", "Message : " + message);
                analyzeIncomeMsg(message);
            }
        });*/

        fragmentAdd = new FragmentAdd();
        fragmentMain = new FragmentMain();

       // navigationView = findViewById(R.id.nav_main);
       //
        // navigationView.getMenu().getItem(0).setChecked(true);

        PersistentStorage.init(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_main) {
            ftrans.replace(R.id.container, fragmentMain);
        } else if (id == R.id.nav_statistics) {

        } else if (id == R.id.nav_recommend_params) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_add) {
            ftrans.replace(R.id.container, fragmentAdd);

        } else if (id == R.id.nav_choose) {

        } else if (id == R.id.nav_create_params) {

        }ftrans.commit();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //bt.stopService();
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bt.stopService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bt.getServiceState() != BluetoothState.STATE_CONNECTED) {
            try {
                if (!address.equals(""))
                    bt.connect(address);
            } catch (Exception ignored) {
            }
        }
    }*/

}
