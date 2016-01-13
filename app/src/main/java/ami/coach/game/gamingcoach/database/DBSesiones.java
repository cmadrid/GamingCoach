package ami.coach.game.gamingcoach.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBSesiones {
    public static final String NOMBRE_TABLA = "sesiones";
    public static final String ID = "_id_sesiones";
    public static final String JUEGO = "id_juego_sesiones";
    public static final String MINUTOS = "minutos_sesiones";
    public static final String INICIO = "inicio_sesiones";
    public static final String ACTUAIZACION = "actulizacion_sesiones";

    public static final  String TABLE_FK=DBJuego.NOMBRE_TABLA;
    public static final String FK_ID = DBJuego.ID;

    private DbHelper helper;
    private SQLiteDatabase db;

    public static final String CREATE_TABLE = "create table "+ NOMBRE_TABLA +" ("
            + ID + " integer primary key autoincrement,"
            + JUEGO + " integer not null,"
            + MINUTOS + " integer not null,"
            + INICIO + "  TIMESTAMP NOT NULL,"
            + ACTUAIZACION + "  TIMESTAMP NOT NULL,"
            + " FOREIGN KEY("+JUEGO+") REFERENCES "+TABLE_FK+"("+FK_ID+"));";

    public DBSesiones(Context contexto) {
        helper = new DbHelper(contexto);
        db = helper.getWritableDatabase();
    }
    public ContentValues generarContentValues(String juego,String minutos,Date inicio){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ContentValues valores = new ContentValues();
        if(juego!=null)
            valores.put(JUEGO,juego);
        if(minutos!=null)
            valores.put(MINUTOS,minutos);
        if(inicio!=null)
            valores.put(INICIO, dateFormat.format(inicio));
        valores.put(ACTUAIZACION, dateFormat.format(new Date()));

        return valores;
    }
    public void insertar(String juego,String minutos,Date inicio){
        //insert  into contactos
        db.insert(NOMBRE_TABLA, null, generarContentValues(juego, minutos, inicio));
    }
    public void insertaroActualizar(String juego,String minutos,Date inicio){
        //insert  into contactos
        Cursor c = consultarActivos(juego);
        if(!c.moveToFirst())
            db.insert(NOMBRE_TABLA,null,generarContentValues(juego, minutos, inicio));
        else
            db.update(NOMBRE_TABLA, generarContentValues(juego, (c.getInt(2) + Integer.parseInt(minutos)) + "", null), ID + "=?", new String[]{c.getString(0)});

    }


    public Cursor consultarActivos(String juego) {

        String QB= NOMBRE_TABLA +
                " JOIN " + DBJuego.NOMBRE_TABLA + " ON " +
                JUEGO + " = " + DBJuego.ID;

        String[] campos = new String[] {ID, JUEGO, MINUTOS,INICIO,DBJuego.NOMBRE,DBJuego.LOGO};
        //Cursor c = db.query(NOMBRE_TABLA, campos, "usuario=?(where)", args(para el where), group by, having, order by, num);

        String[] args = new String[] {juego};

        if(juego==null)
            return db.query(QB, campos, "datetime('now','-5 hours','-45 minutes')<" + ACTUAIZACION, null, null, null,ACTUAIZACION+" desc");
        else
            return db.query(QB, campos, JUEGO + "=? and datetime('now','-5 hours','-45 minutes')<" + ACTUAIZACION, args, null, null, ACTUAIZACION+" desc");

    }
    public Cursor consultar(String id){
        //insert  into contactos

        String QB= NOMBRE_TABLA +
                " JOIN " + TABLE_FK + " ON " +
                NOMBRE_TABLA+"."+JUEGO + " = " + TABLE_FK+"."+FK_ID;


        String[] campos = new String[] {JUEGO, DBJuego.NOMBRE , MINUTOS, DBJuego.LOGO,ID};
        //Cursor c = db.query(NOMBRE_TABLA, campos, "usuario=?(where)", args(para el where), group by, having, order by, num);

        String[] args = new String[] {id};

        String orderBy = ACTUAIZACION+" desc";
        if(id==null)return db.query(QB, campos, null, null, null, null,orderBy);
        return db.query(QB, campos, ID+"=?", args, null, null, orderBy);
    }



    public Cursor consultarSemana(Date desde){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00");

        String QB= NOMBRE_TABLA +
                " JOIN " + TABLE_FK + " ON " + JUEGO + " = "+FK_ID;
        String[] args = new String[] {dateFormat.format(desde)};


        String[] campos = new String[] {JUEGO,DBJuego.NOMBRE, "sum("+MINUTOS+")","strftime('%Y-%m-%d',"+INICIO+ ")","strftime('%w',"+INICIO+")"};

        return db.query(QB, campos, INICIO+">?", args,"1,2,4", null, "1,4");
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
