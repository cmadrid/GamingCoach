package ami.coach.game.gamingcoach;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by ces_m on 12/22/2015.
 */
public class RegistroActivity extends AppCompatActivity {

    ImageView boton_log;
    EditText id;
    Context ctx;
    Activity activity;
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
                    GetInfoXml a = new GetInfoXml();
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


    public class GetInfoXml extends AsyncTask<String, Void, Object[]> {

        @Override
        protected Object[] doInBackground(String... params) {

            Object result[] = new Object[6];
            try {

                String usuario = params[0];//hario0
                System.out.println(usuario);
                HttpGet uri = new HttpGet("http://steamcommunity.com/id/"+usuario+"?xml=1");

                DefaultHttpClient client = new DefaultHttpClient();
                HttpResponse resp = client.execute(uri);

                StatusLine status = resp.getStatusLine();
                if (status.getStatusCode() != 200) {
                    //Log.d(tag, "HTTP error, invalid server status code: " + resp.getStatusLine());
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(resp.getEntity().getContent());


                NodeList list = doc.getElementsByTagName("steamID");
                Node node = list.item(0);
                result[0]=node.getTextContent();

                list = doc.getElementsByTagName("onlineState");
                node = list.item(0);
                result[1]=node.getTextContent();

                list = doc.getElementsByTagName("stateMessage");
                node = list.item(0);
                result[2]=node.getTextContent();

                list = doc.getElementsByTagName("avatarFull");
                node = list.item(0);

                //URL url = new URL(node.getTextContent());
                URL url = new URL("https://www.google.com.ec/logos/doodles/2015/holidays-2015-day-1-6575248619077632-hp.jpg");
                InputStream input = url.openConnection().getInputStream();


                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GamingCoach/avatars");
                File mediaFile = new File(path, customUrl+".jpg");
                path.mkdirs();

                OutputStream os = new FileOutputStream(mediaFile);
                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    os.write(data, 0, count);
                }

                if (os != null)
                    os.close();
                if (input != null)
                    input.close();


                result[3]=mediaFile.getAbsoluteFile();
                System.out.println("archivo: "+mediaFile.getAbsoluteFile());




                System.out.println("steamID64");
                list = doc.getElementsByTagName("steamID64");
                node = list.item(0);
                System.out.println(node.getTextContent());
                result[5]=node.getTextContent();



            }catch (Exception e){
                System.out.println("error: "+e.getMessage());

                result=null;
            }







            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Object[] result) {

            if(result==null){

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setTitle("No se encontro Usuario");

                alertDialogBuilder
                        .setMessage("No se encontro un usuario con ese id.")
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                if(consultando!=null)consultando.dismiss();

                return;
            }

            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);


            Date date = new Date(System.currentTimeMillis());
            long millis = date.getTime();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Prefs.SteamId64.name(), result[5].toString());
            editor.putString(Prefs.SteamId.name(), result[0].toString());
            editor.putString(Prefs.OnlineState.name(), result[1].toString());
            editor.putString(Prefs.StateMessage.name(), result[2].toString());
            editor.putString(Prefs.Avatar.name(), result[3].toString());
            editor.putLong(Prefs.updated.name(),millis);
            editor.putString(Prefs.CustomUrl.name(), customUrl);
            editor.putBoolean(Prefs.UserLog.name(), true);
            editor.commit();
            activity.finish();

        }

        @Override
        protected void onCancelled() {
        }
    }


}
