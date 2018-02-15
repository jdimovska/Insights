package com.example.marinaangelovska.insights.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import android.view.inputmethod.InputMethodManager;

import com.example.marinaangelovska.insights.Fragment.AppsFragment;
import com.example.marinaangelovska.insights.Fragment.ContactsFragment;
import com.example.marinaangelovska.insights.Fragment.HomeFragment;
import com.example.marinaangelovska.insights.Fragment.MessagesFragment;
import com.example.marinaangelovska.insights.Fragment.NetworkFragment;
import com.example.marinaangelovska.insights.Fragment.PeopleFragment;
import com.example.marinaangelovska.insights.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.example.marinaangelovska.insights.Fragment.HomeFragment.unlockedTimesButton;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 100;
    private static final int PERMISSIONS_REQUEST_READ_SMS_LOG = 200;
    public static ProgressDialog appDialog;
    public static ProgressDialog homeDialog;
    final Handler finalHandler = new Handler();
    Calendar cal = Calendar.getInstance();
    Date currentDate;
    Date nextDate;
    SimpleDateFormat sdf;
    SharedPreferences sharedPreferences;
    int unlockedTimes = 0;
    PhoneUnlockedReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpDialogViews();

        sdf = new SimpleDateFormat("yyyy-MM-dd");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS};

        if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        sharedPreferences = getSharedPreferences("AppsPreferences", Context.MODE_PRIVATE);
        receiver = new PhoneUnlockedReceiver();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadHomeFragment();

    }
    private void setUpDialogViews() {
        appDialog = new ProgressDialog(MainActivity.this);
        appDialog.setMessage("Applications loading...");
        appDialog.setCancelable(false);
        appDialog.setInverseBackgroundForced(false);

        homeDialog = new ProgressDialog(MainActivity.this);
        homeDialog.setMessage("Home screen loading...");
        homeDialog.setCancelable(false);
        homeDialog.setInverseBackgroundForced(false);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        unlockedTimes = sharedPreferences.getInt("unlockedTimes", 0);
        unlockedTimesButton.setText("Today you have unlocked your phone " + unlockedTimes + " times");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        return super.onOptionsItemSelected(item);
    }
    private void loadHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.root_layout, homeFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_home) {
            homeDialog.show();
            final Runnable changeView = new Runnable()
            {
                public void run()
                {
                   loadHomeFragment();
                    unlockedTimes = sharedPreferences.getInt("unlockedTimes", 0);
                    unlockedTimesButton.setText("Today you have unlocked your phone " + unlockedTimes + " times");
                }
            };

            finalHandler.postDelayed(changeView, 500);

        } else if (id == R.id.nav_contacts) {
            ContactsFragment contactsFragment = new ContactsFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.root_layout, contactsFragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        } else if (id == R.id.nav_messages) {
            MessagesFragment messagesFragment = new MessagesFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.root_layout, messagesFragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        } else if(id == R.id.nav_people) {

            PeopleFragment peopleFragment = new PeopleFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.root_layout, peopleFragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        } else if(id == R.id.nav_apps) {

            appDialog.show();
            final Runnable changeView = new Runnable()
            {
                public void run()
                {
                    AppsFragment appFragment = new AppsFragment();
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.root_layout, appFragment);
                    fragmentTransaction.commit();
                    fragmentManager.executePendingTransactions();
                }
            };

            finalHandler.postDelayed(changeView, 500);

        } else if(id == R.id.nav_network) {
            NetworkFragment networkFragment = new NetworkFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.root_layout, networkFragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //grant permissions on run time
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void isNewDay() throws ParseException {
        Calendar c = Calendar.getInstance();
        int thisDay = c.get(Calendar.DAY_OF_YEAR);
        long todayMillis = c.getTimeInMillis();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long last = prefs.getLong("date", System.currentTimeMillis());
        c.setTimeInMillis(last);
        int lastDay = c.get(Calendar.DAY_OF_YEAR);

        if (lastDay == thisDay) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            unlockedTimes = 0;
            editor.putInt("unlockedTimes", unlockedTimes);
            editor.commit();

            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong("date", todayMillis + 86400000);
            edit.commit();

        }
    }
    public class PhoneUnlockedReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                isNewDay();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                unlockedTimes = sharedPreferences.getInt("unlockedTimes", 0) + 1;
                editor.putInt("unlockedTimes", unlockedTimes);
                editor.commit();
                unlockedTimesButton.setText("Today you have unlocked your phone " + unlockedTimes + " times");

            }
        }
    }
}
