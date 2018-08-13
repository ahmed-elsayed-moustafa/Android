package com.seg2.edudata.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.seg2.edudata.lists.Indicator;

import java.util.ArrayList;

/**
 * Created by CheokHo on 04/12/2014.
 */
public class IndicatorAdapter extends ArrayAdapter<Indicator> {
    private Typeface typeFace;

    public IndicatorAdapter(Context context, int resource, ArrayList<Indicator> listOfIndicator) {
        super(context, resource, listOfIndicator);
        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-UltraLight.otf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView textView = (TextView) v.findViewById(android.R.id.text1);
        textView.setTypeface(typeFace);
        return textView;
    }
}
