package layout;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import ami.coach.game.gamingcoach.GetPerfilXml;
import ami.coach.game.gamingcoach.MainActivity;
import ami.coach.game.gamingcoach.R;
import ami.coach.game.gamingcoach.database.DBJuego;
import ami.coach.game.gamingcoach.database.DBSesiones;
import ami.coach.game.gamingcoach.views.GameView;

public class games extends Fragment {

    private LinearLayout ll_juegos;
    private GameView sesionAct=null;
    public static games newInstance() {
        games fragment = new games();
        return fragment;
    }

    public games() {
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View V = inflater.inflate(R.layout.fragment_games, container, false);
            ll_juegos=(LinearLayout)V.findViewById(R.id.lista_juegos);

            addGames();

            return V;

        }



        public void addGames(){
            //consulta de las sesiones
            DBSesiones db_sesiones = new DBSesiones(getActivity());
            //GetCurrentSession obtenerSesionAct = new GetCurrentSession(getContext());
            //obtenerSesionAct.execute();
            Cursor datos = db_sesiones.consultar(null);
            if (datos.moveToFirst()) {
                do {
                    ll_juegos.addView(GameView.newInstance(getContext(), datos.getString(1), "Game Time: " + datos.getString(2), datos.getString(3),datos.getInt(4)));
                } while (datos.moveToNext());
            }

            db_sesiones.close();

            start();
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
            MainActivity.mainActivity.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GetCurrentSession obtenerSesionAct = new GetCurrentSession(getContext());
                            obtenerSesionAct.execute();
                        }
                    });;
        }
    };

    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 1 * 60 * 1000);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }

        private class GetCurrentSession extends AsyncTask{

            Context ctx;
            DBSesiones dbs;

            public GetCurrentSession(Context ctx) {
                this.ctx = ctx;
            }

            @Override
            protected Object doInBackground(Object[] params) {

                dbs = new DBSesiones(ctx);
                Cursor datos = dbs.consultarActivos(null);//ID_SESSION,ID_JUEGO,MINUTOS,FECHA_INICIO,NOMBRE_JUEGO,LOGO
                if (datos.moveToFirst()) {
                    if (sesionAct == null) {
                        publishProgress(datos.getInt(0), datos.getString(4), datos.getString(2), datos.getString(3),datos.getString(5), 0);
                    } else {
                        publishProgress(datos.getInt(0), datos.getString(4), datos.getString(2), datos.getString(3),datos.getString(5), 1);
                    }
                } else {
                    sesionAct = null;
                }
                dbs.close();

                return null;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                int caso = (Integer)values[5];
                if(caso==0){
                    sesionAct= GameView.newInstance(getContext(),"Activo: "+(String)values[1],"Game Time: "+(String)values[2],(String)values[4],(Integer)values[0]);
                    ll_juegos.addView(sesionAct,0);
                }else if(caso==1){
                    if(sesionAct.getSessionID()==(Integer)values[0]){
                        sesionAct.setDuracion("Game Time: "+(String)values[2]);
                    }else{
                        ll_juegos.removeViewAt(0);
                        String lbl = sesionAct.getNombre().getText().toString();
                        String lbl2 = sesionAct.getDuracion().getText().toString();
                        ll_juegos.addView(GameView.newInstance(getContext(),lbl.replace("Activo: ",""),"Game Time: "+lbl2.replace("Game Time: ",""),sesionAct.getStrLogo(),sesionAct.getSessionID()),0);
                        sesionAct=GameView.newInstance(getContext(),"Activo: "+(String)values[1],"Game Time: "+(String)values[2],(String)values[4],(Integer)values[0]);
                        ll_juegos.addView(sesionAct,0);
                    }

                }else{
                    sesionAct=null;

                }

            }
        }

    }
