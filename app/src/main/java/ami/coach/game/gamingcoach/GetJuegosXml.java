package ami.coach.game.gamingcoach;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Joseph_Gallardo on 29/12/2015.
 */
public class GetJuegosXml extends AsyncTask<String,Void,Object[]> {

    HashMap<String,Juego> listaJuegos = new HashMap<String,Juego>();

    @Override
    protected Object[] doInBackground(String... params) {

        try{
            String idUsuario = params[0];
            String id64 = params[1];
            HttpGet uri1 = new HttpGet("http://steamcommunity.com/id/"+idUsuario+"/games?tab=all&xml=1");
            HttpGet uri2 = new HttpGet("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=8CA1864E6DDD065C53651CF9F88404B2&steamid="+id64+"&format=xml");

            DefaultHttpClient client1 = new DefaultHttpClient();
            HttpResponse resp1 = client1.execute(uri1);

            DefaultHttpClient client2 = new DefaultHttpClient();
            HttpResponse resp2 = client2.execute(uri2);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resp1.getEntity().getContent());

            NodeList list = doc.getElementsByTagName("game");
            System.out.println(list.getLength());
            System.out.println(list.item(0).getTextContent());

            for (int i = 0; i < list.getLength(); i++) {
                //Node currentNode = list.item(i);
                Element currentGame = (Element)list.item(i);
                Juego juego = new Juego();
                juego.setID_juego(currentGame.getElementsByTagName("appID").item(0).getTextContent());
                juego.setNombre_juego(currentGame.getElementsByTagName("name").item(0).getTextContent());
                juego.setLogo_juego(currentGame.getElementsByTagName("logo").item(0).getTextContent());
                listaJuegos.put(juego.getID_juego(),juego);
            }

            doc = builder.parse(resp2.getEntity().getContent());
            list = doc.getElementsByTagName("message");
            String id = new String();
            int minutos=0;
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
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            System.out.println(pair.getValue());
        }
    }
}
