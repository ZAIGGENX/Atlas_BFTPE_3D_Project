package com.example.battlefortheplanetearth;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.view.GestureDetector;


public class OpenGLES20Activity extends Activity {

    private GLSurfaceView mGLView;
    private Controls mControls;
    GestureDetector gestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gestureDetector = new GestureDetector(this, new GestureListener());
        mGLView = new MyGLSurfaceView(this);
        mControls = new Controls();
        setContentView(mGLView);
        
    }

    private int mActivePointerId;

	@Override
	public boolean onTouchEvent(MotionEvent e) {

	    // Get the pointer ID
	    mActivePointerId = e.getPointerId(0);

    	int action = (e.getAction() & MotionEvent.ACTION_MASK);
    	int pointCount = e.getPointerCount();

    	Log.d("ShaderActivity", "POINTER COUNT ["+pointCount+"]" );

	    int pointerIndex = e.findPointerIndex(mActivePointerId);

		Log.d("ShaderActivity", "POINTER INDEX ["+pointerIndex+"]" );

	    // Get the pointer's current position
	    float x = e.getX(pointerIndex);
	    float y = e.getY(pointerIndex);

		//float x = e.getX();
		//float y = e.getY();

		Controls.isPointerMove = false;

		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d("ShaderActivity", "ACTION_DOWN" );
				Controls.isPointerPressed = true;
				mControls.setXPointer(x);
				mControls.setYPointer(y);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				Log.d("ShaderActivity", "secondPonter ["+e.getX()+"]["+e.getY()+"]" );
				break;
			case MotionEvent.ACTION_UP:		// no mode
				Log.d("ShaderActivity", "ACTION_UP" );
				Controls.isPointerPressed = false;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				Log.d("ShaderActivity", "ACTION_POINTER_UP" );
				break;
			case MotionEvent.ACTION_MOVE:
				//Log.d("ShaderActivity", "MotionEvent.ACTION_MOVE ["+x+"]["+y+"]" );
				Controls.isPointerMove = true;
				mControls.setXPointer(x);
				mControls.setYPointer(y);
				break;
			default: 
				Controls.isPointerMove = false;
				break;
		}
		return gestureDetector.onTouchEvent(e);
	}

 	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

            return true;
        }
    }

}
