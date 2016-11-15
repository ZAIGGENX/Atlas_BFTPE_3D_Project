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

    private final int MAX_POINTERS = 4;
    private int [] mActivePointerId = new int[MAX_POINTERS];
    private int [] pointerIndex = new int[MAX_POINTERS];

	@Override
	public boolean onTouchEvent(MotionEvent e) {

    	int action = (e.getAction() & MotionEvent.ACTION_MASK);
    	int pointCount = e.getPointerCount();

    	//Log.d("pointer", "POINTER COUNT ["+pointCount+"]" );

    	float x[] = new float[MAX_POINTERS];
    	float y[] = new float[MAX_POINTERS];

	    // Get the pointer ID
	    if(pointCount<=MAX_POINTERS){
		    for(int i=0;i<pointCount;i++){
		    	mActivePointerId[i] = e.getPointerId(i);
		    	pointerIndex[i] = e.findPointerIndex(mActivePointerId[i]);
		    	x[i] = e.getX(pointerIndex[i]);
		    	y[i] = e.getY(pointerIndex[i]);
		    	//Log.d("pointer", "POINTER X ["+i+"] = "+x[i] );
		    	//Log.d("pointer", "POINTER Y ["+i+"] = "+y[i] );
		    }
		}

		mControls.isPointerMove = false;

		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//Log.d("ShaderActivity", "ACTION_DOWN" );
				mControls.isPointerPressed = true;
				mControls.handlePointer(x,y,pointCount);
				mControls.setXPointer(x[0]);
				mControls.setYPointer(y[0]);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				//Log.d("ShaderActivity", "secondPonter ["+e.getX()+"]["+e.getY()+"]" );
				break;
			case MotionEvent.ACTION_UP:		// no mode
				//Log.d("ShaderActivity", "ACTION_UP" );
				mControls.isPointerPressed = false;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				//Log.d("ShaderActivity", "ACTION_POINTER_UP" );
				break;
			case MotionEvent.ACTION_MOVE:
				//Log.d("ShaderActivity", "MotionEvent.ACTION_MOVE ["+x+"]["+y+"]" );
				mControls.isPointerMove = true;
				mControls.handlePointer(x,y,pointCount);
				mControls.setXPointer(x[0]);
				mControls.setYPointer(y[0]);
				break;
			default: 
				mControls.isPointerMove = false;
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
