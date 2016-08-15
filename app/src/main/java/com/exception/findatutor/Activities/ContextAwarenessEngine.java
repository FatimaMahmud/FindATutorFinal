//package com.exception.findatutor.Activities;
//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
//import com.exception.findatutor.conn.MongoDB;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ContextAwarenessEngine extends Thread implements ConnectionCallbacks,
//        OnConnectionFailedListener, LocationListener {
//
//    private static ContextAwarenessEngine contextAwarenessEngine;
//
//    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;
//
//    private MongoDB mongodb;
//
//    private String name;
//    private String lat = "";
//    private String lng = "";
//
//    private boolean done = true;
//    private boolean busy = false;
//
//    private List<UserInfo> alluserInfos;
//    private ArrayList<String> allUserUsernames;
//    private int radius = 9;
//    private Context context;
//
//    private ContextAwarenessEngine(Context context, String name) {
//        this.context = context;
//        this.name = name;
//        InitializeLocationRequest();
//        InitializeGoogleAPIClient();
//    }
//
//    public static ContextAwarenessEngine getInstance(Object... params) {
//        if (contextAwarenessEngine == null) {
//            contextAwarenessEngine = new ContextAwarenessEngine((Context) params[0], (String) params[1]);
//        }
//        return contextAwarenessEngine;
//    }
//
//    protected void InitializeLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    protected void InitializeGoogleAPIClient() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(context)
//                    .addApi(LocationServices.API).addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this).build();
//        }
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        mongodb = MongoDB.getInstance();
//        mGoogleApiClient.connect();
//        System.out.println("lat is " + lat);
//        retrieve();
//    }
//
//    private void retrieve() {
//        if (done) {
//            new ShowUsersNearBy().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//        try {
//            Thread.sleep(3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        retrieve();
//    }
//
//    private class ShowUsersNearBy extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            done = false;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                showUsers();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            done = true;
//        }
//    }
//
//    private void showUsers() {
//        alluserInfos = new ArrayList<>();
//        allUserUsernames = new ArrayList<>();
//        try {
//            mongodb.AddLocationInUsers("Fatima",lat,lng);
//            mongodb.showUsers(allUserUsernames, alluserInfos);
//
//            for (UserInfo alluser : alluserInfos) {
//                System.out.println("Name - " + alluser.getName() +
//                        "Latitude - " + alluser.getLat() +
//                        "Longitude - " + alluser.getLng() +
//                        "Status - " + alluser.getStatus());
//            }
//
////            seeNearByFriends();
////
////            for (UserInfo alluser : alluserInfos) {
////                System.out.println("Name - " + alluser.getName() +
////                        "Latitude - " + alluser.getLat() +
////                        "Longitude - " + alluser.getLng() +
////                        "Status - " + alluser.getStatus());
////            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void seeNearByFriends() {
//
//        if (lat.length() != 0 && lng.length() != 0) {
//            double userLat = Double.parseDouble(lat);
//            double userLng = Double.parseDouble(lng);
//
//            double venueLat = -1;
//            double venueLng = -1;
//            System.out.println("alluserInfos.size() is " + alluserInfos.size());
//            for (int i = 0; i < alluserInfos.size(); i++) {
//
//                UserInfo f = alluserInfos.get(i);
//
//                System.out.println("f is " + f);
//                System.out.println("lat is " + f.getLat() + " lng is " + f.getLng()); // this is null
//
//                venueLat = Double.parseDouble(f.getLat());
//                venueLng = Double.parseDouble(f.getLng());
//
//                final double AVERAGE_RADIUS_OF_EARTH = 6371;
//
//                double latDistance = Math.toRadians(userLat - venueLat);
//                double lngDistance = Math.toRadians(userLng - venueLng);
//                double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                        + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
//                        * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
//                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//                double distance = (AVERAGE_RADIUS_OF_EARTH * c);
//                if (distance >= radius) {
//                    Log.d("test", "removed from 2");
//                    alluserInfos.remove(f);
//                    allUserUsernames.remove(allUserUsernames.get(i));
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        lat = Double.toString(location.getLatitude());
//        lng = Double.toString(location.getLongitude());
//        System.out.println("LAT " + lat + "LNG : " + lng);
//        if (!busy) {
//            busy = true;
//            new UpdateLocationOnLocationChanged().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//    }
//
//    private class UpdateLocationOnLocationChanged extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPostExecute(Void result) {
//            busy = false;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                mongodb.updateLocation(name, lat, lng);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        startLocationUpdates();
//    }
//
//    protected void startLocationUpdates() {
//        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        mGoogleApiClient.connect();
//    }
//
//    public List<UserInfo> getAllUserInfos() {
//        return alluserInfos;
//    }
//
//    public ArrayList<String> getAllUserUsernames() {
//        return allUserUsernames;
//    }
//}