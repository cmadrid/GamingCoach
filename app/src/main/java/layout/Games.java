package layout;


import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ami.coach.game.gamingcoach.MainActivity;
import ami.coach.game.gamingcoach.R;
import ami.coach.game.gamingcoach.database.DBSesiones;
import ami.coach.game.gamingcoach.views.GameView;

public class Games extends Fragment {

    private LinearLayout ll_juegos;
    private ScrollView mainScroll;
    private GameView sesionAct=null;
    String id=null;
    public static Games newInstance() {
        return new Games();

    }


    public static Games newInstance(String id) {
        Games fragment = new Games();
        fragment.id=id;
        return fragment;
    }

    public Games() {
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View V = inflater.inflate(R.layout.fragment_games, container, false);
            ll_juegos=(LinearLayout)V.findViewById(R.id.lista_juegos);
            ll_juegos.setWeightSum(1);
            mainScroll=(ScrollView)V.findViewById(R.id.mainScroll);

            addGames(id);
            if(id==null)
                start();
            return V;

        }



        public void addGames(String id){
            //consulta de las sesiones
            DBSesiones db_sesiones = new DBSesiones(getActivity());
            Cursor datos = db_sesiones.consultar(id);
            if (datos.moveToFirst()) {
                do {
                    if(id==null) {
                        ll_juegos.addView(GameView.newInstance(getContext(), datos.getString(1), "Game Time: " + datos.getString(2), datos.getString(3), datos.getInt(4), datos.getInt(0), datos.getString(5)));
                    }else {
                        ll_juegos.addView(GameView.newInstancePop(getContext(), datos.getString(1), "Game Time: " + datos.getString(2), datos.getString(3), datos.getInt(4), datos.getInt(0), datos.getString(5)));
                    }
                } while (datos.moveToNext());
            }else{
                TextView firstmsg = new TextView(getContext());
                firstmsg.setText("Bienvenido a Gaming Coach\nAhora puedes empezar a jugar");
                firstmsg.setLines(2);
                firstmsg.setHeight(500);
                firstmsg.setGravity(Gravity.CENTER_VERTICAL);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    firstmsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                ll_juegos.addView(firstmsg);
            }

            db_sesiones.close();

/*
            //consulta de todos los juegos con el tiempo acumulado de x vida
            DBJuego db_juego=new DBJuego(getActivity());
            datos = db_juego.consultar(null);
            if (datos.moveToFirst()) {
                do {
                    ll_juegos.addView(GameView.newInstance(getContext(),datos.getString(1),"Game Time: "+datos.getString(2),datos.getString(3),datos.getInt(4)));
                } while(datos.moveToNext());
            }
            db_juego.close();
*/


        }


    private Timer timer;
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            System.out.println("loop activos");
            if(MainActivity.mainActivity!=null)
            MainActivity.mainActivity.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new GetCurrentSession(getContext()).execute();
                        }
                    });
        }
    };

    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 60000);//1*60*1000: 1minuto
    }

    public void stop() {
        if(timer!=null)
            timer.cancel();
        timer = null;
    }

        private class GetCurrentSession extends AsyncTask<String,Object,Object>{

            Context ctx;
            DBSesiones dbs;

            public GetCurrentSession(Context ctx) {
                this.ctx = ctx;
            }

            @Override
            protected Object doInBackground(String[] params) {

                dbs = new DBSesiones(ctx);
                Cursor datos = dbs.consultarActivos(null);//ID_SESSION,ID_JUEGO,MINUTOS,FECHA_INICIO,NOMBRE_JUEGO,LOGO
                if (datos.moveToFirst()) {
                    if (sesionAct == null) {
                        publishProgress(datos.getInt(0), datos.getString(4), datos.getString(2), datos.getString(3),datos.getString(5), 0,datos.getInt(1));
                    } else {
                        publishProgress(datos.getInt(0), datos.getString(4), datos.getString(2), datos.getString(3),datos.getString(5), 1,datos.getInt(1));
                    }
                } else {
                    if(sesionAct != null)
                        publishProgress(null,null,null,null,null,2);
                }
                dbs.close();

                return null;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                int caso = (Integer)values[5];
                if(caso==0){
                    if(ll_juegos.getChildCount()==1)
                        if(ll_juegos.getChildAt(0).getClass()==TextView.class)
                            ll_juegos.removeViewAt(0);
                    sesionAct= GameView.newInstance(getContext(),"Activo: "+values[1],"Game Time: "+values[2],(String)values[4],(Integer)values[0],(Integer)values[6],(String)values[3]);
                    ll_juegos.addView(sesionAct,0);
                }else if(caso==1){
                    if(sesionAct.getSessionID()==(Integer)values[0]){
                        sesionAct.setDuracion("Game Time: "+values[2]);
                    }else{
                        ll_juegos.removeViewAt(0);
                        String lbl = sesionAct.getNombre().getText().toString();
                        String lbl2 = sesionAct.getDuracion().getText().toString();
                        ll_juegos.addView(GameView.newInstance(getContext(),lbl.replace("Activo: ", ""),"Game Time: "+lbl2.replace("Game Time: ", ""),sesionAct.getStrLogo(),sesionAct.getSessionID(),sesionAct.getId_juego(),sesionAct.getInicio().getText()+""),0);
                        sesionAct=GameView.newInstance(getContext(),"Activo: "+values[1],"Game Time: "+values[2],(String)values[4],(Integer)values[0],(Integer)values[6],(String)values[3]);
                        ll_juegos.addView(sesionAct,0);
                    }

                }else{

                    ll_juegos.removeViewAt(0);
                    String lbl = sesionAct.getNombre().getText().toString();
                    String lbl2 = sesionAct.getDuracion().getText().toString();
                    ll_juegos.addView(GameView.newInstance(getContext(),lbl.replace("Activo: ",""),"Game Time: "+lbl2.replace("Game Time: ", ""),sesionAct.getStrLogo(),sesionAct.getSessionID(),sesionAct.getId_juego(),sesionAct.getInicio().getText()+""),0);
                    sesionAct=null;

                }

            }
        }

    }
