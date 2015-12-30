package ami.coach.game.gamingcoach;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        if(timer==null) {
            timer = new Timer();
            //timer.execute();
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

        String onlineState, stateMessage;
        HashMap<String,Integer> minutesMap = new HashMap<String,Integer>();
        @Override
        protected String doInBackground(String... params) {
            while(true)
            {
                if(isCancelled())break;
                getOnlineState();
                getGamingTime();
                publishProgress();
                SystemClock.sleep(300 * 1000);
            }
            return null;
        }

        public void getOnlineState(){
            try {
                String customUrl = MainActivity.sharedpreferences.getString(RegistroActivity.Prefs.CustomUrl.name(), "");
                HttpGet uri = new HttpGet("http://steamcommunity.com/id/" + customUrl + "?xml=1");
                DefaultHttpClient client = new DefaultHttpClient();
                HttpResponse resp = client.execute(uri);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(resp.getEntity().getContent());

                NodeList list = doc.getElementsByTagName("onlineState");
                Node node =  list.item(0);
                onlineState = node.getTextContent();

                list = doc.getElementsByTagName("stateMessage");
                node = list.item(0);
                stateMessage = node.getTextContent();


            }catch(Exception e){
                System.out.println(e.getMessage());
            }


        }

        public void getGamingTime(){
            try{
                String steamId64 = MainActivity.sharedpreferences.getString(RegistroActivity.Prefs.SteamId64.name(), "");
                HttpGet uri2 = new HttpGet("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=8CA1864E6DDD065C53651CF9F88404B2&steamid="+steamId64+"&format=xml");
                DefaultHttpClient client2 = new DefaultHttpClient();
                HttpResponse resp2 = client2.execute(uri2);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(resp2.getEntity().getContent());

                NodeList list = doc.getElementsByTagName("message");
                String id;
                int minutos=0;
                for (int i = 0; i < list.getLength(); i++){
                    Element currentGame = (Element)list.item(i);
                    id = currentGame.getElementsByTagName("appid").item(0).getTextContent();
                    minutos = Integer.parseInt(currentGame.getElementsByTagName("playtime_forever").item(0).getTextContent());
                    minutesMap.put(id,minutos);
                }

            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            GetJuegosXml getJuegosXml = new GetJuegosXml();
            getJuegosXml.activity=getApplicationContext();
            //necesita customURL y SteamID64
            getJuegosXml.execute();

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
