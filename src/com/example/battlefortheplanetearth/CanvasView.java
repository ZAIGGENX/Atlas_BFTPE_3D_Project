package com.example.battlefortheplanetearth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class CanvasView extends View {

	public int width;
	public int height;
	private Bitmap mBitmap, image01, resizedBitmap;
	private Canvas mCanvas;
	private Path mPath;
	Context context;
	private Paint mPaint;
	private float mX, mY;
	private float curretXpos, curretYpos;
	private int imageWidth, imageHeight;
	private int drawOffsetX = 50;
	private int drawOffsetY = -50;
	private static final float TOLERANCE = 10;

	public CanvasView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;

		// we set a new Path
		mPath = new Path();

		// and we set a new Paint with the desired attributes
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeWidth(4f);
		image01 = BitmapFactory.decodeResource(getResources(), R.drawable.pencil);
    	// resize the bit map
    	Matrix matrix = new Matrix();
    	matrix.postScale(0.3f, 0.3f);
    	// recreate the new Bitmap
    	resizedBitmap = Bitmap.createBitmap(image01, 0, 0, image01.getWidth(), image01.getHeight(), matrix, false);

    	imageWidth = resizedBitmap.getWidth();
		imageHeight= resizedBitmap.getHeight();
	}

	// override onSizeChanged
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// your Canvas will draw onto the defined Bitmap
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	// override onDraw
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw the mPath with the mPaint on the canvas when onDraw
		//canvas.drawBitmap(image01, curretXpos - (imageWidth/2), curretYpos - (imageHeight/2), null);
		canvas.drawPath(mPath, mPaint);
		canvas.drawBitmap(resizedBitmap, curretXpos - drawOffsetX/*- (imageWidth)*/, curretYpos - (imageHeight) - drawOffsetY, null);
		
	}

	// when ACTION_DOWN start touch according to the x,y values
	private void startTouch(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	// when ACTION_MOVE move touch according to the x,y values
	private void moveTouch(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOLERANCE || dy >= TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	public void clearCanvas() {
		mPath.reset();
		invalidate();
	}
	
	public void switchColor() {
		mPaint.setColor(Color.BLACK);
		//invalidate();
	}

	// when ACTION_UP stop touch
	private void upTouch() {
		mPath.lineTo(mX, mY);
	}

	//override the onTouchEvent
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX() - drawOffsetX;
		float y = event.getY() - drawOffsetY;

		curretXpos = event.getX();
		curretYpos = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTouch(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			moveTouch(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			upTouch();
			invalidate();
			break;
		}
		return true;
	}
}
