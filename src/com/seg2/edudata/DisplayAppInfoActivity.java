package com.seg2.edudata;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.w3c.dom.Text;

/**
 * Created by Diana Ghitun on 21/11/2014.
 */
public class DisplayAppInfoActivity extends Activity {
    private Typeface typeFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_app_info);
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
        TextView textView=(TextView) findViewById(R.id.text_app_info);
        textView.setTypeface(typeFace, Typeface.BOLD);
    }

    public void backToMainPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}