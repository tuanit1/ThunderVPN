package com.example.thundervpn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.thundervpn.asynctasks.GetUserAsync;
import com.example.thundervpn.items.MyUser;
import com.example.thundervpn.listeners.GetUserListener;
import com.example.thundervpn.listeners.MyListener;
import com.example.thundervpn.utils.SharedPref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.example.thundervpn.R;
import com.example.thundervpn.asynctasks.GetAllCountryAsync;
import com.example.thundervpn.asynctasks.GetCountryProxyAsync;
import com.example.thundervpn.fragments.MyBottomSheetFragments;
import com.example.thundervpn.items.Country;
import com.example.thundervpn.items.MyProxy;
import com.example.thundervpn.listeners.GetAllCountryListener;
import com.example.thundervpn.listeners.GetCountryProxyListener;
import com.example.thundervpn.listeners.ItemCountryClickListener;
import com.example.thundervpn.proxyconfig.core.ThunderVpnServices;
import com.example.thundervpn.proxyconfig.core.ProxyConfig;
import com.example.thundervpn.databinding.ActivityMainBinding;
import com.example.thundervpn.utils.Constant;
import com.example.thundervpn.utils.Methods;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                ThunderVpnServices.onStatusChangedListener{

    private ActivityMainBinding binding;
    private boolean toggle_state = false;
    private Methods methods;
    private SharedPref sharedPref;
    private ArrayList<Country> mCountries;
    public static final int REQUEST_PROXY = 1;
    private int CURRENT_COUNTRY;
    private int notificationId = 11;
    private Notification notification;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        mCountries = new ArrayList<>();
        notificationManager = NotificationManagerCompat.from(this);

        setupNotification();

        setupDrawer();

        setupToggleButton();

        LoadCountry();

        setLastCountry();

        ThunderVpnServices.addOnStatusChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Constant.isLogged){
            GetUserState();
        }else{
            LoadBannerAds();
        }
    }

    private void LoadBannerAds(){
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.mycontentview.adView.loadAd(adRequest);
    }

    private void GetUserState(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);

        GetUserAsync async = new GetUserAsync(methods.getRequestBody("method_get_user", bundle), methods, new GetUserListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, MyUser user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Constant.IS_PREMIUM = user.getExpired_date().after(new Date());

                        if(Constant.IS_PREMIUM){
                            binding.mycontentview.adsContainer.removeView(binding.mycontentview.adView);
                        }

                    }else{
                        LoadBannerAds();
                    }

                }
            }
        });

        async.execute();
    }

    private void setupNotification(){
        //tap action
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //init notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constant.NOTI_CHANNEL)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle("Your internet is private now.")
                .setContentText("Tap to change your settings.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
    }

    private void setupMoreCountryButton() {

        binding.mycontentview.cvCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBottomSheetFragments myBottomSheetFragments = new MyBottomSheetFragments(new ItemCountryClickListener() {
                    @Override
                    public void onClick(int id) {

                        if(CURRENT_COUNTRY != id){
                            CURRENT_COUNTRY = id;

                            SetProxy(true);
                        }

                    }
                }, mCountries);

                myBottomSheetFragments.show(getSupportFragmentManager(), myBottomSheetFragments.getTag());
            }
        });

    }

    private void LoadCountry(){
        GetAllCountryAsync async = new GetAllCountryAsync(methods.getRequestBody("method_get_allcountry", null), new GetAllCountryListener() {
            @Override
            public void onStart() {
                mCountries.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<Country> arrayList_country) {
                if(methods.isNetworkConnected()){
                    if(status){
                        mCountries.add(new Country(0, "Default", false, "a"));
                        mCountries.addAll(arrayList_country);
                        mCountries.sort(Comparator.comparing(Country::isPremium));
                    }else{
                        Toast.makeText(getApplicationContext(), Constant.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }

//                    progressBar.setVisibility(View.GONE);
//
//                    setupUI();

                }else{
//                    Toast.makeText(getContext(), Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
                }

                setupMoreCountryButton();

            }
        });
        async.execute();
    }

    private void setDefaultProxy(boolean isToggle){
        if(methods.isNetworkConnected()){
            MyProxy proxy = methods.getDefaultProxy();
            ProxyConfig.Instance.setProxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
            sharedPref.setCurrentCountry(0);
            Log.e("VPN", "Change to: " + proxy.getHost() + ":" + proxy.getPort());

            Picasso.get()
                    .load(R.drawable.global)
                    .error(R.drawable.global)
                    .into(binding.mycontentview.ivCountry);

            binding.mycontentview.tvCountry.setText("Default");

            if(isToggle){
                if(!ThunderVpnServices.IsRunning){
                    binding.mycontentview.tvStatus.setText("CONNECTING...");
                    startVpn();
                }else{
                    switchCountryText();
                }
            }

        }else{
            Toast.makeText(MainActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

    private void SetProxy(boolean isToggle) {
        if(CURRENT_COUNTRY == 0){
            setDefaultProxy(isToggle);
        }else{
            Bundle bundle = new Bundle();
            bundle.putInt("id", CURRENT_COUNTRY);

            GetCountryProxyAsync async = new GetCountryProxyAsync(methods, methods.getRequestBody("method_get_proxy", bundle), new GetCountryProxyListener() {
                @Override
                public void onStart() {

                    if(isToggle){
                        binding.mycontentview.tvStatus.setText("REDIRECTING...");
                    }

                }

                @Override
                public void onEnd(boolean status, Country country, ArrayList<MyProxy> arrayList_proxy) {
                    if(methods.isNetworkConnected()){
                        if(status){

                            if(!arrayList_proxy.isEmpty()){
                                //get random proxy in list
                                int ran_index = (int)(Math.random() * arrayList_proxy.size());
                                MyProxy proxy = arrayList_proxy.get(ran_index);
                                ProxyConfig.Instance.setProxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
                                sharedPref.setCurrentCountry(country.getId());
                                Log.e("VPN", "Change to: " + proxy.getHost() + ":" + proxy.getPort());
                            }else {
                                MyProxy proxy = methods.getDefaultProxy();
                                ProxyConfig.Instance.setProxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
                                sharedPref.setCurrentCountry(0);
                                Log.e("VPN", "Change to: " + proxy.getHost() + ":" + proxy.getPort());
                            }

                            Picasso.get()
                                    .load(country.getThumb())
                                    .error(R.drawable.global)
                                    .into(binding.mycontentview.ivCountry);

                            binding.mycontentview.tvCountry.setText(country.getName());

                            if(isToggle){
                                if(!ThunderVpnServices.IsRunning){
                                    binding.mycontentview.tvStatus.setText("CONNECTING...");
                                    startVpn();
                                }else{
                                    switchCountryText();
                                }
                            }


                        }else{
                            setDefaultProxy(isToggle);
                        }
                    }else{
                        Toast.makeText(MainActivity.this, Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                    }

                }
            });

            async.execute();
        }
    }

    private void setLastCountry(){
        CURRENT_COUNTRY = sharedPref.getCurrentCountry();

        SetProxy(false);
    }

    private void setupToggleButton() {

        toggle_state = ThunderVpnServices.IsRunning;

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
                        binding.mycontentview.tvStatus.setText("CONNECTING...");
                        startVpn();
                    }else{
                        Toast.makeText(getApplicationContext(), Constant.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    ThunderVpnServices.IsRunning = false;
                }
            }
        });
    }

    private void ShowVpnNotification(){
        notificationManager.notify(notificationId, notification);
    }

    private void RemoveVpnNotification(){
        notificationManager.cancelAll();
    }

    private void switchCountryText(){
        binding.mycontentview.tvStatus.setText("CONNECTING...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.mycontentview.tvStatus.setText("CONNECTED");
            }
        },700);
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

                methods.showInterAds(new MyListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
                    }
                });



                break;
            case R.id.nav_unlock:

                methods.showInterAds(new MyListener() {
                    @Override
                    public void onClick() {

                    }
                });

                break;
            case R.id.nav_feedback:

                methods.showInterAds(new MyListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                    }
                });



                break;
            case R.id.nav_rate:
                methods.showInterAds(new MyListener() {
                    @Override
                    public void onClick() {

                    }
                });
                break;
            case R.id.nav_policy:
                methods.showInterAds(new MyListener() {
                    @Override
                    public void onClick() {

                    }
                });
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
            startService(new Intent(this, ThunderVpnServices.class));
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
            ShowVpnNotification();
        }else {
            binding.mycontentview.tvToggle.setText("START");
            binding.mycontentview.tvStatus.setText("DISCONNECTED");
            binding.mycontentview.rvToggle.setBackgroundResource(R.drawable.toggle_button_off);
            binding.mycontentview.ivPower.setImageResource(R.drawable.power_button_off);
            toggle_state = false;
            RemoveVpnNotification();
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
        ThunderVpnServices.removeOnStatusChangedListener(this);
        super.onDestroy();
    }
}