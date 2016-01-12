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
import java.util.Calendar;
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
        int i =0;
        while(it.hasNext()){
            Map.Entry current = (Map.Entry) it.next();
            LineDataSet d1 = new LineDataSet((ArrayList)current.getValue(), ""+current.getKey());
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setColor(ColorTemplate.VORDIPLOM_COLORS[i]);
            d1.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[i]);
            d1.setDrawValues(false);
            sets.add(d1);
            i++;
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
        d.setColors(ColorTemplate.COLORFUL_COLORS);
        d.setValueTextColor(R.color.black);

        PieData cd = new PieData(labels,d);
        return cd;
    }


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
        m.add("Dom");
        m.add("Lun");
        m.add("Mar");
        m.add("Mie");
        m.add("Jue");
        m.add("Vie");
        m.add("Sab");

        return m;
    }

    public void crearMapasV(){


        Calendar cal = Calendar.getInstance();
        int dia = cal.get(Calendar.DAY_OF_WEEK)-1;//Empieza con domingo=1 y termina con sabsado=7

        cal.add(Calendar.DAY_OF_MONTH, dia*-1);



        DBSesiones db_sesiones=new DBSesiones(getActivity());
        Cursor datos = db_sesiones.consultarSemana(cal.getTime());
        int juego=0;
        String juego_nombre=null;
        mapaTest=new HashMap<>();
        ArrayList<Entry> juegos=new ArrayList<>();
        //int indice=0;
        if (datos.moveToFirst()) {
            for(int i=0;i<7;i++)juegos.add(new Entry(0,i));//guardamos en cero ese juego en todos los dias inicialmente

            //Recorremos el cursor hasta que no haya más registros
            do {

                if(juego!=datos.getInt(0) && juego!=0)
                {
                    mapaTest.put(juego_nombre,juegos);//a partir de un segundo juego se almacena en el hashmap
                    juegos = new ArrayList<Entry>();//se reinicia la lista para el nuevo juego
                    for(int i=0;i<7;i++)juegos.add(new Entry(0,i));//se vuelve a encerar los dias

                }

                juego=datos.getInt(0);
                juego_nombre=datos.getString(1);
                int indice = Integer.parseInt(datos.getString(4));
                juegos.get(indice).setVal(datos.getInt(2));

            } while(datos.moveToNext());

            mapaTest.put(juego_nombre,juegos);
        }

        db_sesiones.close();
    }
}