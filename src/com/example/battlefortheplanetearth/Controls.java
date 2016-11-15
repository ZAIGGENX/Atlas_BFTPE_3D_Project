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

public class Controls{

	public static float xPointer;
	public static float yPointer;
	public static boolean isPointerPressed;
	public static boolean isPointerMove;
	private String LOG_TAG = "BFTPE2 Controls";
	private double circleAngle, x1Temp, x2Temp, y1Temp, y2Temp, xPointerFixed1, xPointerFixed2, lastAngle, initialDistance, currentDistance;
	boolean doThisOnce = true;

	public Controls(){
	}

	public void handlePointer(float [] x, float [] y, int pointersCount){
		//Log.d(LOG_TAG, "handlePointer" );
		if(pointersCount == 2){
			float x1 = x[0];
			float x2 = x[1];
			float y1 = y[0];
			float y2 = y[1];
		}

		//Calculate Drag X In touch behavior  --->  <---

		calculate(x, y, pointersCount);

		//Calculate Drag X Out touch behavior  <--  --->

		//Calculate Drag Y Up touch behavior  /\  /\

		//Calculate Drag Y Up Down Inversal touch behavior  \/  /\

	}

	public void calculate(float [] x, float [] y, int pointersCount){

		float x1 = x[0];
		float x2 = x[1];
		float y1 = y[0];
		float y2 = y[1];

		if(pointersCount == 2){
			x1 = x[0];
			x2 = x[1];
			y1 = y[0];
			y2 = y[1];
			//AtlasMovement.handleSpeed();
		}
        
	        if(isPointerPressed && pointersCount == 2){
	            if(doThisOnce){
	                x1Temp = x1;
	                x2Temp = x2;
	                //Log.e(LOG_TAG, "POINTER X1: "+  ( x1 ) );
		            //Log.e(LOG_TAG, "POINTER X2: "+  ( x2 ) );

	                doThisOnce = false;
	                initialDistance = PythagoreanFormula(x1,y1,x2,y2);
	            }

	            if(isPointerMove){
	            	//Log.e(LOG_TAG, "POINTER X1: "+  ( x1 ) );
		            //Log.e(LOG_TAG, "POINTER X2: "+  ( x2 ) );

					//if(pointersCount == 2)
					currentDistance = PythagoreanFormula(x1,y1,x2,y2);
					
					if(currentDistance < initialDistance){
						Log.e(LOG_TAG, "YOU ARE DRAGIN IN' BRO! " );
						AtlasMovement.handleSpeed((float)currentDistance);
					}
						
					
					if(currentDistance > initialDistance){
						Log.e(LOG_TAG, "YOU ARE DRAGIN OUT' BRO! " );
						AtlasMovement.handleSpeed((float)currentDistance);
					}

		            xPointerFixed1 = x1 - x1Temp;
		            xPointerFixed2 = x2 - x2Temp;
	            }

	        } else {
	            doThisOnce = true;
	            AtlasMovement.resetSpeedTrigger = true;
	        }
        
	}

	// d = squareRoot( (x2-x1)^2 + (y2-y1)^2  ) 
	//Get Distance between 2 points, Thanks Pythagoras
	public double PythagoreanFormula(  float x1, float y1 , float x2, float y2 ){
		return Math.sqrt(  Math.pow( x2-x1 , 2 ) + Math.pow( y2-y1, 2 ) );
	}

	public void setXPointer(float x){
		xPointer = x;
	}

	public void setYPointer(float y){
		yPointer = y;
	}

	public float getXPointer(){
		return xPointer;
	}

	public float getYPointer(){
		return yPointer;
	}

}