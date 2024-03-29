package com.project.hawfarmbusiness;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView displayUserImage;
    TextView displayName, displayEmail;
    String userDataString;
    JSONObject userDataJson;
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        displayName = headerView.findViewById(R.id.user_name);
        displayEmail = headerView.findViewById(R.id.user_email);
        displayUserImage = headerView.findViewById(R.id.user_imageView);

        userDataString = getIntent().getStringExtra("userData");
        try {
            Log.d("USERDATA", "User Data String: " + userDataString);
            userDataJson = new JSONObject(userDataString.trim());

            displayName.setText(userDataJson.getString("name"));
            displayEmail.setText(userDataJson.getString("email"));
            Picasso.get().load(userDataJson.getString("userImageURL")).into(displayUserImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new HomeMainFragment()).commit();
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
        getMenuInflater().inflate(R.menu.home, menu);
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
        if (id == R.id.action_logout) {
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(HomeActivity.this, LogInActivity.class));
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new HomeMainFragment()).commit();
        }
        if (id == R.id.nav_add_stock) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new AddStockFragment()).commit();
        }
        if (id == R.id.nav_my_stock) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new CurrentStockFragment()).commit();
        }
        if (id == R.id.nav_all_order) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new OrderFragment()).commit();
        }
        if (id == R.id.nav_pending_order) {
            Bundle bundle = new Bundle();
            bundle.putString("status", "pending");
            OrderFragment orderFragment = new OrderFragment();
            orderFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, orderFragment).commit();
        }
        if (id == R.id.nav_for_delivery) {
            Bundle bundle = new Bundle();
            bundle.putString("status", "accepted");
            OrderFragment orderFragment = new OrderFragment();
            orderFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, orderFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public JSONObject getUser() {
        return userDataJson;
    }
}
