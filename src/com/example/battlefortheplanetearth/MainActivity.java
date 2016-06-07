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

/*public class MainActivity extends Activity {

	LinearLayout mLinearLayout;

    // Called when the activity is first created. /
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

		  // Create a LinearLayout in which to add the ImageView
		  mLinearLayout = new LinearLayout(this);

		  // Instantiate an ImageView and define its properties
		  ImageView i = new ImageView(this);
		  i.setImageResource(R.drawable.my_image);
		  i.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
		  i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
		      LayoutParams.WRAP_CONTENT));

		  // Add the ImageView to the layout and set the layout as the content view
		  mLinearLayout.addView(i);
		  setContentView(mLinearLayout);

    }
}*/

public class MainActivity extends Activity {

	private CanvasView customCanvas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_canvas);

		customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
	}

	public void clearCanvas(View v) {
		customCanvas.clearCanvas();
	}

	public void switchColor(View v) {
		customCanvas.switchColor();
	}

}

