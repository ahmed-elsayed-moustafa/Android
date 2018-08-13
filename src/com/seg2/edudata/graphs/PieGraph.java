package com.seg2.edudata.graphs;

import android.graphics.Color;
import android.widget.Toast;
import lecho.lib.hellocharts.model.ArcValue;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.PieChartView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A basic pie graph representation class.
 */
public class PieGraph extends Graph {


    private PieChartView view;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = true;
    private boolean hasLabelForSelected = false;
    private boolean isExploaded = false;
    private boolean hasArcSeparated = false;

    @Override
    public void onStart() {
        super.onStart();

        graph = this;
        view = new PieChartView(activity);

        CountryData = new ArrayList<>();

        getGraphData(false);
        DefineChartBehaviour();
        DefineChartData();
        createGraph();
        setHasOptionsMenu(true);

    }

    /**
     * @return the view this graph is drawing on.
     */
    public PieChartView getGraphView() {
        return view;
    }

    /**
     * Resets the graph to its initial state.
     */
    public void reset() {
        hasLabels = true;
        hasLabelsOutside = true;
        hasLabelForSelected = false;
        isExploaded = false;
        hasArcSeparated = false;
        DefineChartData();
    }

    /**
     * Toggles whether or not this pie chart's arcs are separated.
     */
    public void toggleCubic() {
        hasArcSeparated = !hasArcSeparated;
        if (hasArcSeparated) {
            isExploaded = false;
        }
        DefineChartData();
    }

    /**
     * Toggles whether or not this pie chart is exploaded.
     */
    public void toggleFilled() {
        isExploaded = !isExploaded;
        DefineChartData();
    }

    public void toggleLines() {
        // no effect for this graph
    }

    /**
     * Toggles whether or not the data value labels are displayed.
     */
    public void toggleLabels() {
        if (hasLabels & hasLabelsOutside) {
            hasLabels = false;
        } else if (hasLabels & !hasLabelsOutside) {
            hasLabels = false;
            hasLabelsOutside = true;
        } else {
            hasLabelsOutside = true;
        }

        DefineChartData();
    }

    protected void DefineChartBehaviour() {
        view.setOnValueTouchListener(new PieChartView.PieChartOnValueTouchListener() {
            private Toast t;

            @Override
            public void onValueTouched(int selectedArc, ArcValue arc) {
                if (t != null)
                    t.cancel();
                t = Toast.makeText(activity, "" + arc.getValue(), Toast.LENGTH_LONG);
                t.getView().setBackgroundColor(
                        Color.parseColor(Colours[selectedArc]));
                t.show();
            }

            @Override
            public void onNothingTouched() {
                if (t != null)
                    t.cancel();
            }
        });
    }

    protected void DefineChartData() {
        ArrayList<ArcValue> arcs = new ArrayList<>();

        for (int i = 0; i < CountryData.size(); i += 1) {
            JSONArray JA = CountryData.get(i);
            ArcValue arc = new ArcValue();

            for (int j = 0; j < JA.length(); j += 1) {
                JSONObject JO;
                try {
                    JO = JA.getJSONObject(j);
                    if (seekbarChanged) {
                        if (JO.getDouble("date") >= dateFrom & JO.getDouble("date") <= dateTo) {
                            arc.setValue(arc.getValue() + (float) JO.getDouble("value"));
                        }
                    } else {
                        arc.setValue(arc.getValue() + (float) JO.getDouble("value"));
                    }
                    date.add(JO.getInt("date"));

                } catch (Exception ex) {

                }
            }

            arc.setColor(Color.parseColor(Colours[i]));
            if (isExploaded) {
                arc.setArcSpacing(24);
            }
            if (hasArcSeparated && i == 0) {
                arc.setArcSpacing(32);
            }
            arcs.add(arc);
        }

        PieChartData chartdata = new PieChartData(arcs);
        chartdata.setHasLabels(hasLabels);
        chartdata.setHasLabelsOnlyForSelected(hasLabelForSelected);
        chartdata.setHasLabelsOutside(hasLabelsOutside);
        chartdata.setHasCenterCircle(false);
        view.setPieChartData(chartdata);

    }
}