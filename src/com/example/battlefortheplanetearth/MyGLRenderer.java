
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

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.util.ArrayList;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    //private Triangle mTriangle;
    private Square   mSquare;
    private Cube   mCube;
    private static final String TAG = "MyGLRenderer";

    public ArrayList<Object3D> object3D;
    public Mesh mMesh;
    public urlList mUrlList;

    public static int vID, fID;
    boolean mHasDepthTextureExtension = false;

    Context mContext;
    Controls mControls;
    private AtlasMovement atlasMov;
    public boolean AtlasCameraActive;

    // texture file ids
    int [] textures = {
        R.raw.flat_color,
        R.raw.texture1,
        R.raw.moon_texture,
        R.raw.metal,
        R.raw.white_texture
    };

    private enum OBJECTS_3D {
        //OBJECT    ( index, Resource ID, Resource Texture ID )
        ATLAS       (R.raw.atlas, R.raw.flat_color),
        CUBE        (R.raw.cube, R.raw.white_texture),
        LAND        (R.raw.land, R.raw.moon_texture),
        FLOOR          (R.raw.floor, R.raw.metal),
        SPHERE      (R.raw.sphere, R.raw.moon_texture);
        //MATRIX      (R.raw.matrix1, R.raw.texture1);
        //BHOUSE      (R.raw.bhouse, R.raw.texture1);

        private final int resource_id;
        private final int texture_id;

        //enum constructor
        OBJECTS_3D(int resource_id, int texture_id) {
            this.resource_id = resource_id;
            this.texture_id = texture_id;
        }

        private int resource_id() { return resource_id; }
        private int texture_id() { return texture_id; }

    }
    
    public MyGLRenderer(Context context){

        mContext = context;
        mControls = new Controls();

        object3D = new ArrayList<Object3D>(100);

        for (OBJECTS_3D o : OBJECTS_3D.values())
            object3D.add ( new Object3D( o.resource_id() , true, o.texture_id(), context) );

        //mMesh = new Mesh(context);
        mUrlList = new urlList(context);
        mUrlList.loadFile();

        getShadersID();

        atlasMov = new AtlasMovement();
        atlasMov.start();

        AtlasCameraActive = true;

        // Test OES_depth_texture extension
        //String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        
        //if (extensions.contains("OES_depth_texture"))
          //  mHasDepthTextureExtension = true;

    }

    public static void getShadersID(){
        //vID = R.raw.vertexshader;
        //fID = R.raw.fragmentshader;

        vID = R.raw.vertexshader_pfragment;
        fID = R.raw.fragmentshader_pfragment;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(255.0f, 255.0f, 255.0f, 1.0f);
        for(int i = 0; i < object3D.size() ; i++){
            object3D.get(i).loadFile();
        }

        Log.d(TAG, "OBJECTS3D --------- No. of 3D OBJECTS:"+ object3D.size() );
        
        setupTextures();
        //mTriangle = new Triangle();
        mSquare   = new Square();
        mCube   = new Cube();
    }

     // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private final float[] landPosition = new float[16];

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 500);
    }

    float zTemp;

    float camera_EyeX;
    float camera_EyeY;
    float camera_EyeZ;

    float camera_CenterX;
    float camera_CenterY;
    float camera_CenterZ;

    double circleAngle, xTemp, xPointerFixed, lastAngle;
    boolean doThisOnce = true;
    boolean useDebugRotateCamera = false;

    float multiColorVar;
    boolean switchIncrement = true;

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        float[] scratch = new float[16];

        atlasMov.updateMovement();

        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 900000L;
        float angle = 0.001f * ((int) time);

        zTemp += 0.040f;
        if(zTemp > 200)
            zTemp = 0;

        //Playing with shader color 
        if(multiColorVar <= 1.0f && switchIncrement){
            multiColorVar += 0.01f;
            if(multiColorVar >= 1.0f)
                switchIncrement = false;
        }

        if(multiColorVar >= 0.0f && switchIncrement == false){
            multiColorVar -= 0.01f;
            if(multiColorVar <= 0.0f)
                switchIncrement = true;
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        //Focus the camera to an object on th scene.
        float [] _3dObject_position = object3D.get( OBJECTS_3D.ATLAS.ordinal() ).getPosition();

        //x  =  h + r cos(t)
        //y  =  k + r sin(t)

        //Log.e(TAG, "xyTouchPointers X: "+ mControls.getXPointer());

        //Rotate the camera over the object.
        if(useDebugRotateCamera){
            if(Controls.isPointerPressed){

                if(doThisOnce){
                    xTemp = mControls.getXPointer();
                    doThisOnce = false;
                    lastAngle = circleAngle; //TO store the last angle and not reset the camera
                }

                if(Controls.isPointerMove){

                //Log.e(TAG, "DRAG CAMERA: "+  ( mControls.getXPointer() - xTemp ) );

                xPointerFixed = mControls.getXPointer() - xTemp;

                //circleAngle += 0.02; //Camera Rotation Speed
                circleAngle = xPointerFixed * 0.02 + lastAngle; //Camera Rotation Speed
                if(circleAngle > 360)
                    circleAngle = 0;
                }

            } else {
                doThisOnce = true;
                xTemp = 0.0f;
            }
        }

        //Using the Circle Ecuation
        //to rotate camera around a object. In this case the Ship.
        float cameraOffset = 7.0f; //The Radius
        float xCam = _3dObject_position[0] + cameraOffset * (float)Math.cos(circleAngle);
        float zCam = _3dObject_position[2] + cameraOffset * (float)Math.sin(circleAngle);

        // Set the camera position (View matrix)
        //float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ
        if(useDebugRotateCamera){
            camera_EyeX = xCam;
            camera_EyeY = 3f;
            camera_EyeZ = zCam;
        } else {
            camera_EyeX = atlasMov.getX();
            camera_EyeY = 3f;
            camera_EyeZ = atlasMov.getZ() - cameraOffset ;
        }

        if(AtlasCameraActive){
            camera_CenterX = atlasMov.getX();
            camera_CenterY = atlasMov.getY();
            camera_CenterZ = atlasMov.getZ();
        } else {
            camera_CenterX = _3dObject_position[0];
            camera_CenterY = _3dObject_position[1];
            camera_CenterZ = _3dObject_position[2];
        }

        Matrix.setLookAtM(mViewMatrix, 0, camera_EyeX, camera_EyeY, camera_EyeZ, camera_CenterX, camera_CenterY, camera_CenterZ, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        mSquare.draw(mMVPMatrix);

        object3D.get( OBJECTS_3D.ATLAS.ordinal() ).setRotationAngleX(atlasMov.rotationAngleX());
        object3D.get( OBJECTS_3D.ATLAS.ordinal() ).setRotationAngleY(atlasMov.rotationAngleY());
        object3D.get( OBJECTS_3D.ATLAS.ordinal() ).setRotationAngleZ(atlasMov.rotationAngleZ());

        object3D.get( OBJECTS_3D.ATLAS.ordinal() ).setPosition(atlasMov.getX(), atlasMov.getY(), atlasMov.getZ());
        object3D.get( OBJECTS_3D.ATLAS.ordinal() ).setLightPos(-camera_EyeX,camera_EyeY,_3dObject_position[2] - camera_EyeZ);
        object3D.get( OBJECTS_3D.ATLAS.ordinal() ).setLightColor(0.2f, 0.2f, 0.2f, 1.0f);
        //Log.d("LIGHT", "LIGHT POS ["+camera_EyeX+"]["+camera_EyeY+"]["+(camera_EyeZ-_3dObject_position[2])+"]");
        

        object3D.get( OBJECTS_3D.CUBE.ordinal() ).setLightPos(1.0f,10.0f,1.0f);
        object3D.get( OBJECTS_3D.CUBE.ordinal() ).setPosition(0.0f, 1.0f, 10.0f);
        object3D.get( OBJECTS_3D.CUBE.ordinal() ).setScale(2.0f, 2.0f, 2.0f);
        object3D.get( OBJECTS_3D.CUBE.ordinal() ).setLightColor(0.5f, 0.5f, 0.5f, 1.0f);

        object3D.get( OBJECTS_3D.FLOOR.ordinal() ).setLightPos(1.0f,10.0f,1.0f);
        object3D.get( OBJECTS_3D.FLOOR.ordinal() ).setPosition(0.0f, 0.0f, 0.0f);
        object3D.get( OBJECTS_3D.FLOOR.ordinal() ).setScale(1.0f, 1.0f, 1.0f);
        object3D.get( OBJECTS_3D.FLOOR.ordinal() ).setLightColor(0.3f, 0.3f, 0.1f, 1.0f);
        
        object3D.get( OBJECTS_3D.LAND.ordinal() ).setPosition(0.0f, -1.0f, 0.0f);
        object3D.get( OBJECTS_3D.LAND.ordinal() ).setScale(1000.0f, 10.0f, 1000.0f);
        object3D.get( OBJECTS_3D.LAND.ordinal() ).setLightColor(0.2f, 0.4f, 0.6f, 1.0f);


        object3D.get( OBJECTS_3D.SPHERE.ordinal() ).setRotationAngleY(angle);
        object3D.get( OBJECTS_3D.SPHERE.ordinal() ).setPosition(0.0f, 0.0f, 300.0f);
        object3D.get( OBJECTS_3D.SPHERE.ordinal() ).setScale(80.0f, 80.0f, 80.0f);

        //object3D.get(OBJECTS_3D.MATRIX.ordinal()).setRotationAngleY(angle);

        for( Object3D o : object3D  )
            o.draw(mMVPMatrix);

        
    }

    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);


        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + type + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }

    public static int getVertexShader(Context context) {
        StringBuffer vs = new StringBuffer();

        // read the files
        try {

            // Read VS first
            InputStream inputStream = context.getResources().openRawResource(vID);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String read = in.readLine();
            while (read != null) {
                vs.append(read + "\n");
                read = in.readLine();
            }

            vs.deleteCharAt(vs.length() - 1);

        } catch (Exception e) {
            Log.d("ERROR-readingShader", "Could not read shader: " + e.getLocalizedMessage());
        }

        //Log.d("VS", "VS " + vs.toString());
        int ret = loadShader(GLES20.GL_VERTEX_SHADER, vs.toString());

        return ret;
    }

    public static int getFragmentShader(Context context) {
        StringBuffer fs = new StringBuffer();

        // read the files
        try {
            // Read FS
            InputStream inputStream = context.getResources().openRawResource(fID);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String read = in.readLine();
            while (read != null) {
                fs.append(read + "\n");
                read = in.readLine();
            }

            fs.deleteCharAt(fs.length() - 1);
        } catch (Exception e) {
            Log.d("ERROR-readingShader", "Could not read shader: " + e.getLocalizedMessage());
        }

        //Log.d("FS", "FS " + fs.toString());

        int ret = loadShader(GLES20.GL_FRAGMENT_SHADER, fs.toString());

        return ret;
    }

    private void setupTextures() {

            int [] tex_temp =  new int[textures.length];
            for(int i = 0; i < textures.length; i++)
                tex_temp[i] = textures[i];

            Log.d("TEXFILES LENGTH: ", textures.length + "");
            GLES20.glGenTextures(textures.length, textures, 0);

            for(int i = 0; i < textures.length; i++) {

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex_temp[i]);

                // parameters
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                                        GLES20.GL_NEAREST);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                                        GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                                        GLES20.GL_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                                        GLES20.GL_REPEAT);

                Log.d("openRawResource: ", tex_temp[i] + "");
                InputStream is = mContext.getResources().openRawResource(tex_temp[i]);
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(is);
                } finally {
                    try {
                        is.close();
                    } catch(Exception e) {
                        // Ignore.
                    }
                }

                // create it 
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                bitmap.recycle();

                Log.d("ATTACHING TEXTURES: ", "Attached " + i);
            }

    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}
