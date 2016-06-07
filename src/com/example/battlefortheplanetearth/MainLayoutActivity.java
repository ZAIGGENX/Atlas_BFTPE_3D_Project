package com.example.battlefortheplanetearth;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Gallery;
import android.widget.TextView;
import android.view.MenuInflater;
import android.view.Menu;

public class MainLayoutActivity extends Activity {

	LinearLayout mLinearLayout;

    // Called when the activity is first created. /
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_simple_layout);
		
		TextView textElement = (TextView) findViewById(R.id.text);
		textElement.setTextColor(0xFF00FF00); //this is green color
		//textElement.setText("FUCK THIS SHIT!");
		
		//mLinearLayout = new LinearLayout(this);
		//setContentView(mLinearLayout);

    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
 
        return super.onCreateOptionsMenu(menu);
    }*/
}

