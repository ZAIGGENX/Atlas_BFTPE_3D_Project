package com.example.battlefortheplanetearth;

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

public class urlList {

	public String LOG_TAG = "LOADFILE";
	Context context;

	public urlList(Context context) {

		this.context = context;

	}

	public int loadFile() {
		Log.d(LOG_TAG, "Starting loadFile");
		try {


			String FILENAME = "urls_text.txt";
			//InputStream inputStream = context.openFileInput(FILENAME);
			InputStream inputStream = context.getResources().openRawResource(R.raw.urls_text);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			ArrayList<String> mUrlList = new ArrayList<String>();
			String str = "";
			int numUrls = 0;

			//Read Whole File
			while((str = in.readLine()) != null) {
				if (!str.equals("")) {
					//if(type.equals("v")) 
					{
						Log.d(LOG_TAG, "STRING: " + str);
						mUrlList.add(str); 
					}
					
				}

			} // End Read File

			Log.d(LOG_TAG, "NUM URLS: " + mUrlList.size());

			// close the reader
			in.close();

			return 1;
		} catch (Exception e) {
			Log.d(LOG_TAG, "FOUND LOAD URL ERROR: " + e.toString());
			return 0;
		}

	}

}