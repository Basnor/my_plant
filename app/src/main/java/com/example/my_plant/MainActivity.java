package com.example.my_plant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.my_plant.fragments.FragmentAdd;
import com.example.my_plant.fragments.FragmentMain;
import com.example.my_plant.fragments.FragmentProfileList;
import com.example.my_plant.fragments.FragmentSettings;
import com.example.my_plant.fragments.FragmentStatistic;

import database.DBProfile;
import model.Profile;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("StaticFieldLeak")

    FragmentAdd fragmentAdd;
    FragmentMain fragmentMain;
    FragmentProfileList fragmentProfileList;
    FragmentStatistic fragmentStatistic;
    FragmentSettings fragmentSettings;

    private TextView navName;
    private TextView navType;
    private DBProfile mDBProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentAdd = new FragmentAdd();
        fragmentMain = new FragmentMain();
        fragmentProfileList = new FragmentProfileList();
        fragmentStatistic = new FragmentStatistic();
        fragmentSettings = new FragmentSettings();

        // Set Homepage Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentMain).commit();
            navigationView.setCheckedItem(R.id.nav_main);
        }

        PersistentStorage.init(this);

        initNavText();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_main) {
            ftrans.replace(R.id.container, fragmentMain);
        } else if (id == R.id.nav_statistics) {
            ftrans.replace(R.id.container, fragmentStatistic);
        } else if (id == R.id.nav_manage) {
            ftrans.replace(R.id.container, fragmentSettings);
        } else if (id == R.id.nav_add) {
            ftrans.replace(R.id.container, fragmentAdd);
        } else if (id == R.id.nav_choose) {
            ftrans.replace(R.id.container, fragmentProfileList);
        }
        ftrans.commit();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // Позволяет прятать клавиатуру при нажатии на пустую часть поля
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void initNavText() {
        NavigationView navigationView = findViewById(R.id.nav_view);

        View navHeaderMain = navigationView.getHeaderView(0);
        navName = navHeaderMain.findViewById(R.id.nav_name);
        navType = navHeaderMain.findViewById(R.id.nav_type);

        mDBProfile = new DBProfile(this);
        Profile profile = mDBProfile.getProfileById(PersistentStorage.getLongProperty(PersistentStorage.CURRENT_PROFILE_ID_KEY));
        navName.setText(profile.getName());
        navType.setText(profile.getCollection().getTypeName());
        mDBProfile.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
