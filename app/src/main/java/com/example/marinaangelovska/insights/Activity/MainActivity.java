package com.example.marinaangelovska.insights.Activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
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

import com.example.marinaangelovska.insights.Fragment.AboutFragment;
import com.example.marinaangelovska.insights.Fragment.AppsFragment;
import com.example.marinaangelovska.insights.Fragment.ContactsFragment;
import com.example.marinaangelovska.insights.Fragment.HomeFragment;
import com.example.marinaangelovska.insights.Fragment.MessagesFragment;
import com.example.marinaangelovska.insights.Fragment.NetworkFragment;
import com.example.marinaangelovska.insights.Fragment.PeopleFragment;
import com.example.marinaangelovska.insights.Helper.AppDatabaseHelper;
import com.example.marinaangelovska.insights.Model.Call;
import com.example.marinaangelovska.insights.Model.Message;
import com.example.marinaangelovska.insights.Model.Person;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.NormalizeNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.example.marinaangelovska.insights.Fragment.HomeFragment.unlockedTimesButton;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 100;
    private static final int PERMISSIONS_REQUEST_READ_SMS_LOG = 200;
    public static ProgressDialog dialog;
    final Handler finalHandler = new Handler();
    Calendar cal = Calendar.getInstance();
    Date currentDate;
    Date nextDate;
    SimpleDateFormat sdf;
    SharedPreferences sharedPreferences;
    int unlockedTimes = 0;
    PhoneUnlockedReceiver receiver;
    AppDatabaseHelper helper;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dialog = new ProgressDialog(MainActivity.this);

        sdf = new SimpleDateFormat("yyyy-MM-dd");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE};

        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        sharedPreferences = getSharedPreferences("AppsPreferences", Context.MODE_PRIVATE);
        receiver = new PhoneUnlockedReceiver();

        checkPermissions();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        unlockedTimes = sharedPreferences.getInt("unlockedTimes", 0);
        setUpScreen();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        helper = new AppDatabaseHelper(getApplicationContext());
        dialog.show();
        setUpDialogViews("Loading content...");
        DatabaseTask task =new DatabaseTask();
        task.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void fillDatabase() {
        SQLiteDatabase dbDelete = helper.getWritableDatabase();
        dbDelete.execSQL("Delete from call_log");
        dbDelete.execSQL("Delete from message_log");
        dbDelete.execSQL("Delete from people");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                phNumber = NormalizeNumber.normalizeNumber(phNumber);
                String callType = managedCursor.getString(type);
                String phName = managedCursor.getString(name);
                String phDate = managedCursor.getString(date);
                String callDuration = managedCursor.getString(duration);

                String sql="INSERT INTO call_log(name,type,number,date,duration) VALUES(?,?,?,?,?)";
                SQLiteStatement statement=dbDelete.compileStatement(sql);

                if(phName == null) {
                    phName = "Unknown";
                }
                statement.bindString(1,phName);
                statement.bindString(2,callType);
                statement.bindString(3,phNumber);
                statement.bindString(4,phDate);
                statement.bindString(5,callDuration);
                statement.execute();

            }
            managedCursor.close();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = this.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);

            int body = managedCursor.getColumnIndex(Telephony.Sms.BODY);
            int phone = managedCursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int date = managedCursor.getColumnIndex(Telephony.Sms.DATE);
            int type = managedCursor.getColumnIndex(Telephony.Sms.TYPE);


            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(phone);
                phNumber = NormalizeNumber.normalizeNumber(phNumber);
                String callType = managedCursor.getString(type);
                String phDate = managedCursor.getString(date);
                String phBody = managedCursor.getString(body);

                String sql="INSERT INTO message_log(number,type,date,content) VALUES(?,?,?,?)";
                SQLiteStatement statement=dbDelete.compileStatement(sql);

                statement.bindString(1,phNumber);
                statement.bindString(2,callType);
                statement.bindString(3,phDate);
                statement.bindString(4,phBody);
                statement.execute();

            }
            managedCursor.close();
        }


        Cursor managedCursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        int display_name = managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int number = managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        while (managedCursor.moveToNext()) {
            String phDisplayName = managedCursor.getString(display_name);
            String phNumber = managedCursor.getString(number);
            phNumber = NormalizeNumber.normalizeNumber(phNumber);
            NormalizeNumber normalizeNumber = new NormalizeNumber();
            phNumber = normalizeNumber.normalizeNumber(phNumber);


            SQLiteDatabase db = helper.getWritableDatabase();
            String[] columns ={"name"};
            Cursor cursor = db.query("call_log", columns, "type=? and number=?", new String[] { "1", phNumber }, null, null, null);
            int factor = cursor.getCount();
            String phFactor = Integer.toString(factor);

            String sql="INSERT INTO people(name,number,factor) VALUES(?,?,?)";
            SQLiteStatement statement=dbDelete.compileStatement(sql);

            statement.bindString(1,phDisplayName);
            statement.bindString(2,phNumber);
            statement.bindDouble(3,factor);
            statement.execute();


        }
        managedCursor.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpScreen() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            setUpDialogViews("Home screen loading...");
            dialog.show();
            final Runnable changeView = new Runnable()
            {
                public void run()
                {
                    loadHomeFragment();
                    unlockedTimes = sharedPreferences.getInt("unlockedTimes", 0);
                    unlockedTimesButton.setText("Today you have unlocked your phone " + unlockedTimes + " times");
                }
            };
            finalHandler.postDelayed(changeView, 400);

        } else
            loadAboutFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkPermissions() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

    }
    private void setUpDialogViews(String message) {
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

    }

    public class DatabaseTask extends AsyncTask<String,Void,String> {


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {
            fillDatabase();
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.hide();
        }
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();


    }

    @Override
    protected void onPause() {
        super.onPause();
        dialog.dismiss();
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
    private void loadAboutFragment() {
        AboutFragment aboutFragment = new AboutFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.root_layout, aboutFragment);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_home) {
            setUpDialogViews("Home screen loading...");
            dialog.show();
            final Runnable changeView = new Runnable()
            {
                public void run()
                {
                   loadHomeFragment();
                    unlockedTimes = sharedPreferences.getInt("unlockedTimes", 0);
                    unlockedTimesButton.setText("Today you have unlocked your phone " + unlockedTimes + " times");
                }
            };

            finalHandler.postDelayed(changeView, 400);

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

            setUpDialogViews("Applications loading...");
            dialog.show();
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

        } else if(id == R.id.nav_about) {
            loadAboutFragment();
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
    class PhoneUnlockedReceiver extends BroadcastReceiver {
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
