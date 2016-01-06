package ami.coach.game.gamingcoach;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by ces_m on 12/6/2015.
 */
public class ServiceBackground extends Service {
    Timer timer;
    private String title="";
    private String content="";
    public static final String MyPREFERENCES = "logPreferences" ;
    SharedPreferences sharedpreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(timer==null) {
            timer = new Timer();
            timer.execute();
            Toast.makeText(ServiceBackground.this,"Gaming Coach ON!",Toast.LENGTH_SHORT).show();
        }
        System.out.println(timer.getStatus());
        return START_STICKY;//averiguar bien
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel(true);
        Toast.makeText(this,"Gaming Coach OFF",Toast.LENGTH_SHORT).show();
    }


    public class Timer extends AsyncTask<String, Void, String> {

        HashMap<String,Integer> minutesMap = new HashMap<String,Integer>();
        int segundos=1000;
        int minutos=60000;
        String customUrl = sharedpreferences.getString(RegistroActivity.Prefs.CustomUrl.name(), "");
        @Override
        protected String doInBackground(String... params) {
            while(true)
            {
                if(isCancelled())break;
                publishProgress();
                //SystemClock.sleep(5 * minutos);//minutos
                SystemClock.sleep(10 * segundos);//segundos
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            System.out.println("progreso del servicio!!!");
            /*System.out.println("el proceso corre bien..s.");
            GetPerfilXml getPerfilXml = new GetPerfilXml(getApplicationContext());
            getPerfilXml.execute(customUrl);*/
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            timer = null;
        }
    }

}
