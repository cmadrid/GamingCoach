package ami.coach.game.gamingcoach;

/**
 * Created by Joseph_Gallardo
 * on 29/12/2015.
 */
public class Juego {
    private String ID_juego;
    private String nombre_juego;
    private String logo_juego;
    private int minTotal;

    public Juego() {
    }
/*
    public Juego(String ID_juego, String nombre_juego, String logo_juego, int minTotal) {
        this.ID_juego = ID_juego;
        this.nombre_juego = nombre_juego;
        this.logo_juego = logo_juego;
        this.minTotal = minTotal;
    }
*/
    public String getID_juego() {
        return ID_juego;
    }

    public String getNombre_juego() {
        return nombre_juego;
    }

    public String getLogo_juego() {
        return logo_juego;
    }

    public int getMinTotal() {
        return minTotal;
    }

    public void setID_juego(String ID_juego) {
        this.ID_juego = ID_juego;
    }

    public void setNombre_juego(String nombre_juego) {
        this.nombre_juego = nombre_juego;
    }

    public void setLogo_juego(String logo_juego) {
        this.logo_juego = logo_juego;
    }

    public void setMinTotal(int minTotal) {
        this.minTotal = minTotal;
    }

    @Override
    public String toString() {
        return "Nombre Juego: "+this.nombre_juego+"\nAppID: "+this.ID_juego+ "\nLogo: "+this.logo_juego+"\nMinutos Totales Jugados: "+minTotal+"\n";
    }
}
