package com.seg2.edudata.lists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.seg2.edudata.MainActivity;
import com.seg2.edudata.R;
import com.seg2.edudata.graphs.GraphActivity;
import com.seg2.edudata.util.IndicatorAdapter;
import com.seg2.edudata.util.RetrieveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class IndicatorActivity extends Activity implements SearchView.OnQueryTextListener {
    private ListView listView;
    private ArrayList<Indicator> listOfIndicator;
    private JSONArray temp;
    private ArrayList<Country> selectedCountries;
    private SearchView searchView;
    private static final int SPEECH_REQUEST_CODE =0;
    private IndicatorAdapter indicatorAdapter;
    private ArrayList<Country> countriesWithData;
    private String name;
    private String idString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicator_main);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-UltraLight.otf"); //font from http://www.ephifonts.com/free-helvetica-font-helvetica-neue-ultra-light-complete.html
        listView = (ListView) findViewById(R.id.listView);
        listOfIndicator = new ArrayList<Indicator>();
        selectedCountries = getIntent().getParcelableArrayListExtra(MainActivity.EXTRA_COUNTRIES);
        populateIndicators();


        Collections.sort(listOfIndicator, Indicator.IndicatorCompare);
        indicatorAdapter = new IndicatorAdapter(this, android.R.layout.simple_list_item_1, listOfIndicator);
        listView.setAdapter(indicatorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                name = indicatorAdapter.getItem(position).getName();
                idString = indicatorAdapter.getItem(position).getId();
                System.out.println(name);
                System.out.println(idString);
                System.out.println(selectedCountries);
                if (selectedCountries == null) {
                    Intent intent = new Intent(view.getContext(), CountryActivity.class);
                    intent.putExtra(MainActivity.EXTRA_TOPICNAME, name);
                    intent.putExtra(MainActivity.EXTRA_TOPICID, idString);
                    startActivity(intent);
                } else {
                    //Check if there is data for the selected countries
                    countriesWithData = new ArrayList<Country>();
                    String tmp = "";
                    for (int i = 0; i < selectedCountries.size();i++){
                        if(checkDataOfCountry(selectedCountries.get(i), idString)){
                            countriesWithData.add(selectedCountries.get(i));
                            continue;
                        }
                        tmp = tmp + selectedCountries.get(i).toString()+",";
                    }
                    Typeface typeFace = Typeface.createFromAsset(getAssets(),
                            "fonts/HelveticaNeue-UltraLight.otf"); // font from
                    // http://www.ephifonts.com/free-helvetica-font-helvetica-neue-ultra-light-complete.html

                    if(countriesWithData.size() == selectedCountries.size()){
                        //Open Graph screen
                        openGraphActivity(countriesWithData);
                    }
                    else if (countriesWithData.size() < selectedCountries.size() && !countriesWithData.isEmpty()) {
                        //Open dialog then open graph

                        //creating a Dialog Message to inform user about the validity of selected countries
                        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(IndicatorActivity.this);
                        confirmDialog.setMessage("There is no data to show for the following: " + tmp).setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openGraphActivity(countriesWithData);
                                    }
                                })
                                .setNegativeButton(R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                        AlertDialog alertDialog = confirmDialog.create();
                        alertDialog.show();
                        alertDialog.getWindow().getAttributes();

                        TextView text = (TextView) alertDialog
                                .findViewById(android.R.id.message);
                        text.setTypeface(typeFace);
                        Button ok = alertDialog
                                .getButton(DialogInterface.BUTTON_POSITIVE);
                        Button cancel = alertDialog
                                .getButton(DialogInterface.BUTTON_NEGATIVE);
                        ok.setTypeface(typeFace, Typeface.BOLD);
                        cancel.setTypeface(typeFace, Typeface.BOLD);
                    } else {
                        //When all countries have no data
                        //Dont open graph
                        AlertDialog.Builder errorDialog = new AlertDialog.Builder(IndicatorActivity.this);
                        errorDialog.setTitle("Error");
                        errorDialog.setMessage("Sorry! There is no data available for what you've selected.").setPositiveButton(R.string.ok, null);
                        errorDialog.setIcon(R.drawable.error_icon);
                        AlertDialog alertDialog=errorDialog.create();
                        alertDialog.show();
                        alertDialog.getWindow().getAttributes();
                        TextView text = (TextView) alertDialog.findViewById(android.R.id.message);
                        Button ok = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        text.setTypeface(typeFace); ok.setTypeface(typeFace);

                    }



                }
            }
        });


    }

    public Boolean checkDataOfCountry(Country c, String idString){

        String url = "http://api.worldbank.org/countries/"
                + c.getCountryCode() + "/indicators/" + idString
                + "?date=1960:2013&format=json";
        System.out.println(url);

        JSONArray array = new RetrieveData(url).getData();
        if (array != null) {
            JSONObject obj;
            for (int j = 0; j < array.length(); j++) { // Cycles through each array
                try {
                    obj = array.getJSONObject(j);
                    if (!obj.isNull("value")) {
                        return true;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void openGraphActivity(ArrayList<Country> temp){
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra(MainActivity.EXTRA_TOPICNAME, name);
        intent.putExtra(MainActivity.EXTRA_TOPICID, idString);
        intent.putExtra(MainActivity.EXTRA_COUNTRIES, temp);
        GraphActivity.forceGraphReload = true;
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search Here");

        // Delete done button from the action bar
        menu.removeItem(R.id.filterText);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.microphone:
                displaySpeechRecognizer();
                return true;
            case android.R.id.home:
                finish();
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    private void displaySpeechRecognizer() {
        if (getPackageManager().hasSystemFeature("android.hardware.microphone")) {
            PackageManager pm = getPackageManager();
            List<?> activities = pm.queryIntentActivities(new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (activities.size() > 0) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
                Toast toast = Toast.makeText(this,
                        "Loading Voice recognizer...", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast.makeText(this,
                        "This action is not available on this device.",
                        Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(this,
                    "This action is not available on this device.",
                    Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            searchView.setQuery(spokenText, false);
            searchView.clearFocus();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void populateIndicators() {
        listOfIndicator.add(new Indicator("Children out of school, primary, female", "SE.PRM.UNER.FE"));
        listOfIndicator.add(new Indicator("Children out of school, primary, male", "SE.PRM.UNER.MA"));
        listOfIndicator.add(new Indicator("Expenditure per student, primary (% of GDP per capita)", "SE.XPD.PRIM.PC.ZS"));
        listOfIndicator.add(new Indicator("Expenditure per student, secondary (% of GDP per capita)", "SE.XPD.SECO.PC.ZS"));
        listOfIndicator.add(new Indicator("Expenditure per student, tertiary (% of GDP per capita)", "SE.XPD.TERT.PC.ZS"));
        listOfIndicator.add(new Indicator("Gross intake ratio in first grade of primary education, female (% of relevant age group)", "SE.PRM.GINT.FE.ZS"));
        listOfIndicator.add(new Indicator("Gross intake ratio in first grade of primary education, male (% of relevant age group)", "SE.PRM.GINT.MA.ZS"));
        listOfIndicator.add(new Indicator("Labor force, total", "SL.TLF.TOTL.IN"));
        listOfIndicator.add(new Indicator("Literacy rate, adult total (% of people ages 15 and above)", "SE.ADT.LITR.ZS"));
        listOfIndicator.add(new Indicator("Literacy rate, youth female (% of females ages 15-24)", "SE.ADT.1524.LT.FE.ZS"));
        listOfIndicator.add(new Indicator("Literacy rate, youth male (% of males ages 15-24)", "SE.ADT.1524.LT.MA.ZS"));
        listOfIndicator.add(new Indicator("Literacy rate, youth total (% of people ages 15-24)", "SE.ADT.1524.LT.ZS"));
        listOfIndicator.add(new Indicator("Mortality rate, under-5 (per 1,000 live births)", "SH.DYN.MORT"));
        listOfIndicator.add(new Indicator("Persistence to last grade of primary, female (% of cohort)", "SE.PRM.PRSL.FE.ZS"));
        listOfIndicator.add(new Indicator("Persistence to last grade of primary, male (% of cohort)", "SE.PRM.PRSL.MA.ZS"));
        listOfIndicator.add(new Indicator("Population ages 0-14 (% of total)", "SP.POP.0014.TO.ZS"));
        listOfIndicator.add(new Indicator("Population ages 15-64 (% of total)", "SP.POP.1564.TO.ZS"));
        listOfIndicator.add(new Indicator("Prevalence of HIV, total (% of population ages 15-49)", "SH.DYN.AIDS.ZS"));
        listOfIndicator.add(new Indicator("Primary completion rate, female (% of relevant age group)", "SE.PRM.CMPT.FE.ZS"));
        listOfIndicator.add(new Indicator("Primary completion rate, male (% of relevant age group)", "SE.PRM.CMPT.MA.ZS"));
        listOfIndicator.add(new Indicator("Primary completion rate, total (% of relevant age group)", "SE.PRM.CMPT.ZS"));
        listOfIndicator.add(new Indicator("Progression to secondary school, female (%)", "SE.SEC.PROG.FE.ZS"));
        listOfIndicator.add(new Indicator("Progression to secondary school, male (%)", "SE.SEC.PROG.MA.ZS"));
        listOfIndicator.add(new Indicator("Public spending on education, total (% of GDP)", "SE.XPD.TOTL.GD.ZS"));
        listOfIndicator.add(new Indicator("Public spending on education, total (% of government expenditure)", "SE.XPD.TOTL.GB.ZS"));
        listOfIndicator.add(new Indicator("Pupil-teacher ratio, primary)", "SE.PRM.ENRL.TC.ZS"));
        listOfIndicator.add(new Indicator("Ratio of female to male primary enrollment (%)", "SE.ENR.PRIM.FM.ZS"));
        listOfIndicator.add(new Indicator("Ratio of female to male secondary enrollment (%)", "SE.ENR.SECO.FM.ZS"));
        listOfIndicator.add(new Indicator("Ratio of female to male tertiary enrollment (%)", "SE.ENR.TERT.FM.ZS"));
        listOfIndicator.add(new Indicator("Ratio of girls to boys in primary and secondary education (%)", "SE.ENR.PRSC.FM.ZS"));
        listOfIndicator.add(new Indicator("Repeaters, primary, female (% of female enrollment)", "SE.PRM.REPT.FE.ZS"));
        listOfIndicator.add(new Indicator("Repeaters, primary, male (% of male enrollment)", "SE.PRM.REPT.MA.ZS"));
        listOfIndicator.add(new Indicator("School enrollment, preprimary (% gross)", "SE.PRE.ENRR"));
        listOfIndicator.add(new Indicator("School enrollment, primary (% gross)", "SE.PRM.ENRR"));
        listOfIndicator.add(new Indicator("School enrollment, primary (% net)", "SE.PRM.NENR"));
        listOfIndicator.add(new Indicator("School enrollment, secondary (% gross)", "SE.SEC.ENRR"));
        listOfIndicator.add(new Indicator("School enrollment, secondary (% net)", "SE.SEC.NENR"));
        listOfIndicator.add(new Indicator("School enrollment, tertiary (% gross)", "SE.TER.ENRR"));
        listOfIndicator.add(new Indicator("Trained teachers in primary education (% of total teachers)", "SE.PRM.TCAQ.ZS"));
        listOfIndicator.add(new Indicator("Unemployment, female (% of female labor force) (modeled ILO estimate)", "SL.UEM.TOTL.FE.ZS"));
        listOfIndicator.add(new Indicator("Unemployment, male (% of male labor force) (modeled ILO estimate)", "SL.UEM.TOTL.MA.ZS"));
        listOfIndicator.add(new Indicator("Unemployment, total (% of total labor force) (modeled ILO estimate)", "SL.UEM.TOTL.ZS"));
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        indicatorAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        indicatorAdapter.getFilter().filter(newText);
        return false;
    }
}