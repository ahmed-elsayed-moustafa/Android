package com.seg2.edudata.lists;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.seg2.edudata.R;
import com.seg2.edudata.util.RetrieveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

/**
 * Created by CheokHo on 26/11/2014.
 */
public class CountryDialog {
    private Dialog dialog;
    private Typeface typeFace;
    private TextView countryText, capitalText, longitudeText, latitudeText, populationText, starText;

    public void showCountryInfo(Country country, Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
        dialog.setContentView(R.layout.countrydialog);
        countryText = (TextView) dialog.findViewById(R.id.countryText);
        capitalText = (TextView) dialog.findViewById(R.id.capitalText);
        longitudeText = (TextView) dialog.findViewById(R.id.longitudeText);
        latitudeText = (TextView) dialog.findViewById(R.id.latitudeText);
        populationText = (TextView) dialog.findViewById(R.id.popText);
        starText = (TextView) dialog.findViewById(R.id.starText);
        countryText.setTypeface(typeFace);
        capitalText.setTypeface(typeFace);
        longitudeText.setTypeface(typeFace);
        latitudeText.setTypeface(typeFace);
        populationText.setTypeface(typeFace);
        starText.setTypeface(typeFace);

        countryText.append(" " + country.toString());
        String countryCode = country.getCountryCode();
        String initUrl1 = "http://api.worldbank.org/countries/";
        String initUrl2 = "/indicators/SP.POP.TOTL?date=2013&format=json";
        String finalUrl = initUrl1 + countryCode + initUrl2;
        JSONArray data = new RetrieveData(finalUrl).getData();
        if (data != null) {
            try {
                JSONObject popData = data.getJSONObject(0);
                Double population = popData.getDouble("value");
                populationText.append(" " + NumberFormat.getNumberInstance().format(population));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        capitalText.append(" " + country.getCapital());
        longitudeText.append(" " + country.getLongitude());
        latitudeText.append(" " + country.getLatitude());

        ImageView flag = (ImageView) dialog.findViewById(R.id.flagPicture); //Flags taken from http://icondrawer.com/flag-icons.php ; acknowledge required.
        //fixes Dominican Republic flag
        if (countryCode.toLowerCase().equals("do")) {
            countryCode = "dom";
        }
        flag.setImageResource(context.getResources().getIdentifier("drawable/" + countryCode.toLowerCase(), null, context.getPackageName()));

        dialog.show();
    }
}