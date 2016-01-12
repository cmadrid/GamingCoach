package ami.coach.game.gamingcoach.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DBIndices {
    public static final String NOMBRE_TABLA = "indices";
    public static final String ID = "_id_indice";
    public static final String COD = "codigo_indice";
    public static final String NAME = "name_indice";
    public static final String GROUP = "group_indice";
    public Context ctx;


    private DbHelper helper;
    private SQLiteDatabase db;

    public static final String CREATE_TABLE = "create table "+ NOMBRE_TABLA +" ("
            + ID + " integer primary key autoincrement,"
            + COD + " integer not null,"
            + NAME + " text not null,"
            + GROUP + " text not null"
            +");";

    public DBIndices(Context contexto) {
        this.ctx=contexto;
        helper = new DbHelper(contexto);
        db = helper.getWritableDatabase();
    }
    public ContentValues generarContentValues(int code,String name,String group){
        ContentValues valores = new ContentValues();
        valores.put(COD,code);
        valores.put(NAME,name);
        valores.put(GROUP, group);

        return valores;
    }
    public void insertar(int code,String name,String group){
        //insert  into contactos
        db.insert(NOMBRE_TABLA, null, generarContentValues(code, name, group));
    }
    public Cursor consultar(String id){
        //insert  into contactos

        String[] campos = new String[] {ID, COD , NAME,GROUP};
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
