package com.seg2.edudata.graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.seg2.edudata.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A basic table graph representation class.
 */
public class TableGraph extends Graph {
    private ScrollView view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.graph_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        graph = this;
        view = new ScrollView(activity);

        CountryData = new ArrayList<>();

        getGraphData(false);
        DefineChartBehaviour();
        DefineChartData();
        createGraph();


    }

    /**
     * @return the view this graph draws on.
     */
    public View getGraphView() {
        return view;
    }

    /**
     * Resets the table back to its initial state.
     */
    public void reset() {
        DefineChartData();
    }

    public void toggleCubic() {
        // no effect for this graph
    }

    public void toggleFilled() {
        // no effect for this graph
    }

    public void toggleLines() {
        // no effect for this graph
    }

    public void toggleLabels() {
        // no effect for this graph
    }

    protected void DefineChartBehaviour() {
        // no effect for this graph
    }

    protected void DefineChartData() {
        view.removeAllViews();
        TableLayout table = new TableLayout(activity);

        for (int i = 0; i < CountryData.size(); i += 1) {
            TableRow firstRow = new TableRow(activity);
            TextView country = new TextView(activity);

            country.setText(Countries.get(i).toString());
            country.setTextColor(Color.parseColor(Colours[i]));
            country.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            firstRow.addView(country);

            ImageView flag = new ImageView(getActivity());
            String countryCode = Countries.get(i).getCountryCode();
            //fixes Dominican Republic flag
            if (countryCode.toLowerCase().equals("do")) {
                countryCode = "dom";
            }
            flag.setImageResource(getActivity().getResources().getIdentifier("drawable/" + countryCode.toLowerCase()+"small", null, getActivity().getPackageName()));

            firstRow.addView(flag);
            table.addView(firstRow);

            JSONArray JA = CountryData.get(i);
            for (int j = 0; j < JA.length(); j += 1) {
                JSONObject JO;
                try {
                    JO = JA.getJSONObject(j);

                    if (seekbarChanged) {
                        if (JO.getDouble("date") >= dateFrom & JO.getDouble("date") <= dateTo) {
                            TableRow row = new TableRow(activity);

                            TextView year = new TextView(activity);
                            year.setText(String.valueOf(JO.getInt("date")));
                            year.setTypeface(typeFace);

                            TextView value = new TextView(activity);
                            value.setText(String.valueOf(JO.getDouble("value")));
                            value.setTypeface(typeFace);

                            row.addView(year);
                            row.addView(value);

                            table.addView(row);
                        }
                    } else {
                        TableRow row = new TableRow(activity);

                        TextView year = new TextView(activity);
                        year.setText(String.valueOf(JO.getInt("date")));
                        year.setTypeface(typeFace);

                        TextView value = new TextView(activity);
                        value.setText(String.valueOf(JO.getDouble("value")));
                        value.setTypeface(typeFace);

                        row.addView(year);
                        row.addView(value);

                        table.addView(row);
                    }
                    date.add(JO.getInt("date"));
                } catch (Exception ex) {

                }
            }
            table.addView(new TableRow(activity));
        }
        view.addView(table);
    }
}
