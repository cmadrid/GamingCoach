package ami.coach.game.gamingcoach;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;

import ami.coach.game.gamingcoach.database.DBJuego;
import ami.coach.game.gamingcoach.database.DBSesiones;

/**
 * Created by César Madrid
 * on 12/22/2015.
 */
public class RegistroActivity extends Activity {

    ImageView boton_log;
    EditText id;
    Context ctx;
    RegistroActivity activity;
    ProgressDialog consultando;
    //private SharedPreferences sharedpreferences;
    private String customUrl=null;
/*
    public static final String MyPREFERENCES = "logPreferences" ;
    public static enum Prefs {
        UserLog,SteamId64,SteamId,CustomUrl,OnlineState,StateMessage,Avatar,updated,TimeNoGame,TimeInGame
    }*/
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedpreferences=getSharedPreferences(GamingCoach.Pref.MyPREFERENCES,Context.MODE_PRIVATE);

        if (sharedpreferences.contains(GamingCoach.Pref.UserLog))
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();

        }
        else if(sharedpreferences.contains(GamingCoach.Pref.CustomUrl)){
            customUrl=sharedpreferences.getString(GamingCoach.Pref.CustomUrl, null);
            id.setText(customUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.registro_layout);


        ctx = getApplicationContext();
        activity=this;




        boton_log = (ImageView)findViewById(R.id.btnLog);
        id = (EditText)findViewById(R.id.id_steam);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        boton_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().length() == 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setTitle("Datos incompletos");

                    alertDialogBuilder
                            .setMessage("Debe ingresar un usuario de steam.")
                            .setIcon(android.R.drawable.stat_notify_error)
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                } else {
                    customUrl = id.getText().toString();
                    GetPerfilXml a = new GetPerfilXml(activity);
                    consultar(a);
                    a.execute(customUrl);

                }
            }
        });

    }


    public void consultar(final AsyncTask hilo){
        if(consultando!=null)consultando.dismiss();
        consultando = new ProgressDialog(activity);
        consultando.setTitle("Consultando...");
        consultando.setMessage("Espere mientras se consulta...");
        consultando.setCanceledOnTouchOutside(false);
        consultando.setCancelable(true);
        consultando.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which){dialog.dismiss();hilo.cancel(true);}
        });
        consultando.show();
    }

    public void consultarJuegos(final AsyncTask hilo){
        if(consultando!=null)consultando.dismiss();
        consultando = new ProgressDialog(activity);
        consultando.setTitle("Consultando lista de juegos...");
        consultando.setMessage("Espere mientras se consulta los juegos...");
        consultando.setCanceledOnTouchOutside(false);
        consultando.setCancelable(true);
        consultando.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                hilo.cancel(true);
            }
        });
        consultando.show();
    }


    public void procesoFinal() {
        //if(consultando!=null)consultando.dismiss();
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        llenarSesiones();
        activity.finish();
    }

    public void llenarSesiones(){
        //id juegos
        DBSesiones dbSesiones=new DBSesiones(ctx);
        DBJuego dbJuego = new DBJuego(ctx);
        String id1=null;
        String id2=null;
        try {
            Cursor c = dbJuego.consultar(null);
            if(c.moveToFirst()){
                id1=c.getString(0);
                if(c.moveToNext())
                    id2=c.getString(0);
            }
        /*
        String id3="381990";
        String id4="397040";
        String id5="420880";
        String id6="422630";
        String id7="423880";
*/
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -5);
            //calendar.add(Calendar.DAY_OF_MONTH, -5);
            if(id1!=null){
                dbSesiones.insertar(id1, 35 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                //dbSesiones.insertar(id1, 0 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id1, 10 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                //dbSesiones.insertar(id1, 0 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                //dbSesiones.insertar(id1, 0 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id1, 5 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id1, 115 + "", calendar.getTime());

                calendar.add(Calendar.DAY_OF_MONTH, 6);
            }
            if(id2!=null){
                //dbSesiones.insertar(id2, 0 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id2, 35 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id2, 20 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                //dbSesiones.insertar(id2, 0 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id2, 10 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id2, 110 + "", calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dbSesiones.insertar(id2, 20 + "", calendar.getTime());

                calendar.add(Calendar.DAY_OF_MONTH, 6);
            }



            calendar.add(Calendar.MONTH, -1);//dic
            //dbSesiones.insertar(id1, 110 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//nov
            //dbSesiones.insertar(id1, 95 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//oct
            //dbSesiones.insertar(id1, 40 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//sep
            dbSesiones.insertar(id1, 85 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//ago
            dbSesiones.insertar(id1, 50 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//jul
            dbSesiones.insertar(id1, 84 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//jun
            dbSesiones.insertar(id1, 112 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//may
            dbSesiones.insertar(id1, 140 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//abr
            //dbSesiones.insertar(id1, 9 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//marz
            dbSesiones.insertar(id1, 50 + "", calendar.getTime());
            calendar.add(Calendar.MONTH, -1);//feb
            dbSesiones.insertar(id1, 35 + "", calendar.getTime());



            dbSesiones.setActualizacionInicio();

        }catch (Exception e ){
            System.out.println("error creando base de sesiones automatica");
        }finally {
            dbSesiones.close();
        }
    }
}
