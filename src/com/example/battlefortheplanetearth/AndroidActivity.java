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
import android.widget.ListView;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import android.app.Activity;
import java.io.FileInputStream; 
import android.util.Log;
import android.content.Context;

public class AndroidActivity extends Activity {

	ListView listView;
	Context context;
	public String LOG_TAG = "LOADFILE";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        context = this.getApplicationContext();
        ArrayList<String> mUrlList = new ArrayList<String>();

		try {
			String FILENAME = "urls_text.txt";
			//InputStream inputStream = context.openFileInput(FILENAME);
			InputStream inputStream = context.getResources().openRawResource(R.raw.urls_text);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			String str = "";
			int count;
			//Read Whole File
			while((str = in.readLine()) != null) {
				if (!str.equals("")) {
						mUrlList.add(str);
				}

			} // End Read File
			in.close();
		} catch (Exception e) { }

		// Define Array values to show
		String[] values = new String[mUrlList.size()];
		values = mUrlList.toArray(values);

		// Get ListView object from xml
        listView = (ListView) findViewById(R.id.listview);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter); 

    }
}
