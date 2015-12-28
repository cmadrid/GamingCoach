package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ami.coach.game.gamingcoach.R;
import ami.coach.game.gamingcoach.views.GameView;

public class games extends Fragment {

    public static games newInstance() {
        games fragment = new games();
        return fragment;
    }

    public games() {
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View V = inflater.inflate(R.layout.fragment_games, container, false);
            final LinearLayout ll=(LinearLayout)V.findViewById(R.id.lista_juegos);

            ll.addView(GameView.newInstance(getContext(),"Juego X",null));
            ll.addView(GameView.newInstance(getContext(),"Juego Y",null));
            ll.addView(GameView.newInstance(getContext(),"Juego Z",null));
            ll.addView(GameView.newInstance(getContext(),"Juego A",null));
            ll.addView(GameView.newInstance(getContext(),"Juego B",null));
            ll.addView(GameView.newInstance(getContext(),"Juego C",null));

            return V;

        }

    }
