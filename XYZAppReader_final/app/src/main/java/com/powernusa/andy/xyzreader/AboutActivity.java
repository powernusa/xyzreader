package com.powernusa.andy.xyzreader;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView tv = (TextView) findViewById(R.id.programmer);
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "GreatVibes-Regular.ttf");
        tv.setTypeface(typeface);
    }

}
