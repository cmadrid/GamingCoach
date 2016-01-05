package layout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import ami.coach.game.gamingcoach.R;



public class Chart extends Fragment {

    LineChart mLineChart;


    public static Chart newInstance() {
        Chart fragment = new Chart();
        return fragment;
    }

    public Chart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View V = inflater.inflate(R.layout.fragment_chart, container, false);
        mLineChart = (LineChart) V.findViewById(R.id.mLineChart);
        addChart();


        return V;

    }

    public void addChart() {

        // add the programmatically created chart
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

        Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
        valsComp1.add(c1e2);
        Entry c1e3 = new Entry(70.000f, 2); // 0 == quarter 3
        valsComp1.add(c1e3);
        Entry c1e4 = new Entry(85.000f, 3); // 1 == quarter 4 ...
        valsComp1.add(c1e4);
        Entry c1e5 = new Entry(100.000f, 4); // 1 == quarter 4 ...
        valsComp1.add(c1e5);
        Entry c1e6 = new Entry(150.000f, 5); // 1 == quarter 4 ...
        valsComp1.add(c1e6);

        Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
        valsComp2.add(c2e1);
        Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
        valsComp2.add(c2e2);
        Entry c2e3 = new Entry(75.000f, 2); // 0 == quarter 3
        valsComp2.add(c2e3);
        Entry c2e4 = new Entry(125.000f, 3); // 1 == quarter 4 ...
        valsComp2.add(c2e4);
        Entry c2e5 = new Entry(95.000f, 4); // 1 == quarter 4 ...
        valsComp2.add(c2e5);
        Entry c2e6 = new Entry(105.000f, 5); // 1 == quarter 4 ...
        valsComp2.add(c2e5);

        LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");
        setComp1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        LineDataSet setComp2 = new LineDataSet(valsComp2, "Company 2");
        setComp2.setColor(ColorTemplate.LIBERTY_COLORS[3]);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q");
        xVals.add("2.Q");
        xVals.add("3.Q");
        xVals.add("4.Q");
        xVals.add("5.Q");
        xVals.add("6.Q");

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate(); // refresh

    }
}