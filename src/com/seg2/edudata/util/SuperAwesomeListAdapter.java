package com.seg2.edudata.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.seg2.edudata.R;
import com.seg2.edudata.lists.Country;
import com.seg2.edudata.lists.CountryDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperAwesomeListAdapter extends ArrayAdapter<Country> {
    private Typeface typeFace;
    private Context context;
    private ArrayList<Country> countryArrayList;
    private String indicatorId;
    private int k = 0;

    //Lets you customise a listview to have different designs stored in each cell.

    public SuperAwesomeListAdapter(Context context, ArrayList<Country> countryArrayList,String indicatorId) {
        super(context, R.layout.listview, countryArrayList);
        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
        this.indicatorId = indicatorId;
        this.context = context;
        this.countryArrayList = countryArrayList;
    }

    private class ViewHolder {
        CheckBox countryCheck;
        Button infoButton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder; //Creates widgets which are stored in each individual row.
        if (row == null) { //Checks listview row is empty before adding ViewHolder and assigning tag.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview, parent, false);
            holder = new ViewHolder();
            holder.countryCheck = (CheckBox) row.findViewById(R.id.countryCheckBox);
            holder.infoButton = (Button) row.findViewById(R.id.informationButton);
            row.setTag(holder);
        } else { //gets row if it is filled.
            holder = (ViewHolder) row.getTag();
        }

        holder.countryCheck.setOnCheckedChangeListener(null);
        holder.countryCheck.setChecked(false);
        holder.countryCheck.setChecked(getItem(position).isSelected());
        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country country = getItem(position);
                new CountryDialog().showCountryInfo(country, context);
            }
        });
        holder.countryCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //a choosen country will be checked whether is contain information for selected indicator
                if (isChecked & k < 4) {
                    if (indicatorId != null) {
                        boolean isEmpty = false;
                        Country c = getItem(position);
                        String url = "http://api.worldbank.org/countries/"
                                + c.getCountryCode() + "/indicators/" + indicatorId
                                + "?date=1960:2013&format=json";
                        System.out.println(url);
                        RetrieveData tmp = new RetrieveData(url);
                        //System.out.println("the data is " + tmp.getData());

                        JSONArray array = tmp.getData();
                        if (array != null) {
                            JSONObject obj;
                            for (int i = 0; i < array.length(); i++) { // Cycles through each
                                // array {}
                                try {
                                    obj = array.getJSONObject(i);

                                    if (!obj.isNull("value")) {
                                        isEmpty = true;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (isEmpty == false) {
                            Toast.makeText(context, "There is no data available for this country! Please choose another one", Toast.LENGTH_SHORT).show();
                            holder.countryCheck.setChecked(false);
                        } else {
                            getItem(position).setSelected(true);
                            k++;
                        }
                    }
                    else{
                        getItem(position).setSelected(true);
                        k++;
                    }
                }
                else if(!isChecked){
                    getItem(position).setSelected(isChecked);

                    k--;
                }
                else {
                    Toast.makeText(context,"You can only select 4 countries to compare.", Toast.LENGTH_SHORT).show();;
                    holder.countryCheck.setChecked(false);
                }

            }
        });

        Country country = getItem(position);
        holder.countryCheck.setText(country.toString());
        holder.countryCheck.setTypeface(typeFace);
        return row;
    }

    public ArrayList<Country> getCheckedCountries() {
        ArrayList<Country> checkedCountries = new ArrayList<Country>();

        for (int i = 0; i < countryArrayList.size(); i++) {
            if (countryArrayList.get(i).isSelected()) {

                checkedCountries.add(countryArrayList.get(i)); //needs to get element.
            }
        }
        return checkedCountries;
    }

    public void checkCountries(ArrayList<Country> selectedCountries){
        for(Country c : countryArrayList){
            for(Country s : selectedCountries){
                if(s.toString().equals(c.toString())){
                    c.setSelected(true);
                }
            }
        }
    }
}