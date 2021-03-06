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
            this.activity=this;
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
        //llenarSesiones();
        activity.finish();
    }


}
