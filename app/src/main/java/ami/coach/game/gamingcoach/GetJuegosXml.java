package ami.coach.game.gamingcoach;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ami.coach.game.gamingcoach.database.DBJuego;

/**
 * Created by Joseph_Gallardo
 * on 29/12/2015.
 */
public class GetJuegosXml extends AsyncTask<String,Void,Object[]> {

    HashMap<String,Juego> listaJuegos = new HashMap<>();
    Context activity;
    SharedPreferences sharedPreferences;

    public GetJuegosXml(Context ctx, SharedPreferences sharedPreferences){
        activity=ctx;
        this.sharedPreferences = sharedPreferences;
    }
    @Override
    protected Object[] doInBackground(String... params) {

        String token = SystemClock.currentThreadTimeMillis()+"";
        try{
            String idUsuario = params[0];
            String id64 = params[1];
            HttpGet uri1 = new HttpGet("http://steamcommunity.com/id/"+idUsuario+"/games?tab=all&xml=1&token="+token);
            HttpGet uri2 = new HttpGet("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=8CA1864E6DDD065C53651CF9F88404B2&steamid="+id64+"&format=xml&token="+token);


            DefaultHttpClient client1 = new DefaultHttpClient();
            HttpResponse resp1 = client1.execute(uri1);

            DefaultHttpClient client2 = new DefaultHttpClient();
            HttpResponse resp2 = client2.execute(uri2);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resp1.getEntity().getContent());

            NodeList list = doc.getElementsByTagName("game");


            for (int i = 0; i < list.getLength(); i++) {

                Element currentGame = (Element)list.item(i);
                Juego juego = new Juego();
                juego.setID_juego(currentGame.getElementsByTagName("appID").item(0).getTextContent());
                juego.setNombre_juego(currentGame.getElementsByTagName("name").item(0).getTextContent());

                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GamingCoach/logos");
                File mediaFile = new File(path,juego.getID_juego()+".jpg");
                String ruta=mediaFile.getAbsolutePath();
                if(!mediaFile.exists()){

                    if(path.mkdirs())
                        System.out.println("archivo creado");

                    URL url = new URL(currentGame.getElementsByTagName("logo").item(0).getTextContent());
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

                    }catch (SocketTimeoutException e){
                        ruta = null;
                    }
                }
                juego.setLogo_juego(ruta);
                listaJuegos.put(juego.getID_juego(),juego);
            }

            doc = builder.parse(resp2.getEntity().getContent());
            list = doc.getElementsByTagName("message");
            String id;
            int minutos;
            for (int i = 0; i < list.getLength(); i++){
                Element currentGame = (Element)list.item(i);
                id = currentGame.getElementsByTagName("appid").item(0).getTextContent();
                minutos = Integer.parseInt(currentGame.getElementsByTagName("playtime_forever").item(0).getTextContent());
                listaJuegos.get(id).setMinTotal(minutos);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new Object[0];
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        Iterator iterator = listaJuegos.entrySet().iterator();
        DBJuego db_juego=new DBJuego(activity);
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            Juego juego = (Juego)pair.getValue();

            db_juego.insertaroActualizar(juego.getID_juego(), juego.getNombre_juego(), juego.getLogo_juego(), juego.getMinTotal());

        }
        db_juego.close();
        if(activity.getClass()==RegistroActivity.class)
            ((RegistroActivity)activity).procesoFinal();
    }
}
