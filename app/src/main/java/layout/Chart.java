package layout;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ami.coach.game.gamingcoach.R;
import ami.coach.game.gamingcoach.database.DBSesiones;
import ami.coach.game.gamingcoach.views.GameView;


public class Chart extends Fragment {

    LineChart mLineChart;
    HashMap<String,ArrayList<Entry>> mapaTest;


    public static Chart newInstance() {
        Chart fragment = new Chart();
        return fragment;
    }

    public Chart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View V = inflater.inflate(R.layout.fragment_chart, container, false);
        //mLineChart = (LineChart) V.findViewById(R.id.mLineChart);

        ListView lv = (ListView) V.findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        //crearMaps();
        crearMapasV();

        list.add(new LineChartItem(generateDataLine(mapaTest),getActivity()));
        list.add(new PieChartItem(generateDataPie(mapaTest),getActivity()));

        //addChart();

        ChartDataAdapter cda = new ChartDataAdapter(getActivity(), list);
        lv.setAdapter(cda);


        return V;

    }

    private LineData generateDataLine(HashMap<String,ArrayList<Entry>> entradas) {

        Iterator it = entradas.entrySet().iterator();
        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        while(it.hasNext()){
            Map.Entry current = (Map.Entry) it.next();
            LineDataSet d1 = new LineDataSet((ArrayList)current.getValue(), ""+current.getKey());
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setDrawValues(false);
            sets.add(d1);
        }

        LineData cd = new LineData(getWeekDays(), sets);
        return cd;
    }

    private PieData generateDataPie(HashMap<String,ArrayList<Entry>> entradas) {

        Iterator it = entradas.entrySet().iterator();

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<Entry> temp;
        ArrayList<String> labels = new ArrayList<String>();
        int horasxjuego=0,j=0;

        while(it.hasNext()){
            Map.Entry current = (Map.Entry) it.next();
            temp = (ArrayList)current.getValue();
            for(int i=0;i<temp.size();i++){
                horasxjuego += (int)temp.get(i).getVal();
            }
            entries.add(new Entry(horasxjuego,j));
            labels.add(current.getKey().toString());
            j++;
            horasxjuego=0;
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(labels,d);
        return cd;
    }


/*
    public void crearMaps() {

        mapaTest = new HashMap<String,ArrayList<Entry>>();
        ArrayList<Entry> horas = new ArrayList<Entry>();

        horas.add(new Entry(0,0));
        horas.add(new Entry(2, 1));
        horas.add(new Entry(3, 2));
        horas.add(new Entry(0, 3));
        horas.add(new Entry(2, 4));
        horas.add(new Entry(5, 5));
        horas.add(new Entry(0, 6));
        mapaTest.put("Resident Evil 5", horas);


        horas=new ArrayList<>();

        horas.add(new Entry(3,0));
        horas.add(new Entry(0,1));
        horas.add(new Entry(2,2));
        horas.add(new Entry(4,3));
        horas.add(new Entry(0,4));
        horas.add(new Entry(5,5));
        horas.add(new Entry(3,6));
        mapaTest.put("Metal Gear Solid 5", horas);


        horas=new ArrayList<>();

        horas.add(new Entry(1,0));
        horas.add(new Entry(0,1));
        horas.add(new Entry(4,2));
        horas.add(new Entry(1,3));
        horas.add(new Entry(2,4));
        horas.add(new Entry(3,5));
        horas.add(new Entry(1,6));
        mapaTest.put("Crash Bandicoot", horas);

    }*/

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private ArrayList<String> getQuarters() {

        ArrayList<String> q = new ArrayList<String>();
        q.add("1st Quarter");
        q.add("2nd Quarter");
        q.add("3rd Quarter");
        q.add("4th Quarter");

        return q;
    }

    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
    }

    private ArrayList<String> getWeekDays() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Lun");
        m.add("Mar");
        m.add("Mie");
        m.add("Jue");
        m.add("Vie");
        m.add("Sab");
        m.add("Dom");

        return m;
    }

    public void crearMapasV(){
        DBSesiones db_sesiones=new DBSesiones(getActivity());
        Cursor datos = db_sesiones.consultarSemana();
        int juego=0;
        String juego_nombre=null;
        mapaTest=new HashMap<>();
        ArrayList<Entry> juegos=new ArrayList<>();
        int indice=0;
        if (datos.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                if(juego!=datos.getInt(0) && juego!=0)
                {
                    mapaTest.put(juego_nombre,juegos);
                    juegos = new ArrayList<>();
                    indice=0;
                }

                juego=datos.getInt(0);
                juego_nombre=datos.getString(1);
                juegos.add(new Entry(datos.getInt(2), indice));
                indice++;

                System.out.println(datos.getInt(1));
                System.out.println(datos.getInt(2));
                System.out.println(datos.getString(3));
            } while(datos.moveToNext());

            mapaTest.put(juego_nombre,juegos);
        }

        db_sesiones.close();
    }
}