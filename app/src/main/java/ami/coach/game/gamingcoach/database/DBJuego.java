package ami.coach.game.gamingcoach.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBJuego {
    public static final String NOMBRE_TABLA = "juegos";
    public static final String ID = "_id";
    public static final String NOMBRE = "nombre";
    public static final String LOGO = "logo";
    public static final String MINUTOS = "minutos";
    public Context ctx;


    private DbHelper helper;
    private SQLiteDatabase db;

    public static final String CREATE_TABLE = "create table "+ NOMBRE_TABLA +" ("
            + ID + " integer primary key,"
            + NOMBRE + " text not null,"
            + LOGO + " text,"
            + MINUTOS + " integer"
            +");";

    public DBJuego(Context contexto) {
        this.ctx=contexto;
        helper = new DbHelper(contexto);
        db = helper.getWritableDatabase();
    }
    public ContentValues generarContentValues(String id,String nombre,String logo,int minutos){
        ContentValues valores = new ContentValues();
        valores.put(ID,id);
        valores.put(NOMBRE,nombre);
        valores.put(LOGO,logo);
        valores.put(MINUTOS, minutos);

        return valores;
    }
    public void insertar(String id,String nombre,String logo,int minutos){
        //insert  into contactos
        db.insert(NOMBRE_TABLA, null, generarContentValues(id, nombre, logo, minutos));
    }
    public void insertaroActualizar(String id,String nombre,String logo,int minutos){

        Cursor c = consultar(id);
        if(c.moveToFirst()) {
            String[] args = new String[] {id};
            int minutos_antes = consultarMinutos(id);
            if(minutos_antes!=minutos) {
               // System.out.println("minutos no coinciden!!!: " + minutos_antes + " - " + minutos);

                DBSesiones dbSesiones=new DBSesiones(ctx);
                dbSesiones.insertar(id,(minutos-minutos_antes)+"",new Date());
                dbSesiones.close();

                db.update(NOMBRE_TABLA, generarContentValues(id, nombre, logo, minutos), ID + "=?", args);
            }
           // else System.out.println("coincide");

        }else
            db.insert(NOMBRE_TABLA,null,generarContentValues(id,nombre,logo,minutos));
    }

    public int consultarMinutos(String id){
        String[] campos = new String[] {MINUTOS};
        String[] args = new String[] {id};
        int minutos=0;
        Cursor c = db.query(NOMBRE_TABLA, campos, ID+"=?", args, null, null, null);
        if(c.moveToFirst())
            minutos=c.getInt(0);
        return minutos;
    }


    public Cursor consultar(String id){
        //insert  into contactos

        String[] campos = new String[] {ID, NOMBRE , MINUTOS,LOGO};
        //Cursor c = db.query(NOMBRE_TABLA, campos, "usuario=?(where)", args(para el where), group by, having, order by, num);

        String[] args = new String[] {id};

        if(id==null)return db.query(NOMBRE_TABLA, campos, null, null, null, null,null);
        return db.query(NOMBRE_TABLA, campos, ID+"=?", args, null, null, null);
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
