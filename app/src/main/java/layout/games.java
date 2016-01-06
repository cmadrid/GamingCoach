package layout;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ami.coach.game.gamingcoach.R;
import ami.coach.game.gamingcoach.database.DBJuego;
import ami.coach.game.gamingcoach.views.GameView;

public class games extends Fragment {

    private LinearLayout ll_juegos;
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
            DBJuego db_juego=new DBJuego(getActivity());
            Cursor datos = db_juego.consultar(null);
            if (datos.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    ll_juegos.addView(GameView.newInstance(getContext(),datos.getString(1),"Tiempo de juego en minutos: "+datos.getString(2),datos.getString(3)));
                } while(datos.moveToNext());
            }

            db_juego.close();
        }

    }
