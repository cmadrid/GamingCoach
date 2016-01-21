package ami.coach.game.gamingcoach.views;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ami.coach.game.gamingcoach.InfoGame;
import ami.coach.game.gamingcoach.MainActivity;
import ami.coach.game.gamingcoach.InfoJuego;
import ami.coach.game.gamingcoach.R;

public class GameView extends FrameLayout {

    private TextView nombre_juego;
    private TextView duracion_sesion;
    private TextView inicio_sesion;
    private int sessionID;
    int id_juego;
    private ImageView logo_juego;
    private String strLogo;


    public static GameView newInstance(Context ctx,String nombre,String duracion,String ruta,int sessionID,int id_juego,String inicio) {
        GameView view = new GameView(ctx);
        view.setParams(nombre, duracion, ruta, sessionID,id_juego,inicio);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), InfoGame.class);
                intent.putExtra("id", ((GameView) v).getId_juego() + "");
                intent.putExtra("nombre",((GameView) v).getNombre().getText().toString()+"");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)v.getContext(),((GameView) v).getNombre(),"nombreJuego");
                v.getContext().startActivity(intent, options.toBundle());
            }
        });
        view.setOnLongClickListener(onLongClick());
        return view;
    }
    public static GameView newInstancePop(Context ctx,String nombre,String duracion,String ruta,int sessionID,int id_juego,String inicio) {
        GameView view = new GameView(ctx);
        view.setParams(nombre, duracion, ruta, sessionID, id_juego, inicio);
        return view;
    }
    public GameView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initData(context);
    }

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initData(context);
    }

    public GameView(Context context)
    {
        super(context);
        initData(context);
    }

    private void initData(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View mHeader = inflater.inflate(R.layout.view_game, this);
        nombre_juego = (TextView)mHeader.findViewById(R.id.nombre_juego);
        duracion_sesion = (TextView)mHeader.findViewById(R.id.duracion);
        inicio_sesion = (TextView)mHeader.findViewById(R.id.inicio);
        logo_juego = (ImageView)mHeader.findViewById(R.id.logo_juego);
    }

    public void setParams(String titulo,String duracion, String ruta,int sessionID,int id_juego,String inicio){
        setNombre(titulo);
        setDuracion(duracion);
        setLogo(ruta);
        setId_juego(id_juego);
        setInicio(inicio);
        this.strLogo = ruta;
        this.sessionID = sessionID;
    }

    public void setNombre(String nombre){
        nombre_juego.setText(nombre);
    }
    public void setDuracion(String duracion){
        duracion_sesion.setText(duracion);
    }
    public void setInicio(String inicio){
        inicio_sesion.setText(inicio);
    }

    public int getSessionID(){
        return this.sessionID;
    }

    public String getStrLogo() {
        return strLogo;
    }

    public TextView getNombre() {
        return nombre_juego;
    }
    public TextView getDuracion() {
        return duracion_sesion;
    }
    public TextView getInicio() {
        return inicio_sesion;
    }

    /*
    public ImageView getLogo_juego() {
        return logo_juego;
    }*/

    public void setId_juego(int id_juego){
        this.id_juego=id_juego;
    }
    public int getId_juego(){
        return id_juego;
    }

    public void setLogo(String ruta){
        if(ruta==null)
            logo_juego.setImageResource(R.drawable.no_logo);
        else
            logo_juego.setImageURI(Uri.parse(ruta));
    }


    public static OnLongClickListener onLongClick(){
        return new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println("evento on LongClick");
                return false;
            }
        };
    }
}

