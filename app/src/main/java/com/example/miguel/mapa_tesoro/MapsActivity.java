package com.example.miguel.mapa_tesoro;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Marker marcador;
    private LatLng latLng;
    private LatLng latLng1;
    private LatLng latLng2;
    private LatLng Castelao;
    private double lat=0.0, lon=0.0;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final String TAG = "gpslog";
    private LocationManager mLocMgr;
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 20; // 20 metro
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 10000; // 10 sg

    private TextView latitud, longitud;



    String cadena = "¿Seguro que quieres irte ya sin terminar? Pss... Como me lo esperaba";

    public static final int INTERVALO = 2000; //2 segundos para salir
    public long tiempoPrimerClick;

    //Declaraciones de distancias entre puntos tanto lideres como puntos de asalto
    private Location locationGPS;
    double distancia1, distancia2, distancia3;
    float metroscerca = 20;
    float metroslejos = 10;


    //boton escaner
    Button btn;

    private final static int codigo=0;
    TextView txtqr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);

        mLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            Log.e(TAG, "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            return;
        } else {
            Log.i(TAG, "Permisos necesarios OK!.");
            // registra el listener para obtener actualizaciones
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIEMPO_ENTRE_UPDATES, MIN_CAMBIO_DISTANCIA_PARA_UPDATES, locListener, Looper.getMainLooper());
        }

        latitud.setText("Latitud");
        longitud.setText("Longitud");

        btn = (Button) findViewById(R.id.bEscaner);
        btn.setEnabled(true);

        txtqr = (TextView) findViewById(R.id.txtqr);





    }

// --------------------------------------------------------------------------------------------------------------


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();

        // Puntero por defecto con permisos de administrador Castelao
        Castelao = new LatLng(42.236525, -8.714207);
        mMap.addMarker(new MarkerOptions()
                .position(Castelao)
                .title("COCHE")
                .snippet("Aparcado en el tejado del Daniel Castelao")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.coche)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Castelao));
        mMap.setOnInfoWindowClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                // Solicitar permisos de usuario
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);


        //Punto 1
        latLng = new LatLng(42.237439526686515, -8.714226186275482);//La Fayette
        int radius = 10;

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle = mMap.addCircle(circleOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Morty Cuerpo Enano")
                .snippet("Creador: Rick Sánchez - Universo SD-45a")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mortymapa1))
        );
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Punto 2
        latLng1 = new LatLng(42.237706320945556, -8.715687990188599);//GaliPizza
        int radius1 = 10;

        CircleOptions circleOptions1 = new CircleOptions()
                .center(latLng1)
                .radius(radius1)
                .strokeColor(Color.parseColor("#FF0000"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle1 = mMap.addCircle(circleOptions1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Morty Doble Cara")
                .snippet("Creador: Rick Sánchez - Universo GF-L8p")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mortymapa2))
        );
        mMap.setOnInfoWindowClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));

        //Punto 3
        latLng2 = new LatLng(42.238956026405795, -8.71614396572113);//Parada Bus Arenal
        int radius2 = 10;

        CircleOptions circleOptions2 = new CircleOptions()
                .center(latLng2)
                .radius(radius2)
                .strokeColor(Color.parseColor("#3ADF00"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle2 = mMap.addCircle(circleOptions2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 17));

        mMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .title("Morty Insecto")
                .snippet("Creador: Rick Sánchez - Universo PJ-2Y7")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mortymapa3))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
        mMap.setOnInfoWindowClickListener(this);


        btn.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent qr = new Intent(MapsActivity.this, QRActivity.class);
                startActivityForResult(qr,codigo);
            }
        });

    }
// --------------------------------------------------------------------------------------------------------------

    //Ventana de información
    @Override
    public void onInfoWindowClick(Marker marker) {

    }

// --------------------------------------------------------------------------------------------------------------

    //Posicion Actual del Usuario (conectarse vía GPS)
    private void localizacionActual(double lat, double lon){
        LatLng coordenadas= new LatLng(lat,lon);
        CameraUpdate miUbi= CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if(marcador!=null)marcador.remove();
        marcador=mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Tu posicion actual")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.rick2))
                );
        mMap.animateCamera(miUbi);
    }

    private void actualizarUbicacion(Location localitation){
        if(localitation!=null){
            lat=localitation.getLatitude();
            lon=localitation.getLongitude();
            localizacionActual(lat,lon);
        }
    }

// --------------------------------------------------------------------------------------------------------------
    //Distancias entre tu posicion y la posicion de los reinos a conquistar
    private void distanciaMorty1(Location localitation) {
        double latim1 = 42.237439;
        double longim1 = -8.714226;
        Location locationmorty1 = new Location("Morty Cuerpo Enano");
        locationmorty1.setLatitude(latim1);
        locationmorty1.setLongitude(longim1);
        distancia1 = localitation.distanceTo(locationmorty1);

        if(localitation.distanceTo(locationmorty1)<=metroscerca){

            btn.setEnabled(true);

        }else if(localitation.distanceTo(locationmorty1)>=metroslejos){
            btn.setEnabled(false);

        }
    }


    private void distanciaMorty2(Location localitation) {
        double latim2 = 42.237706;
        double longim2 = -8.715687;
        Location locationmorty2 = new Location("Morty Doble Cara");
        locationmorty2.setLatitude(latim2);
        locationmorty2.setLongitude(longim2);
        distancia2 = localitation.distanceTo(locationmorty2);

        if(localitation.distanceTo(locationmorty2)<=metroscerca){

            btn.setEnabled(true);

        }else if(localitation.distanceTo(locationmorty2)>=metroslejos){
            btn.setEnabled(false);

        }
    }

    private void distanciaMorty3(Location localitation){
        double latim3 = 42.238956;
        double longim3 = -8.716143;
        Location locationmorty3 = new Location("Morty Insecto");
        locationmorty3.setLatitude(latim3);
        locationmorty3.setLongitude(longim3);
        distancia3 = localitation.distanceTo(locationmorty3);

        if(localitation.distanceTo(locationmorty3)<=metroscerca){

            btn.setEnabled(true);

        }else if(localitation.distanceTo(locationmorty3)>=metroslejos){
            btn.setEnabled(false);

        }
    }

// --------------------------------------------------------------------------------------------------------------


    LocationListener locListener = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
            distanciaMorty1(location);
            distanciaMorty2(location);
            distanciaMorty3(location);
            Log.i(TAG, "Lat " + location.getLatitude() + " Long " + location.getLongitude());
            Log.i(TAG, "Lat " + location.getLatitude() + " Long " + location.getLongitude());
            latitud.setText("Latitud "+lat);
            longitud.setText("Longitud "+lon);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i(TAG, "onProviderDisabled()");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i(TAG, "onProviderDisabled()");
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i(TAG, "onProviderDisabled()");
        }

    };

    private void miUbicacion(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener);

    }

// --------------------------------------------------------------------------------------------------------------
//al salir dando doble click
    public void onBackPressed(){

        //se prepara la alerta creando nueva instancia
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        alertbox.setMessage(cadena);

        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            alertbox.setMessage(cadena);
            //elegimos un positivo SI y creamos un Listener
            alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent salida=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
                    finishAffinity();
                }
            });

            //elegimos un positivo NO y creamos un Listener
            alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        //mostramos el alertbox
        alertbox.show();
    }

// --------------------------------------------------------------------------------------------------------------

//resultados del codigo QR
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == codigo) {
                txtqr.setText(data.getExtras().getString("retorno"));

            }
        }
    }
}

