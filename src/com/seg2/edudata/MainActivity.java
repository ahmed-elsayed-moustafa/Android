package com.seg2.edudata;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.seg2.edudata.lists.CountryActivity;
import com.seg2.edudata.lists.IndicatorActivity;

/**
 * @author Team2-I
 * Home Screen page
 */
public class MainActivity extends Activity {
    public static final String EXTRA_COUNTRIES = "INTENT_KEY_EXTRA_COUNTRIES";
    public static final String EXTRA_TOPICNAME = "INTENT_KEY_EXTRA_TOPICNAME";
    public static final String EXTRA_TOPICID = "INTENT_KEY_EXTRA_TOPICID";

    private Typeface typeFace;
    private Button topicButton, countryButton;

    /**
     * Creates the elements on the screen
     * @param savedInstanceState The activity's saved instance state
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
        topicButton = (Button) findViewById(R.id.button_topic);
        countryButton = (Button) findViewById(R.id.button_country);
        topicButton.setTypeface(typeFace, Typeface.BOLD);
        countryButton.setTypeface(typeFace, Typeface.BOLD);
    }

    /**
     * Intent for {@link com.seg2.edudata.DisplayAppInfoActivity}
     * @param view The current view
     */
    //listener for ? button that displays information about our application
    public void displayInformation(View view) {
        Intent intent = new Intent(this, DisplayAppInfoActivity.class);
        startActivity(intent);
    }
    /**
     * Intent for {@link com.seg2.edudata.lists.CountryActivity}
     * @param view The current view
     */
    //Intent for opening country list through android:onClick via country_button
    public void browseCountry(View view) {
        Intent intent = new Intent(this, CountryActivity.class);
        startActivity(intent);
    }
    /**
     * Intent for {@link com.seg2.edudata.lists.IndicatorActivity}
     * @param view The current view
     */
    public void browseIndicator(View view) {
        Intent intent = new Intent(this, IndicatorActivity.class);
        startActivity(intent);
    }
}
