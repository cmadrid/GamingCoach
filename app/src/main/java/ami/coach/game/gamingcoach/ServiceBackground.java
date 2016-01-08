package ami.coach.game.gamingcoach;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ces_m on 12/6/2015.
 */
public class ServiceBackground extends Service {
    public static final String MyPREFERENCES = "logPreferences" ;
    SharedPreferences sharedpreferences;
    String customUrl;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        customUrl = sharedpreferences.getString(RegistroActivity.Prefs.CustomUrl.name(), "");
        start();
        Toast.makeText(this,"Gaming Coach ON!!!",Toast.LENGTH_SHORT).show();
        return START_STICKY;//averiguar bien
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stop();
        Toast.makeText(this,"Gaming Coach OFF",Toast.LENGTH_SHORT).show();
    }


    private Timer timer;
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            System.out.println("progreso del servicio!!!");
            GetPerfilXml getPerfilXml = new GetPerfilXml(getApplicationContext());
            getPerfilXml.execute(customUrl);
        }
    };

    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 30*1000);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }


}
