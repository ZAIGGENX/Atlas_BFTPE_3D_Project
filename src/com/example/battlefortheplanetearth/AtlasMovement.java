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

public class AtlasMovement extends Thread {

	public static float xPointer;
	public static float yPointer;
	public static boolean isPointerPressed;
	public static boolean isPointerMove;
	private Thread t;
	private String LOG_TAG = "BFTPE2 AtlasMovement";
	public static float atlasSpeed;
	
	public static float atlasXPosition;
	public static float atlasYPosition;
	public static float atlasZPosition;
	
	public static float atlasXRotation;
	public static float atlasYRotation;
	public static float atlasZRotation;	

	public static float DEFAULT_SPEED = 0.040f;
	public static boolean resetSpeedTrigger = false;

	public void resetSpeed(){
		if(resetSpeedTrigger){
			if (atlasSpeed > DEFAULT_SPEED){
				atlasSpeed -= 0.010f;
			} else {
				atlasSpeed = DEFAULT_SPEED;
				resetSpeedTrigger = false;
			}
		}
	}

	public AtlasMovement(){
		atlasSpeed = DEFAULT_SPEED;
		atlasXPosition = 0.0f;
		atlasYPosition = 1.0f;
		atlasZPosition = 0.0f;
		
		atlasXRotation = 0.0f;
		atlasYRotation = 0.0f;
		atlasZRotation = 0.0f;	

	}

	public static void handleSpeed(float currentDistance){
		setSpeed( DEFAULT_SPEED + ( 60f / currentDistance ) );
		atlasYRotation -= 0.5f;
	}

	public static float getSpeed(){
		return atlasSpeed;
	}

	public static float getX(){
		return atlasXPosition;
	}
	public static float getY(){
		return atlasYPosition;
	}
	public static float getZ(){
		return atlasZPosition;
	}

	public static float rotationAngleX(){
		return atlasXRotation;
	}
	public static float rotationAngleY(){
		return atlasYRotation;
	}
	public static float rotationAngleZ(){
		return atlasZRotation;
	}
	public static void setSpeed(float speed){
		atlasSpeed = speed;
	}

   public void run() {
		//while(true){
			try {
				//synchronized(this) {
				//Log.d(LOG_TAG, "Thread Movement Running" );
	            //this.sleep(5);
	            //atlasZPosition += atlasSpeed;
	            //resetSpeed();

	        	//}
		    } catch (Exception e) {
		    	Log.d(LOG_TAG, "Thread  interrupted." );
		    }
   		//}

   }

   public void updateMovement(){
		atlasZPosition += atlasSpeed;
		resetSpeed();
   }

   public void start ()
   {
      //Log.d(LOG_TAG, "Thread Movement START" );
      if (t == null)
      {
         t = new Thread (this);
         t.start ();
      }
   }

}