package ami.coach.game.gamingcoach.views;

/**
 * Created by Jegerima on 25/01/2015.
 */

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ami.coach.game.gamingcoach.R;

/**
 * Created by Jegerima on 25/01/2015.
 */
public class GameView extends FrameLayout {

    private TextView nombre_juego;
    private TextView duracion_sesion;
    private ImageView logo_juego;

    private Context mContext;
    private View mHeader;

    //private enum estado{'A Entegar',Entregado,Atrasado,};
    public static GameView newInstance(Context ctx,String nombre,String duracion,String ruta) {
        GameView view = new GameView(ctx);
        view.setParams(nombre,duracion,ruta);
        return view;
    }
    public GameView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context,attrs,defStyle);
        initData(context);
    }

    public GameView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        initData(context);
    }

    public GameView(Context context)
    {
        super(context);
        initData(context);
    }

    private void initData(Context context){
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mHeader = inflater.inflate(R.layout.view_game, this);
        nombre_juego = (TextView)mHeader.findViewById(R.id.nombre_juego);
        duracion_sesion = (TextView)mHeader.findViewById(R.id.duracion);
        logo_juego = (ImageView)mHeader.findViewById(R.id.logo_juego);
    }

    public void setParams(String titulo,String duracion, String ruta){
        setNombre(titulo);
        setDuracion(duracion);
        setLogo(ruta);
    }

    public void setNombre(String nombre){
        nombre_juego.setText(nombre);
    }
    public void setDuracion(String duracion){
        duracion_sesion.setText(duracion);
    }

    public TextView getNombre() {
        return nombre_juego;
    }
    public TextView getDuracion() {
        return duracion_sesion;
    }

    public void setLogo(String ruta){
        if(ruta==null)
            logo_juego.setImageResource(R.mipmap.ic_launcher);
        else
            logo_juego.setImageURI(Uri.parse(ruta));
    }


}

