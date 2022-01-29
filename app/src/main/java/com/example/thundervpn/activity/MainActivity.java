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
import android.widget.Toast;

import com.example.thundervpn.R;
import com.example.thundervpn.asynctasks.GetAllCountryAsync;
import com.example.thundervpn.asynctasks.GetCountryProxyAsync;
import com.example.thundervpn.fragments.MyBottomSheetFragments;
import com.example.thundervpn.items.Country;
import com.example.thundervpn.items.MyProxy;
import com.example.thundervpn.listeners.GetAllCountryListener;
import com.example.thundervpn.listeners.GetCountryProxyListener;
import com.example.thundervpn.listeners.ItemCountryClickListener;
import com.example.thundervpn.proxyconfig.core.LocalVpnService;
import com.example.thundervpn.proxyconfig.core.ProxyConfig;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                LocalVpnService.onStatusChangedListener{

    private ActivityMainBinding binding;
    private boolean toggle_state = false;
    private Methods methods;
    private ArrayList<Country> mCountries;
    public static final int REQUEST_PROXY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);
        mCountries = new ArrayList<>();

        setupDrawer();

        setupToggleButton();

        setupMoreCountryButton();

        //String text = "http://namlanvy:namlanvy@23.229.109.154:6180";

        ProxyConfig.Instance.setProxy("23.229.109.154", 6180, "namlanvy", "namlanvy");
        ProxyConfig.setHttpProxyServer(MainActivity.this, "23.229.109.154", 6180, "namlanvy", "namlanvy");
        LocalVpnService.addOnStatusChangedListener(this);
    }

    private void setupMoreCountryButton() {

        binding.mycontentview.btnMoreCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBottomSheetFragments myBottomSheetFragments = new MyBottomSheetFragments(new ItemCountryClickListener() {
                    @Override
                    public void onClick(int id) {
                        if(id == 0){
                            //default

                        }else{
                            GetProxy(id);
                        }
                    }
                });

                myBottomSheetFragments.show(getSupportFragmentManager(), myBottomSheetFragments.getTag());
            }
        });

    }

    private void GetProxy(int country_id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", country_id);

        GetCountryProxyAsync async = new GetCountryProxyAsync(methods, methods.getRequestBody("method_get_proxy", bundle), new GetCountryProxyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, Country country, ArrayList<MyProxy> arrayList_proxy) {
                if(methods.isNetworkConnected()){
                    if(status){
                        if(arrayList_proxy.isEmpty()){
                            //get random proxy in list
                            int ran_index = (int)(Math.random() * arrayList_proxy.size());
                            MyProxy proxy = arrayList_proxy.get(ran_index);
                            ProxyConfig.Instance.setProxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
                        }else {
                            MyProxy proxy = methods.getDefaultProxy();
                            ProxyConfig.Instance.setProxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
                        }

                    }else{
                        Toast.makeText(MainActivity.this, Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                }
            }
        });

        async.execute();
    }

    private void setupToggleButton() {

        toggle_state = LocalVpnService.IsRunning;

        if(!toggle_state){
            binding.mycontentview.tvToggle.setText("START");
            binding.mycontentview.rvToggle.setBackgroundResource(R.drawable.toggle_button_off);
            binding.mycontentview.ivPower.setImageResource(R.drawable.power_button_off);
        }else{
            binding.mycontentview.tvToggle.setText("STOP");
            binding.mycontentview.rvToggle.setBackgroundResource(R.drawable.toggle_button);
            binding.mycontentview.ivPower.setImageResource(R.drawable.power_button);
        }

        binding.mycontentview.cvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!toggle_state){
                    if(methods.isNetworkConnected()){
                        startVpn();
                    }else{
                        Toast.makeText(getApplicationContext(), Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    LocalVpnService.IsRunning = false;
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
        navigationView.setItemIconTintList(null);
        binding.mycontentview.btnNavigation.setOnClickListener(view -> binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.nav_profile:

                startActivity(new Intent(MainActivity.this, MyAccountActivity.class));

                break;
            case R.id.nav_unlock:
                break;
            case R.id.nav_feedback:
                break;
            case R.id.nav_rate:
                break;
            case R.id.nav_policy:
                break;
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
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
            startService(new Intent(this, LocalVpnService.class));
        }
    }

    @Override
    public void onStatusChanged(String status, Boolean isRunning) {
        if(isRunning){
            binding.mycontentview.tvToggle.setText("STOP");
            binding.mycontentview.tvStatus.setText("CONNECTED");
            binding.mycontentview.rvToggle.setBackgroundResource(R.drawable.toggle_button);
            binding.mycontentview.ivPower.setImageResource(R.drawable.power_button);
            toggle_state = true;
        }else {
            binding.mycontentview.tvToggle.setText("START");
            binding.mycontentview.tvStatus.setText("DISCONNECTED");
            binding.mycontentview.rvToggle.setBackgroundResource(R.drawable.toggle_button_off);
            binding.mycontentview.ivPower.setImageResource(R.drawable.power_button_off);
            toggle_state = false;
        }
    }

    public void reRunVpn(){
        if(LocalVpnService.IsRunning){
            LocalVpnService.IsRunning = false;

            //new Runnable()
        }else{
            startVpn();
        }
    }


    @Override
    public void onLogReceived(String logString) {

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);;
    }

    private void openLoginActivity(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(this);
        super.onDestroy();
    }
}