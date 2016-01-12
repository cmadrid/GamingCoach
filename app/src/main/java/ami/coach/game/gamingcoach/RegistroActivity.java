package ami.coach.game.gamingcoach;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;

import ami.coach.game.gamingcoach.database.DBSesiones;

/**
 * Created by ces_m on 12/22/2015.
 */
public class RegistroActivity extends AppCompatActivity{

    ImageView boton_log;
    EditText id;
    Context ctx;
    RegistroActivity activity;
    ProgressDialog consultando;
    public static final String MyPREFERENCES = "logPreferences" ;
    private SharedPreferences sharedpreferences;
    private String customUrl=null;

    public static enum Prefs {
        UserLog,SteamId64,SteamId,CustomUrl,OnlineState,StateMessage,Avatar,updated
    }
    @Override
    protected void onResume() {
        super.onResume();

        sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Prefs.UserLog.name()))
        {
            sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();

        }
        else if(sharedpreferences.contains(Prefs.CustomUrl.name())){
            customUrl=sharedpreferences.getString(Prefs.CustomUrl.name(), null);
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
        String id1="314000";
        String id2="356330";
        String id3="381990";
        String id4="397040";
        String id5="420880";
        String id6="422630";
        String id7="423880";

        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_MONTH, -5);
        DBSesiones dbSesiones=new DBSesiones(ctx);

        dbSesiones.insertar(id1, 35 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id1, 0 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id1, 10 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id1, 0 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id1, 0 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id1, 5 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id1, 115 + "", calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 6);
        dbSesiones.insertar(id2, 0 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id2, 35 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id2, 20 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id2, 0 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id2, 10 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id2, 110 + "", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dbSesiones.insertar(id2, 20 + "", calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 6);

        dbSesiones.close();
    }
}
