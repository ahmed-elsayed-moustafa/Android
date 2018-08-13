package com.example.countrieswithflags;

import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CountriesListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
 
	public CountriesListAdapter(Context context, String[] values) {
		super(context, R.layout.country_list_item, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View viewCountryRow = inflater.inflate(R.layout.country_list_item, parent, false);
		TextView countryView = (TextView) viewCountryRow.findViewById(R.id.txtViewCountryName);
		ImageView flagView = (ImageView) viewCountryRow.findViewById(R.id.imgViewFlag);
		
    	String[] countries=values[position].split(",");
    	countryView.setText(GetCountryCode(countries[1]).trim());

    	String flags = countries[1].trim().toLowerCase();
    	flagView.setImageResource(context.getResources().getIdentifier("drawable/" + flags, null, context.getPackageName()));
		return viewCountryRow;
	}
	
	private String GetCountryCode(String ssid){
        Locale loc = new Locale("", ssid);
        return loc.getDisplayCountry().trim();
    }
}