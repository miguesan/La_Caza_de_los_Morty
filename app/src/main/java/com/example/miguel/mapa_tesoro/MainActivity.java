package com.example.miguel.mapa_tesoro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String cadena = "¿Estas seguro de que quieres dejar la caza y escapar de tu deber como una Gallina?";
    String instru = "La Caza de los Morty´s:\n \n"+
                    "1. Deverás llegar a la ubicación del mapa\n"+
                    "2. Una vez allí buscar el codigo QR y darle al boton escanear\n"+
                    "3. Escanea el codigo QR. Cuando lo hagas ve a la siguiente posicion\n"+
                    "4. Cuando tengas los 3 cazados ganaras\n \n"+
                    "** IMPORTANTE **\n"+
                    "Tienes tiempo para hacer los puntos, asique mas te vale ser rapido.\n"+
                    "Si eres una tortuga empezaras de nuevo\n \n"+
                    "- Dale al botón de 'Comenzar Caza' cuando estes listo\n"+
                    "- O si eres un gallina dale al botón de 'Salir' o 'Atras' en tu movil\n";

    public static final int INTERVALO = 2000; //2 segundos para salir
    public long tiempoPrimerClick;

    //para la musica
    private MediaPlayer musicafondo;
    int MAX_VOLUME = 100;
    int soundVolume = 90;
    float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.bComenzar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), MapsActivity.class);
                musicafondo.stop(); //para la musica de fondo
                startActivityForResult(intent, 0);
            }
        });

        //musica de fondo para la app
        musicafondo = MediaPlayer.create(this, R.raw.musica);
        musicafondo.setLooping(true);
        musicafondo.setVolume(volume, volume);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                musicafondo.start();
            }
        }, 1000);
    }

    public void instrucciones (View view){ //para salir de la aplicacion con un dialogo de confirmacion

        //se prepara la alerta creando nueva instancia
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        alertbox.setMessage(instru);
        //elegimos un positivo Ok y creamos un Listener
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent okey=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
                closeContextMenu();
            }
        });
        //mostramos el alertbox
        alertbox.show();

    }

    public void salir (View view){ //para salir de la aplicacion con un dialogo de confirmacion

        //se prepara la alerta creando nueva instancia
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        alertbox.setMessage(cadena);
        //elegimos un positivo SI y creamos un Listener
        alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent salida=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
                musicafondo.stop(); //para la musica de fondo
                finishAffinity();
            }
        });

        //elegimos un positivo NO y creamos un Listener
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //mostramos el alertbox
        alertbox.show();

    }

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
                    musicafondo.stop(); //para la musica de fondo
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
}
