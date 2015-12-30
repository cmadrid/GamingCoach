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
            Toast.makeText(ServiceBackground.this,"Service Started",Toast.LENGTH_SHORT).show();
        }
        System.out.println(timer.getStatus());
        return START_STICKY;//averiguar bien
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timer.cancel(true);
        Toast.makeText(this,"Services Stoped",Toast.LENGTH_SHORT).show();
    }


    public class Timer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            while(true)
            {
                if(isCancelled())break;
                publishProgress();
                SystemClock.sleep(300 * 1000);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            title="Mantenerte en contacto con tu hijo.";
            content="Es bueno que de vez en cuando en tus momentos libres " +
                    "llames a tu hijo un momento, asi sea solo a saludar o a " +
                    "preguntar como esta. Es bueno que el note que lo tienes presente.";
            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);


            Intent intent = new Intent(ServiceBackground.super.getApplicationContext(),MainActivity.class);

            intent.putExtra("title",title);
            intent.putExtra("content",content);
            //intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pIntent = PendingIntent.getActivity(ServiceBackground.super.getApplication(), iUniqueId, intent, 0);
            Notification noti = new Notification.Builder(ServiceBackground.super.getApplication())
                    .setTicker("DadTime Notification")
                    .setContentTitle("DadTime - "+title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setStyle(new Notification.BigTextStyle()
                    //        .bigText("un texto muy muy laaaaaargooooooo,un texto muy muy laaaaaargooooooo,un texto muy muy laaaaaargooooooo,un texto muy muy laaaaaargooooooo,un texto muy muy laaaaaargooooooo,un texto muy muy laaaaaargooooooo"))
                    .setContentIntent(pIntent).getNotification();
            noti.flags=Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);

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
