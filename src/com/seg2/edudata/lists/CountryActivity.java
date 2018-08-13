package com.seg2.edudata.lists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.seg2.edudata.MainActivity;
import com.seg2.edudata.R;
import com.seg2.edudata.graphs.GraphActivity;
import com.seg2.edudata.util.RetrieveData;
import com.seg2.edudata.util.SuperAwesomeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryActivity extends Activity implements
        SearchView.OnQueryTextListener {
    private ListView listView;
    private Context context = this;
    private String indicatorName;
    private String indicatorId;
    private SuperAwesomeListAdapter arrayAdapter;
    private ArrayList<Country> countries;
    private SearchView searchView;
    private ProgressDialog PD;
    private static final int SPEECH_REQUEST_CODE = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Standard Android stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_main);

        // Need an Array Adapter for manipulating(Adding/Deleting) items on the list view
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
        // font from http://www.ephifonts.com/free-helvetica-font-helvetica-neue-ultra-light-complete.html
        listView = (ListView) findViewById(R.id.listView);
        indicatorName = getIntent().getStringExtra(MainActivity.EXTRA_TOPICNAME);
        indicatorId = getIntent().getStringExtra(MainActivity.EXTRA_TOPICID);
        if(isOnline()== true) {
            new MyAsync().execute();
        } else {
            Toast.makeText(
                    this,
                    "There is a problem accessing the data. Please check your internet connection.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private class MyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PD = new ProgressDialog(CountryActivity.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading Countries...");
            PD.setCancelable(false);
            PD.show();
        }

        protected Void doInBackground(Void... params) {

            createData();
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            fillCountryList();
            PD.dismiss();
        }
    }

    public void fillCountryList() {
        arrayAdapter = new SuperAwesomeListAdapter(this, createData(),indicatorId);
        listView.setTextFilterEnabled(true);
        listView.setAdapter(arrayAdapter);
    }

    public ArrayList<Country> createData() {
        ArrayList<Country> countryArrayList = new ArrayList<Country>();
        String sURL = "http://api.worldbank.org/country?per_page=300&region=WLD&format=json";
        JSONArray array = new RetrieveData(sURL).getData();
        JSONObject obj;
        if (array !=null) {
            for (int i = 0; i < array.length(); i++) { // Cycles through each array
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
        }

        Collections.sort(countryArrayList, Country.CountryCompare);
        return countryArrayList;
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.filterText:
                countries = arrayAdapter.getCheckedCountries();
                String countryList = countries.toString().replace("[", "")
                        .replace("]", "");
                if (countries.isEmpty()) {
                    Toast.makeText(
                            this,
                            "You haven't selected anything. Please select at least one country.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder confirmDialog = new AlertDialog.Builder(
                            this);
                    confirmDialog
                            .setMessage("You have selected: " + countryList)
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            showNextScreen();
                                        }
                                    })
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                        }
                                    });
                    AlertDialog alertDialog = confirmDialog.create();
                    alertDialog.show();
                    alertDialog.getWindow().getAttributes();
                    Typeface typeFace = Typeface.createFromAsset(getAssets(),
                            "fonts/HelveticaNeue-UltraLight.otf"); // font from
                    // http://www.ephifonts.com/free-helvetica-font-helvetica-neue-ultra-light-complete.html
                    TextView text = (TextView) alertDialog
                            .findViewById(android.R.id.message);
                    text.setTypeface(typeFace);
                    Button ok = alertDialog
                            .getButton(DialogInterface.BUTTON_POSITIVE);
                    Button cancel = alertDialog
                            .getButton(DialogInterface.BUTTON_NEGATIVE);
                    ok.setTypeface(typeFace, Typeface.BOLD);
                    cancel.setTypeface(typeFace, Typeface.BOLD);
                }
                return true;
            case R.id.action_search:
                return true;
            case R.id.microphone:
                displaySpeechRecognizer();
                return true;
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

    public void showNextScreen() {

        if (indicatorId == null) {
            Intent intent = new Intent(this, IndicatorActivity.class);
            intent.putExtra(MainActivity.EXTRA_COUNTRIES, countries);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, GraphActivity.class);
            intent.putExtra(MainActivity.EXTRA_TOPICNAME, indicatorName);
            intent.putExtra(MainActivity.EXTRA_TOPICID, indicatorId);
            intent.putExtra(MainActivity.EXTRA_COUNTRIES, countries);
            GraphActivity.forceGraphReload = true;
            startActivity(intent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        System.out.println(query);
        arrayAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        arrayAdapter.getFilter().filter(query);
        return false;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}