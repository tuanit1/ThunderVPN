package com.example.thundervpn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.thundervpn.R;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.proxy.ThunderProxyServices;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private boolean toggle_state = false;
    public static final int REQUEST_PROXY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupDrawer();

        setupToggleButton();
    }

    private void setupToggleButton() {
        if(!toggle_state){
            binding.mycontentview.tvToggle.setText("START");
        }else{
            binding.mycontentview.tvToggle.setText("STOP");
        }

        binding.mycontentview.cvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!toggle_state){
                    startVpn();
                }else {
                    stopVpn();
                }
            }
        });
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, binding.drawerLayout, null, 0, 0);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        binding.mycontentview.btnNavigation.setOnClickListener(view -> binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void stopVpn() {
        binding.mycontentview.tvToggle.setText("START");
        binding.mycontentview.tvStatus.setText("DISCONNECTED");
        toggle_state = false;
        ThunderProxyServices.stop(this);
    }

    private void startVpn() {
        Intent i = VpnService.prepare(this);
        if (i != null) {
            startActivityForResult(i, REQUEST_PROXY);
        } else {
            onActivityResult(REQUEST_PROXY, RESULT_OK, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PROXY) {
            binding.mycontentview.tvToggle.setText("STOP");
            binding.mycontentview.tvStatus.setText("CONNECTED");
            toggle_state = true;
            ThunderProxyServices.start(this);
        }
    }
}