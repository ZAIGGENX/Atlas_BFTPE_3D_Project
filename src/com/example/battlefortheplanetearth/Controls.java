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