package com.exception.findatutor.Activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {

    private RelativeLayout rightRL;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private boolean conStatus = false;
    private GoogleMap GoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static String lat = "";      //33.669483
    public static String lng = "";      //73.074383
    private Handler handler = new Handler();
    private Bundle dataBundle;
    private String username;
    private MongoDB mongoDB;
    private List<UserInfo> alluserInfos;
    private ArrayList<String> allUserUsernames;
    private boolean first_time = true;
    private boolean done = true;
    private boolean busy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        InitializeLocationRequest();
        InitializeGoogleAPIClient();

        new ConnectToServer().execute();

        InitialiseViews();

        dataBundle = getIntent().getExtras();
        username = dataBundle.getString("username");
        Toast.makeText(MainActivity.this, "Username is: " + username, Toast.LENGTH_SHORT).show();
        new CheckNotification().execute();

    }

    public void InitialiseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rightRL = (RelativeLayout) findViewById(R.id.whatYouWantInRightDrawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRight(fab);
            }
        });
    }

    public void onRight(View v) {
        drawer.openDrawer(rightRL);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_us) {
            startActivity(new Intent(MainActivity.this, AboutUs.class));
        }
        if (id == R.id.exit) {
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            Bundle dataBundle = new Bundle();
            dataBundle.putString("username", username);
            intent.putExtras(dataBundle);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
            //startActivity(new Intent(MainActivity.this, MapActivity.class));
        } else if (id == R.id.nav_setting) {
            onRight(fab);
        } else if (id == R.id.nav_listTutors) {
            Intent intent = new Intent(MainActivity.this, Tutors.class);
            Bundle dataBundle = new Bundle();
            dataBundle.putString("username", username);
            intent.putExtras(dataBundle);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, AddaTutor.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected void InitializeGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }
    }

    protected void InitializeLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap = googleMap;
        GoogleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = Double.toString(location.getLatitude());
        lng = Double.toString(location.getLongitude());
        //Toast.makeText(MainActivity.this, "lat is: " + lat + " lng is : " + lng, Toast.LENGTH_SHORT).show();
        //System.out.println("lat is: " + lat + " lng is : " + lng );
        if (!busy) {
            busy = true;
            new UpdateLocationOnLocationChanged().execute();
        }

    }

    private class CheckNotification extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                if (mongoDB.CheckRegisteringAsForNotification(username).equals("tutor")) {
                    if (mongoDB.CheckTutorNotification(username, true).equals("sent")) {
                        NotificationGenerator("Wants to connect", "Hey I want to contact");
                        mongoDB.UpdateTheTutorNOtification(username, "false");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void NotificationGenerator(String title, String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.logo)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle(title)
                        .setContentText(message);
        Intent resultIntent = new Intent(MainActivity.this, CardProfile.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        stackBuilder.addParentStack(CardProfile.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    private void retrieve() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (done) {
                    new ShowUsersNearBy().execute();
                    retrieve();
                } else {
                    retrieve();
                }
            }
        }, 10000);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
        // Zoom in, animating the camera.
        GoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        GoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        return false;
    }

    private class ConnectToServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            if (conStatus) {
                mGoogleApiClient.connect();
                retrieve();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            mongoDB = MongoDB.getInstance();
            conStatus = true;
            return null;
        }
    }


    private class UpdateLocationOnLocationChanged extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            busy = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mongoDB.updateLocation(username, lat, lng);
                //retrieve();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ShowUsersNearBy extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            done = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                alluserInfos = new ArrayList<>();
                allUserUsernames = new ArrayList<>();
                //System.out.println("lat is: " + lat + " lng is : " + lng );
//                mongoDB.AddLocationInUsers(username,lat,lng);
//                mongoDB.showUsers(allUserUsernames, alluserInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            done = true;
        }
    }

}
