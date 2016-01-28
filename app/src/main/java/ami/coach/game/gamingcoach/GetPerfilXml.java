package ami.coach.game.gamingcoach;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by César Madrid
 * on 1/4/2016.
 */
@SuppressWarnings("deprecation")
public class GetPerfilXml extends AsyncTask<String, Void, Object[]> {

    String customUrl;
    Context ctx;
    public static final String MyPREFERENCES = "logPreferences" ;
    SharedPreferences sharedpreferences;
    public GetPerfilXml(Context ctx){

        this.ctx=ctx;
        sharedpreferences=ctx.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
    }
    @Override
    protected Object[] doInBackground(String... params) {
        this.customUrl=params[0];

        Object result[] = new Object[6];
        String token = SystemClock.currentThreadTimeMillis()+"";
        try {

            String usuario = params[0];//hario0
            System.out.println(usuario);
            HttpGet uri = new HttpGet("http://steamcommunity.com/id/"+usuario+"?xml=1&token="+token);

            /*Se defina el tiempo de espera maxima para la conexion y obtener el perfil del usuario*/
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
            HttpConnectionParams.setSoTimeout(httpParams, 10*1000);
            /***************************************************************************************/

            DefaultHttpClient client = new DefaultHttpClient(httpParams);
            HttpResponse resp = client.execute(uri);

            /*
            StatusLine status = resp.getStatusLine();

            if (status.getStatusCode() != 200) {
                //Log.d(tag, "HTTP error, invalid server status code: " + resp.getStatusLine());
                //realizar validacion en el logueo.
                //mostrar alerta
                //return;
            }*/

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resp.getEntity().getContent());


            NodeList list = doc.getElementsByTagName("steamID");
            Node node = list.item(0);
            if(node==null){
                result[0]="Error";
                result[1]="Error de logueo";
                result[2]="Usuario '"+usuario+"' no encontrado.\n 1.Ve a http://steamcomunity.com e inicia sesion\n2.Ve a 'Modificar mi perfil'\n3.Asegurate que tu URL personalizado sea el mismo que tu SteamID y vuelve a intentarlo.";
                return result;
            }
            result[0]=node.getTextContent();

            list = doc.getElementsByTagName("onlineState");
            node = list.item(0);
            result[1]=node.getTextContent();
            System.out.println(result[1]);

            list = doc.getElementsByTagName("stateMessage");
            node = list.item(0);
            result[2]=node.getTextContent();

            list = doc.getElementsByTagName("avatarFull");
            node = list.item(0);



            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GamingCoach/avatars");
            File mediaFile = new File(path, customUrl+".jpg");
            String ruta=mediaFile.getAbsolutePath();
            if(!mediaFile.exists()){

                if(path.mkdirs())
                    System.out.println("archivo creado");

                URL url = new URL(node.getTextContent());
                //URL url = new URL("https://www.google.com.ec/logos/doodles/2015/holidays-2015-day-1-6575248619077632-hp.jpg");
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                try {
                    InputStream input = urlConnection.getInputStream();

                    OutputStream os = new FileOutputStream(mediaFile);
                    byte data[] = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        os.write(data, 0, count);
                    }

                    os.close();
                    input.close();

                    System.out.println("archivo: " + mediaFile.getAbsoluteFile());
                }catch (SocketTimeoutException e){
                    System.out.println("Error timeout descargando la imagen de perfil: "+e.getMessage());
                    System.out.println("archivo: " + null);
                    ruta = null;
                }

            }




            result[3]=ruta;


            System.out.println("steamID64");
            list = doc.getElementsByTagName("steamID64");
            node = list.item(0);
            System.out.println(node.getTextContent());
            result[5]=node.getTextContent();

            System.out.println(new Date());


        }catch (UnknownHostException e){
            result[0]="Error";
            result[1]="No se pudo establecer conexion";
            result[2]="Existen problemas con el servidor, pruebe en un momento.";
        }catch(ConnectTimeoutException e){
            result[0]="Error";
            result[1]="El tiempo de conexion ha expirado";
            result[2]="Se ha sobrepasado el tiempo de espera, vuelva a intentar en un momento.";
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("error: "+e.getMessage());
            System.out.println("error: "+e.getClass());
            result[0]="Error";
            result[1]="Error en inicio.";
            result[2]="Ha surgido un error: "+e.getMessage();
        }

        publishProgress();
        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        System.out.println("avanzando");

    }

    @Override
    protected void onPostExecute(Object[] result) {
        if(result[0].equals("Error") ){
            String titulo=result[1]+"";
            String mensaje=result[2]+"";
            if(!isOnline()){
                this.ctx=MainActivity.mainActivity;
                titulo="No se pudo establecer conexion";
                mensaje="Revise su conexion a internet.";
            }

            if(ctx.getClass()==RegistroActivity.class) {
                RegistroActivity reg = (RegistroActivity) ctx;
                if (reg.consultando != null) reg.consultando.dismiss();
            }
            generaDialogo(titulo,mensaje);
            //result=null;
            return;
        }
/*
        if(result==null && ctx!=null){

            generaDialogo("No se encontro Usuario","No se encontro un usuario con ese id.");

            if(ctx.getClass()==RegistroActivity.class) {
                RegistroActivity reg = (RegistroActivity) ctx;
                if (reg.consultando != null) reg.consultando.dismiss();
            }
            return;
        }
*/
        Date date = new Date(System.currentTimeMillis());
        long millis = date.getTime();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(ctx.getClass()==RegistroActivity.class) {
            editor.putString(GamingCoach.Pref.SteamId64, result[5].toString());
            editor.putString(GamingCoach.Pref.SteamId, result[0].toString());
            editor.putString(GamingCoach.Pref.CustomUrl, customUrl);
            editor.putBoolean(GamingCoach.Pref.UserLog, true);
            editor.putFloat(GamingCoach.Pref.TimeNoGame, 0);
            editor.putFloat(GamingCoach.Pref.TimeInGame,0);
        }
        /* Implementacion para Controlar las horas de juego*/
        if(result[1].toString().equalsIgnoreCase("in-game")){
            Float timeOnline = sharedpreferences.getFloat(GamingCoach.Pref.TimeInGame,0);
            timeOnline +=(float)0.5;//se puede ser mas exacto comparando la hora de actualizacion anterior con la actual.
            editor.putFloat(GamingCoach.Pref.TimeInGame,timeOnline);
            editor.putFloat(GamingCoach.Pref.TimeNoGame, 0);
            System.out.println("tiempo en juego");
            System.out.println(timeOnline);
            if(timeOnline==5)
                notificacion(timeOnline);
        }
        else if(sharedpreferences.getFloat(GamingCoach.Pref.TimeInGame,0)!=0){
            Float timeNoGame = sharedpreferences.getFloat(GamingCoach.Pref.TimeNoGame,0);
            timeNoGame += (float)0.5;
            if(timeNoGame >5)
            {
                editor.putFloat(GamingCoach.Pref.TimeNoGame, 0);
                editor.putFloat(GamingCoach.Pref.TimeInGame,0);
            }
            else
                editor.putFloat(GamingCoach.Pref.TimeNoGame, timeNoGame);
            System.out.println("tiempo sin juego");
            System.out.println(timeNoGame);

        }
        /* Fin Implementacion para Controlar las horas de juego*/

        editor.putString(GamingCoach.Pref.OnlineState, result[1].toString());
        editor.putString(GamingCoach.Pref.Avatar, (String)result[3]);
        editor.putString(GamingCoach.Pref.StateMessage, result[2].toString().replace("<br/>",": "));
        editor.putLong(GamingCoach.Pref.updated, millis);
        //editor.commit();
        editor.apply();

        if(MainActivity.mainActivity!=null)
            MainActivity.mainActivity.setInfo();

        GetJuegosXml getJuegosXml = new GetJuegosXml(ctx,sharedpreferences);
        if(ctx.getClass()==RegistroActivity.class) {
            RegistroActivity reg = (RegistroActivity) ctx;
            reg.consultarJuegos(getJuegosXml);
        }
        getJuegosXml.execute(customUrl, result[5].toString());

        //activity.finish();

    }

    @Override
    protected void onCancelled() {
    }

    public void notificacion(float tiempo){
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        //tiempo=0;
        String title="Mucho tiempo de Juego";
        CharSequence content="Ud ha llevado jugando durante "+tiempo+" minutos. \n" +
                "Por su salud considere tomar un tiempo de descanso.";


        Intent intent = new Intent(ctx,MainActivity.class);

        intent.putExtra("title",title);
        intent.putExtra("content",content);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(ctx, iUniqueId, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setTicker("Gaming Coach Notification")
                .setContentTitle("Gaming Coach - " + title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .addAction(R.color.online,
                        ctx.getString(R.string.cerrar_sesion), pIntent)
                .addAction(R.drawable.bg_profile,
                        ctx.getString(R.string.steam_id), pIntent);
                        //.setSound(Uri.parse("uri://sadfasdfasdf.mp3"))

        Notification noti = builder.getNotification();


        noti.flags=Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }

    public void generaDialogo(String titulo,String mensaje){

        if(ctx==null) return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle(titulo);

        alertDialogBuilder
                .setMessage(mensaje)
                .setIcon(android.R.drawable.stat_notify_error)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
}