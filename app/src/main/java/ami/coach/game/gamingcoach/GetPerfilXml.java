package ami.coach.game.gamingcoach;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;

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
 * Created by ces_m on 1/4/2016.
 */
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
            System.out.println(result[1]);

            list = doc.getElementsByTagName("stateMessage");
            node = list.item(0);
            result[2]=node.getTextContent();

            list = doc.getElementsByTagName("avatarFull");
            node = list.item(0);



            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GamingCoach/avatars");
            File mediaFile = new File(path, customUrl+".jpg");
            if(!mediaFile.exists()){

                path.mkdirs();

                URL url = new URL(node.getTextContent());
                //URL url = new URL("https://www.google.com.ec/logos/doodles/2015/holidays-2015-day-1-6575248619077632-hp.jpg");
                InputStream input = url.openConnection().getInputStream();

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

            }



            result[3]=mediaFile.getAbsoluteFile();
            System.out.println("archivo: " + mediaFile.getAbsoluteFile());



            System.out.println("steamID64");
            list = doc.getElementsByTagName("steamID64");
            node = list.item(0);
            System.out.println(node.getTextContent());
            result[5]=node.getTextContent();

            System.out.println(new Date());


        }catch (Exception e){
            System.out.println("error: "+e.getMessage());

            result=null;
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

        if(result==null){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
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


            if(ctx.getClass()==RegistroActivity.class) {
                RegistroActivity reg = (RegistroActivity) ctx;
                if (reg.consultando != null) reg.consultando.dismiss();
            }
            return;
        }

        Date date = new Date(System.currentTimeMillis());
        long millis = date.getTime();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(ctx.getClass()==RegistroActivity.class) {
            editor.putString(RegistroActivity.Prefs.SteamId64.name(), result[5].toString());
            editor.putString(RegistroActivity.Prefs.SteamId.name(), result[0].toString());
            editor.putString(RegistroActivity.Prefs.Avatar.name(), result[3].toString());
            editor.putString(RegistroActivity.Prefs.CustomUrl.name(), customUrl);
            editor.putBoolean(RegistroActivity.Prefs.UserLog.name(), true);
        }
        editor.putString(RegistroActivity.Prefs.OnlineState.name(), result[1].toString());
        editor.putString(RegistroActivity.Prefs.StateMessage.name(), result[2].toString().replace("<br/>",": "));
        editor.putLong(RegistroActivity.Prefs.updated.name(), millis);
        editor.commit();

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
}