package com.seg2.edudata.graphs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.seg2.edudata.MainActivity;
import com.seg2.edudata.R;
import com.seg2.edudata.lists.Country;
import com.seg2.edudata.util.RangeSeekBar;
import com.seg2.edudata.util.RetrieveData;
import com.seg2.edudata.util.SuperAwesomeListAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Defines a basic abstract Graph interpretation.
 */
public abstract class Graph extends Fragment {
    ////////////////////////////////////////////////////////////
    // Graph
    ////////////////////////////////////////////////////////////

    /**
     * @return the view this graph draws on.
     */
    public abstract View getGraphView();

    /**
     * Resets this graph to its initial state.
     */
    public abstract void reset();

    /**
     * Toggles whether or not this graph is displaying as cubic.
     */
    public abstract void toggleCubic();

    /**
     * Toggles whether or not this graph is colouring in areas covered by the graphs data.
     */
    public abstract void toggleFilled();

    /**
     * Toggles between displaying lines and points, just lines, and just points.
     */
    public abstract void toggleLines();

    /**
     * Toggles the display of data value labels.
     */
    public abstract void toggleLabels();

    protected abstract void DefineChartBehaviour();

    protected abstract void DefineChartData();

    /**
     * Handles change of date
     */
    public void changeDate() {
        seekbarChanged = true;
        DefineChartData();
    }

    /**
     * Handles removing of line
     */
    public void removeLine(int i) {
        CountryData.remove(i);
        reset();
    }


    ////////////////////////////////////////////////////////////
    // GraphFragment
    ////////////////////////////////////////////////////////////
    protected static final String[] Colours = {"#28abe3", "#ff4d4d", "#00ff00", "#a0afa0"};

    protected static Activity activity;
    protected static ArrayList<Country> Countries;
    protected static String TopicName;
    protected static String TopicID;
    protected static ArrayList<JSONArray> CountryData;

    protected static ArrayList<Integer> dateArray;
    protected static Typeface typeFace;

    protected static ArrayList<Integer> date;
    protected static int dateFrom;
    protected static int dateTo;
    protected static boolean seekbarChanged;

    protected Graph graph;
    protected Dialog list_dialog;
    protected SuperAwesomeListAdapter arrayAdapter;
    protected SearchView search;

    /**
     * Creates the elements on the screen
     *
     * @param inflater           The inflator
     * @param container          The container
     * @param savedInstanceState The activity's saved instance state
     * @return inflator
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.graph_layout, container, false);
    }

    /**
     * Sets up the graph data
     */
    @Override
    public void onStart() {
        super.onStart();
        activity = getActivity();

        // return if already initialized
        if (Countries != null && !GraphActivity.forceGraphReload)
            return;

        // Grab extra resources from intent
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
        date = new ArrayList<>();
        try {
            @SuppressWarnings("unchecked")
            ArrayList<Country> _array = (ArrayList<Country>) extras.get(MainActivity.EXTRA_COUNTRIES);
            Countries = _array;
            TopicName = (String) extras.get(MainActivity.EXTRA_TOPICNAME);
            TopicID = (String) extras.get(MainActivity.EXTRA_TOPICID);
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
            Intent in = new Intent(getActivity(), MainActivity.class);
            startActivity(in);
        }

        TextView topic = (TextView) getActivity().findViewById(R.id.topicName);
        topic.setTypeface(typeFace, Typeface.BOLD);
        topic.setText(TopicName);
    }

    protected void getGraphData(boolean force) {
        if (!CountryData.isEmpty() && !force && !GraphActivity.forceGraphReload)
            return;

        // Construct JSON query and fetch data from server
        CountryData = new ArrayList<>();
        for (Country c : Countries) {
            String url = "http://api.worldbank.org/countries/"
                    + c.getCountryCode() + "/indicators/" + TopicID
                    + "?date=1960:2013&format=json";
            CountryData.add(new RetrieveData(url).getData());
        }
        GraphActivity.forceGraphReload = false;
    }

    protected void createGraph() {
        // topic text
        TextView topictext = ((TextView) getView().findViewById(R.id.topicName));
        topictext.setText(TopicName);
        // add graph view to graph layout
        LinearLayout graphlayout = ((LinearLayout) getView().findViewById(R.id.graph_view));
        graphlayout.removeAllViews();
        graphlayout.addView(graph.getGraphView());

        // Get the actual dates from the graph
        dateArray = date;

        // Get the range of years
        if (dateArray.size() != 0) {
            int minDate = dateArray.get(0);
            int maxDate = dateArray.get(dateArray.size() - 1);
            if (minDate > maxDate) {
                minDate = maxDate;
                maxDate = dateArray.get(0);
            }

            // Create the range seek bar if there is data for more than 2 years
            if (maxDate - minDate >= 2) {
                RangeSeekBar<Integer> seekbar = new RangeSeekBar<>(
                        minDate, maxDate, getActivity());
                ((RelativeLayout) getView().findViewById(R.id.seekbarLayout)).removeAllViews();
                RelativeLayout seekbarLayout = (RelativeLayout) getView()
                        .findViewById(R.id.seekbarLayout);
                seekbarLayout.addView(seekbar);

                final TextView fromDate = (TextView) getView().findViewById(
                        R.id.fromDate);
                final TextView toDate = (TextView) getView().findViewById(
                        R.id.toDate);

                fromDate.setText(String.valueOf(minDate));
                toDate.setText(String.valueOf(maxDate));
                fromDate.setTypeface(typeFace, Typeface.BOLD);
                toDate.setTypeface(typeFace, Typeface.BOLD);

                // add listener to the seek bar
                seekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

                    public void onRangeSeekBarValuesChanged(
                            RangeSeekBar<?> bar, Integer minValue,
                            Integer maxValue) {

                        fromDate.setText(minValue.toString());
                        toDate.setText(maxValue.toString());

                        Graph.dateFrom = minValue;
                        Graph.dateTo = maxValue;
                        graph.changeDate();
                    }
                });
            }
        }

        AddButtons();
        AddCountriesDialog();
    }

    private void AddButtons() {
        // Add removable buttons for selected countries
        GridLayout layout = (GridLayout) getView().findViewById(R.id.graph_textview);
        layout.removeAllViews();

        for (int i = 0; i < Countries.size(); i++) {
            TextView amendText = (TextView) getActivity().findViewById(R.id.textView);
            amendText.setTypeface(typeFace, Typeface.BOLD);
            final Button countryButton = new Button(getActivity());
            String CountryString = "" + Countries.get(i).toString();
            countryButton.setTextColor(Color.parseColor(Colours[i]));
            countryButton.setTextSize(12);
            Typeface crossFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DejaVuSans.ttf");
            countryButton.setTypeface(crossFont, Typeface.BOLD);
            String cross = "\u2612" + " ";
            countryButton.setText(cross + CountryString);
            countryButton.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(5, 7, 5, 7);

            countryButton.setLayoutParams(params);
            countryButton.setSingleLine(true);
            layout.addView(countryButton);
            layout.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);

            countryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Removes the country from the list
                    if (Countries.size() > 1) {
                        for (int i = 0; i < Countries.size(); i++) {
                            String countryButtonName = countryButton.getText().toString().substring(2);
                            if (countryButtonName.equals(Countries.get(i).toString())) {
                                Countries.remove(i);
                                graph.removeLine(i);
                                AddButtons();
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void AddCountriesDialog() {
        Button btn = (Button) getView().findViewById(R.id.addCountryButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_dialog = new Dialog(getActivity());
                list_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                list_dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.WHITE));
                list_dialog.setContentView(R.layout.dialog_layout);
                ListView list = (ListView) list_dialog
                        .findViewById(R.id.dialoglist);
                arrayAdapter = new SuperAwesomeListAdapter(getActivity(),
                        createCountryInfoData(), TopicID);

                search = (SearchView) list_dialog.findViewById(R.id.searchView1);
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        arrayAdapter.getFilter().filter(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        arrayAdapter.getFilter().filter(query);
                        return false;
                    }
                });
                list.setTextFilterEnabled(true);
                list.setAdapter(arrayAdapter);
                Button doneButton = (Button) list_dialog
                        .findViewById(R.id.cancel);
                doneButton.setTypeface(typeFace, Typeface.BOLD);
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        ArrayList<Country> temp = arrayAdapter.getCheckedCountries();
                        if (temp.size() > 0 && temp.size() < 5) {
                            Countries = temp;
                            getGraphData(true);
                            createGraph();
                            graph.reset();
                            list_dialog.dismiss();
                        }

                    }
                });
                Log.e("List of Selected Countries", Countries.toString());
                arrayAdapter.checkCountries(Countries);
                list_dialog.show();
            }
        });
    }

    private ArrayList<Country> createCountryInfoData() {
        ArrayList<Country> countryArrayList = new ArrayList<>();
        String sURL = "http://api.worldbank.org/country?per_page=300&region=WLD&format=json";
        JSONArray array = new RetrieveData(sURL).getData();
        if (array != null) {
            JSONObject obj;
            for (int i = 0; i < array.length(); i++) { // Cycles through each
                // array {}
                try {
                    obj = array.getJSONObject(i);
                    if (!obj.get("capitalCity").equals("")) {
                        countryArrayList.add(new Country(obj)); // Gets the
                        // value at name
                        // key
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(
                    getActivity(),
                    "There is a problem accessing the data. Please check your internet connection.",
                    Toast.LENGTH_LONG).show();
        }
        Collections.sort(countryArrayList, Country.CountryCompare);
        return countryArrayList;
    }
}