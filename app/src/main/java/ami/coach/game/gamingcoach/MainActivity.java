package ami.coach.game.gamingcoach;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ami.coach.game.gamingcoach.database.DBJuego;
import ami.coach.game.gamingcoach.database.DBSesiones;
import layout.Chart;
import layout.gamess;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public static MainActivity mainActivity=null;
    gamess actividades = gamess.newInstance();
    Chart estadisticas = Chart.newInstance();

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    static Intent intent;
    static String steamId64;
    static String steamId;
    static SharedPreferences sharedpreferences;
    ImageView imagen_perfil;
    TextView steam_id;
    TextView estado;
    TextView mensaje_estado;
    TextView actualizado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        MainActivity.intent = getIntent();

        sharedpreferences = getSharedPreferences(GamingCoach.Pref.MyPREFERENCES, Context.MODE_PRIVATE);
        steamId64 = sharedpreferences.getString(GamingCoach.Pref.SteamId64, "");
        steamId = sharedpreferences.getString(GamingCoach.Pref.SteamId, "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                      .setAction("Action", null).show();
          }
      });


        imagen_perfil = (ImageView)findViewById(R.id.img_perfil);
        steam_id = (TextView)findViewById(R.id.steam_id);
        estado = (TextView)findViewById(R.id.status);
        mensaje_estado = (TextView)findViewById(R.id.stateMessage);
        actualizado = (TextView)findViewById(R.id.updated);


        setInfo();

        startService(new Intent(getBaseContext(),ServiceBackground.class));

    }

    public void setInfo(){

        String estado_str = sharedpreferences.getString(GamingCoach.Pref.StateMessage, "");
        String id = sharedpreferences.getString(GamingCoach.Pref.SteamId, "");
        String estadoEnlinea = sharedpreferences.getString(GamingCoach.Pref.OnlineState, "");
        steam_id.setText(id);
        estado.setText(estadoEnlinea);
        mensaje_estado.setText(estado_str);
        String logo = sharedpreferences.getString(GamingCoach.Pref.Avatar, null);
        if(logo==null)
            imagen_perfil.setImageResource(R.drawable.no_avatar);
        else
            imagen_perfil.setImageURI(Uri.parse(logo));

        Long millis = sharedpreferences.getLong(GamingCoach.Pref.updated, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        String newstring = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US).format(calendar.getTime());
        String actualizado_str="Actualizado el: " + newstring;
        actualizado.setText(actualizado_str);

        if(estadoEnlinea.equalsIgnoreCase("offline"))
            estado.setTextColor(Color.RED);
        else
            estado.setTextColor(Color.GREEN);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivity=null;
        actividades.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrar_sesion) {
            SharedPreferences sharedpreferences = getSharedPreferences(GamingCoach.Pref.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.putString(GamingCoach.Pref.CustomUrl, sharedpreferences.getString(GamingCoach.Pref.CustomUrl, null));
            editor.apply();
            //editor.commit();

            Intent intent = new Intent(this,RegistroActivity.class);
            startActivity(intent);
            this.finish();

            DBSesiones db_sesiones=new DBSesiones(this);
            db_sesiones.vaciar();
            db_sesiones.close();
            DBJuego db_juego=new DBJuego(this);
            db_juego.vaciar();
            db_juego.close();
            stopService(new Intent(getBaseContext(),ServiceBackground.class));

            actividades.stop();
            
            return true;
        }
        else if (id==R.id.mostrar_alerta){
            notificacion(0);
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
                return actividades;
            else return estadisticas;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Actididades";
                case 1:
                    return "Estadisticas";
                /*case 2:
                    return "Proximamente";*/
            }
            return null;
        }
    }
    public void notificacion(float tiempo){
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        //tiempo=0;
        String title="Mucho tiempo de Juego";
        CharSequence content="Ud ha llevado jugando durante "+tiempo+" minutos. \n" +
                "Por su salud considere tomar un tiempo de descanso.";


        Intent intent = new Intent(mainActivity,MainActivity.class);

        intent.putExtra("title",title);
        intent.putExtra("content",content);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(mainActivity, iUniqueId, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity)
                .setTicker("Gaming Coach Notification")
                .setContentTitle("Gaming Coach - " + title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .addAction (R.color.online,
                        getString(R.string.cerrar_sesion), pIntent)
                .addAction (R.drawable.bg_profile,
                        getString(R.string.steam_id), pIntent);
        //.setSound(Uri.parse("uri://sadfasdfasdf.mp3"))

        Notification noti = builder.build();


        noti.flags=Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(MainActivity.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }



}
