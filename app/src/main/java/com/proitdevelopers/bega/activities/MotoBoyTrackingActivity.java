package com.proitdevelopers.bega.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.api.ApiInterfaceEncomenda;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.utilsClasses.DirectionJSONParser;

import org.java_websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

public class MotoBoyTrackingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final String TAG = "MotoBoyTrackingActivity";
    SupportMapFragment mapFragment;
    private GoogleMap mMap;

    String riderLat,riderLng;

    String idFactura,tokenEncomenda;

    //Play Services

    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    private Circle riderMarker;
    private Marker driverMarker,pacientMarker,motoboyMarker;

    private Polyline direction;

    ApiInterfaceEncomenda mService;
//    IFCMService mFCMService;



    Location pickupLocation;

    String toolbarTitle;

    private static final String SERVER_URL_WEBSOCKET ="ws://35.181.153.234:8086/api-entrega";

    Uri uri = Uri.parse(SERVER_URL_WEBSOCKET);
    private StompClient mStompClient;
    List<StompHeader> headers = new ArrayList<>();

    String telefoneEstafeta,estadoEncomendaEstafeta;
    String latitudeAtualEstafeta,longitudeAtualEstafeta;
    String latitudeDestinoEstafeta,longitudeDestinoEstafeta;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto_boy_tracking);

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (getIntent()!= null){
            toolbarTitle = getIntent().getStringExtra("toolbarTitle");
//            riderLat = getIntent().getStringExtra("lat");
//            riderLng = getIntent().getStringExtra("lng");
            idFactura = String.valueOf(getIntent().getIntExtra("idFactura",0));
            tokenEncomenda = getIntent().getStringExtra("tokenEncomenda");
        }

        if (toolbarTitle==null){
            toolbarTitle = "Mapa";
        }
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

//        riderLat = String.valueOf(-8.825867897434245);
//        riderLng = String.valueOf(13.229186380831504);


        mService = getClientEncomenda().create(ApiInterfaceEncomenda.class);;

//        setUpLocation();

        headers.add((new StompHeader("X-Authorization",tokenEncomenda)));
        mStompClient = Stomp.over(WebSocket.class, uri.toString());


        verifConecxao();

    }

    private void verifConecxao() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                mostrarMensagem(this,R.string.txtMsg);
            } else {

                setUpLocation();
            }
        }
    }

    private void startStompClient() {

        if (mStompClient.isConnected()){
            mStompClient.disconnect();
        }

        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LifecycleEvent>() {
                    @Override
                    public void call(final LifecycleEvent lifecycleEvent) {
                        switch (lifecycleEvent.getType()) {

                            case OPENED:
                                Log.e(TAG, "Stomp connection OPENED");
                                
                                enviarStompClient(idFactura);
                                break;

                            case ERROR:
                                Log.e(TAG, "Stomp connection ERROR", lifecycleEvent.getException());
                                break;

                            case CLOSED:
                                Log.e(TAG, "Stomp connection CLOSED");
                                mStompClient.disconnect();
                                break;

                        }
                    }


                });



        mStompClient.topic("/user/topic/encomenda")
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StompMessage>() {

                    @Override
                    public void onNext(StompMessage stompMessage) {

                        Log.e(TAG, "/topic/online/onNext: " + stompMessage.getPayload());

                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(stompMessage.getPayload());



                            latitudeAtualEstafeta = String.valueOf(jsonResponse.getString("latitudeAtual"));
                            longitudeAtualEstafeta = String.valueOf(jsonResponse.getString("longitudeAtual"));
                            latitudeDestinoEstafeta = String.valueOf(jsonResponse.getString("latitudeDestino"));
                            longitudeDestinoEstafeta = String.valueOf(jsonResponse.getString("longitudeDestino"));

                            telefoneEstafeta = String.valueOf(jsonResponse.getString("telefoneEstafeta"));
                            estadoEncomendaEstafeta = String.valueOf(jsonResponse.getString("estadoEncomenda"));

                            riderLat = latitudeDestinoEstafeta;
                            riderLng = longitudeDestinoEstafeta;


//                            {
//                                "idCliente":"47",
//                                    "idFatura":"512",
//                                    "idLoja":"1",
//                                    "nomeCliente":"Adao Gaspar BARTOLOmeu",
//                                    "telefoneEstafeta":"916774313",
//                                    "latitudeOrigem":-8.8249116,
//                                    "longitudeOrigem":13.235682,
//                                    "longitudeDestino":13.229186380831504,
//                                    "latitudeDestino":-8.825867897434245,
//                                    "latitudeAtual":-8.8275547,
//                                    "longitudeAtual":13.2288956,
//                                    "estadoEncomenda":"ANDAMENTO"}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "/topic/online/onError: " + e.getMessage());

                    }

                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "/topic/online/onCompleted: ");
                    }



                });



        mStompClient.connect(headers);




    }

    private void enviarStompClient(String idFactura) {


        mStompClient.send("/app/tempo/real/encomenda/"+idFactura)
                .subscribeOn(Schedulers.io()).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "/app/tempo/real/encomenda/{idFactura}: " + "Complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "/app/tempo/real/encomenda/{idFactura}: " + e.getMessage());
            }

            @Override
            public void onNext(Void message) {
                Log.e(TAG, "/app/tempo/real/encomenda/{idFactura}: " + message);
            }


        });

        Log.e(TAG, "/app/tempo/real/encomenda/{idFactura}: " + idFactura);


    }

    private void setUpLocation() {

        if (checkPlayServices()){

            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();

        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }

        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

//        try {
//
//            boolean isSucess = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map)
//            );
//
//            if (!isSucess)
//                Log.e("ERROR", "Map style load failed !!!");
//
//        } catch (Resources.NotFoundException ex) {
//            ex.printStackTrace();
//        }


        mMap = googleMap;

//        startStompClient();

        if (mStompClient.isConnected()){
            mStompClient.disconnect();
        }

        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LifecycleEvent>() {
                    @Override
                    public void call(final LifecycleEvent lifecycleEvent) {
                        switch (lifecycleEvent.getType()) {

                            case OPENED:
                                Log.e(TAG, "Stomp connection OPENED");

                                enviarStompClient(idFactura);
                                break;

                            case ERROR:
                                Log.e(TAG, "Stomp connection ERROR", lifecycleEvent.getException());
                                break;

                            case CLOSED:
                                Log.e(TAG, "Stomp connection CLOSED");
                                mStompClient.disconnect();
                                break;

                        }
                    }


                });



        mStompClient.topic("/user/topic/encomenda")
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StompMessage>() {

                    @Override
                    public void onNext(StompMessage stompMessage) {

                        Log.e(TAG, "/topic/online/onNext: " + stompMessage.getPayload());

                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(stompMessage.getPayload());



                            latitudeAtualEstafeta = String.valueOf(jsonResponse.getString("latitudeAtual"));
                            longitudeAtualEstafeta = String.valueOf(jsonResponse.getString("longitudeAtual"));
                            latitudeDestinoEstafeta = String.valueOf(jsonResponse.getString("latitudeDestino"));
                            longitudeDestinoEstafeta = String.valueOf(jsonResponse.getString("longitudeDestino"));

                            telefoneEstafeta = String.valueOf(jsonResponse.getString("telefoneEstafeta"));
                            estadoEncomendaEstafeta = String.valueOf(jsonResponse.getString("estadoEncomenda"));

                            riderLat = latitudeDestinoEstafeta;
                            riderLng = longitudeDestinoEstafeta;


//                            if (pacientMarker != null)
//                                pacientMarker.remove();
                            pacientMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(riderLat),Double.parseDouble(riderLng)))
                                    .title("Paciente")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker)));


                            riderMarker = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(Double.parseDouble(riderLat),Double.parseDouble(riderLng)))
                                    .radius(50) // 50 => radius is 50m
                                    .strokeColor(Color.BLUE)
                                    .fillColor(0x220000FF)
                                    .strokeWidth(5.0f));

//                            if (motoboyMarker != null)
//                                motoboyMarker.remove();
                            motoboyMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitudeAtualEstafeta),Double.parseDouble(longitudeAtualEstafeta)))
                                    .title("MotoBoy")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motoboy_marker)));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(Double.parseDouble(riderLat),Double.parseDouble(riderLng)),14.0f));

                            if(direction != null)
                                direction.remove(); //remove old direction
                            getDirection();


//                            {
//                                "idCliente":"47",
//                                    "idFatura":"512",
//                                    "idLoja":"1",
//                                    "nomeCliente":"Adao Gaspar BARTOLOmeu",
//                                    "telefoneEstafeta":"916774313",
//                                    "latitudeOrigem":-8.8249116,
//                                    "longitudeOrigem":13.235682,
//                                    "longitudeDestino":13.229186380831504,
//                                    "latitudeDestino":-8.825867897434245,
//                                    "latitudeAtual":-8.8275547,
//                                    "longitudeAtual":13.2288956,
//                                    "estadoEncomenda":"ANDAMENTO"}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "/topic/online/onError: " + e.getMessage());

                    }

                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "/topic/online/onCompleted: ");
                    }



                });



        mStompClient.connect(headers);






    }

    private void sendArrivedNotification(String customerId) {
//        Token token = new Token(customerId);
//        //We will send notification with title is Arrived and body is
//
////        Notification notification = new Notification("Arrived",
////                String.format("The driver %s has arrived at your location",
////                        Common.currentUser.getName()));
////        Sender sender = new Sender(token.getToken(), notification);
//
//        Map<String,String> content = new HashMap<>();
//        content.put("title", "Arrived");
//        content.put("message", String.format("The doctor %s has arrived at your location",
//                Common.currentUser.getNome()));
//        DataMessage dataMessage = new DataMessage(token.getToken(),content);
//
//
//
//        mFCMService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
//            @Override
//            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                if (response.body().success!=1){
//                    Toast.makeText(DriverTracking.this,
//                            "Failed", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//            }
//        });
    }

    private void sendDropOffNotification(String customerId) {
//        Token token = new Token(customerId);
//        //We will send notification with title is Arrived and body is
////        Notification notification = new Notification("DropOff",
////               customerId);
////        Sender sender = new Sender(token.getToken(), notification);
//
//        Map<String,String> content = new HashMap<>();
//        content.put("title", "DropOff");
//        content.put("message", customerId);
//        DataMessage dataMessage = new DataMessage(token.getToken(),content);
//
//        mFCMService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
//            @Override
//            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                if (response.body().success!=1){
//                    Toast.makeText(DriverTracking.this,
//                            "Failed", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//            }
//        });
    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (Common.mLastLocation != null) {


            final double latitude = Common.mLastLocation.getLatitude();
            final double longitude = Common.mLastLocation.getLongitude();

            if (driverMarker != null)
                driverMarker.remove();
            driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude,longitude))
                    .title("You")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));



//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(latitude,longitude),14.0f));




        } else {
            Log.d("Error", "Cannot get your location");
        }

    }

    private void getDirection() {

//        LatLng currentPosition = new LatLng(Common.mLastLocation.getLatitude(), Common.mLastLocation.getLongitude());
        LatLng currentPositionMotoboy = new LatLng(Double.parseDouble(latitudeAtualEstafeta),Double.parseDouble(longitudeAtualEstafeta));

        String requestApi = null;
        try {

//            "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+


            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+currentPositionMotoboy.latitude+","+currentPositionMotoboy.longitude+"&"+
                    "destination="+riderLat+","+riderLng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);
            Log.d("EDMTDEV", requestApi); // Print URL for debug
            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {

                                new ParserTask().execute(response.body().toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(MotoBoyTrackingActivity.this, ""+t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this
        );
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
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
    public void onLocationChanged(Location location) {
        Common.mLastLocation = location;
        displayLocation();
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {

        ProgressDialog mDialog = new ProgressDialog(MotoBoyTrackingActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Carregando...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i=0; i<lists.size(); i++) {

                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for (int j=0; j<path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);

                    points.add(position);

                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

            }
            direction = mMap.addPolyline(polylineOptions);



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onDestroy() {
        mStompClient.disconnect();

        super.onDestroy();
    }


    public Retrofit getClientEncomenda() {

        Retrofit retrofit;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit;
    }

}
