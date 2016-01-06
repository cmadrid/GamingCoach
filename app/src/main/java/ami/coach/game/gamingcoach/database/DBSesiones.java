package ami.coach.game.gamingcoach.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBSesiones {
    public static final String NOMBRE_TABLA = "sesiones";
    public static final String ID = "_id";
    public static final String JUEGO = "ig_juego";
    public static final String MINUTOS = "minutos";
    public static final String INICIO = "inicio";


    private DbHelper helper;
    private SQLiteDatabase db;

    public static final String CREATE_TABLE = "create table "+ NOMBRE_TABLA +" ("
            + ID + " integer primary key autoincrement,"
            + JUEGO + " integer not null,"
            + MINUTOS + " integer not null,"
            + INICIO + "  TIMESTAMP NOT NULL"
            +");";

    public DBSesiones(Context contexto) {
        helper = new DbHelper(contexto);
        db = helper.getWritableDatabase();
    }
    public ContentValues generarContentValues(String juego,String minutos,Date inicio){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ContentValues valores = new ContentValues();
        valores.put(JUEGO,juego);
        valores.put(MINUTOS,minutos);
        valores.put(INICIO, dateFormat.format(inicio));

        return valores;
    }
    public void insertar(String juego,String minutos,Date inicio){
        //insert  into contactos
        db.insert(NOMBRE_TABLA, null, generarContentValues(juego, minutos, inicio));
    }
    public void insertaroActualizar(String juego,String minutos,Date inicio){
        //insert  into contactos
        Cursor c = consultarActivos(juego);
        if(c.moveToFirst()) {
            String[] args = new String[] {c.getString(0)};
            db.update(NOMBRE_TABLA, generarContentValues(juego, minutos, inicio), ID + "=?", args);
        }
        else
            db.insert(NOMBRE_TABLA,null,generarContentValues(juego, minutos, inicio));
    }


    public Cursor consultarActivos(String juego){
        //insert  into contactos

        String[] campos = new String[] {ID, JUEGO , MINUTOS,INICIO};
        //Cursor c = db.query(NOMBRE_TABLA, campos, "usuario=?(where)", args(para el where), group by, having, order by, num);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] args = new String[] {juego};

        if(juego==null)return db.query(NOMBRE_TABLA, campos, null, null, null, null,null);
        return db.query(NOMBRE_TABLA, campos, JUEGO+"=? and date('now',-10 minutes)<"+INICIO, args, null, null, null);
    }


    public void vaciar(){
        db.delete(NOMBRE_TABLA,null,null);
    }

    public void close(){
        try {
            if(helper!=null){
                helper.close();
                helper=null;
            }

            if(db!=null){
                db.close();
                db=null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
