package com.example.battlefortheplanetearth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;

import android.opengl.GLES20;
import android.opengl.Matrix;

import android.os.SystemClock;

public class Mesh {

    private final String vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
        "attribute vec3 aNormal;" +
        "attribute vec2 textureCoord;"+

        "void main() {" +
        	"gl_Position = uMVPMatrix * vPosition;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +

        "void main() {" +
        	"gl_FragColor = vColor;" +
        "}";

    private int mProgram;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private int mLightPosHandle;
    private int mLightColorHandle;
    private int mTextureCoordHandle;

    private int mMatAmbientHandle;
    private int mMatDiffuseHandle;
    private int mMatSpecularHandle;
    private int mMatShininessHandle;

	private int mEyePosHandle;

	// Modelview/Projection matrices
	private float[] mMVPMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	private float[] mScaleMatrix = new float[16];   // scaling
	private float[] mRotXMatrix = new float[16];	// rotation x
	private float[] mRotYMatrix = new float[16];	// rotation x
	private float[] mRotZMatrix = new float[16];	// rotation z
	private float[] mMMatrix = new float[16];		// rotation
	private float[] mVMatrix = new float[16]; 		// modelview
	private float[] normalMatrix = new float[16]; 	// modelview normal
	private float[] mPositionMatrix = new float[16]; 	// position
	

	// scaling
	float scaleX = 1.0f;
	float scaleY = 1.0f;
	float scaleZ = 1.0f;

	// rotation 
	public float rAngleX;
	public float rAngleY;
	public float rAngleZ;

        // R, G, B, A
        final float[] color =
        {               
                // Front face (red)
                1.0f, 1.0f, 1.0f, 1.0f,             
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };

    	// Constants
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int SHORT_SIZE_BYTES = 2;
	// the number of elements for each vertex
	// [coordx, coordy, coordz, normalx, normaly, normalz....]
	private final int VERTEX_ARRAY_SIZE = 8;
	
	// if tex coords exist
	private final int VERTEX_TC_ARRAY_SIZE = 8;

	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_NOR_OFFSET = 3;
	private static final int TRIANGLE_VERTICES_DATA_TEX_OFFSET = 6;

	static final int COORDS_PER_VERTEX = 3;

	public String LOG_TAG = "BFTPE";

	// Vertices
	private float _vertices[];

	// Normals
	private float _normals[];
	
	// Texture coordinates
	private float _texCoords[];
	
	// Indices
	private short _indices[];	

	// Store the context
	Context activity;

	// Buffers - index, vertex, normals and texcoords
	private FloatBuffer _vb;
	private FloatBuffer _nb;
	private ShortBuffer _ib;
	private FloatBuffer _tcb;

	// light parameters
	private float[] lightPos;
	private float[] lightColor;
	private float[] lightAmbient;
	private float[] lightDiffuse;

	// material properties
	private float[] matAmbient;
	private float[] matDiffuse;
	private float[] matSpecular;
	private float matShininess;

	// eye pos
	private float[] eyePos = {-8.0f, 10.0f, 0.0f};

	// Object XYZ Position
	public float [] getPos;

	public int [] normalMapTextures;

	private int textureID;

	public Mesh(int textureID, Context activity) {

		this.activity = activity;
		this.textureID = textureID;

	}

	public void meshInitialize() {

 		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
  		//GLES20.glEnable(GLES20.GL_BLEND);

		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		GLES20.glDepthMask( true );
		//GLES20.glEnable(GLES20.GL_LIGHTING);

		// cull backface
		GLES20.glEnable( GLES20.GL_CULL_FACE );
		GLES20.glCullFace(GLES20.GL_BACK); 

		_vb = ByteBuffer.allocateDirect(_vertices.length
				* FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		_vb.put(_vertices);
		_vb.position(0);

		// index buffer
		_ib = ByteBuffer.allocateDirect(_indices.length
				* SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
		_ib.put(_indices);
		_ib.position(0);

		// light variables
		float[] lightP = {6.0f, 10.0f, -13.0f, 1.0f};
		this.lightPos = lightP;

		float[] lightC = {0.2f, 0.2f, 0.7f, 1.0f};
		this.lightColor = lightC;

		// material properties
		float[] mA = {0.2f, 0.02f, 0.3f, 1.0f};
		matAmbient = mA;

		float[] mD = {0.1f, 0.25f, 0.6f, 1.0f};
		matDiffuse = mD;

		float[] mS =  {0.1f, 0.1f, 0.1f, 1.0f};
		matSpecular = mS;

		matShininess = 3.5f;

		getPos = new float[3];
		getPos[0] = 0.0f;
		getPos[1] = 0.0f;
		getPos[2] = 0.0f;

        // prepare shaders and OpenGL program
		int vertexShader = MyGLRenderer.getVertexShader(activity);
        int fragmentShader = MyGLRenderer.getFragmentShader(activity);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glBindAttribLocation(mProgram, 1, "aNormal");
        GLES20.glBindAttribLocation(mProgram, 2, "textureCoord");
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

	}

	// angle rotation for light
	float angle = 0.0f;
	boolean lightRotate = false;

	public void draw(float[] mvpMatrix) {

		//GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		//GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		//mMVPMatrix = mvpMatrix;



        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);


		// rotate the light?
		if (lightRotate) 
		{
			angle += 0.000005f;
			if (angle >= 6.2)
				angle = 0.0f;

			// rotate light about y-axis
			float newPosX = (float)(Math.cos(angle) * lightPos[0] - Math.sin(angle) * lightPos[2]);
			float newPosZ = (float)(Math.sin(angle) * lightPos[0] + Math.cos(angle) * lightPos[2]);
			lightPos[0] = newPosX; lightPos[2] = newPosZ;
		}

		// scaling
		Matrix.setIdentityM(mScaleMatrix, 0);
		Matrix.scaleM(mScaleMatrix, 0, scaleX, scaleY, scaleZ);

		// Rotation along x
		Matrix.setRotateM(mRotXMatrix, 0, getRotationAngleX(), 1.0f, 0.0f, 0.0f);
		Matrix.setRotateM(mRotYMatrix, 0, getRotationAngleY(), 0.0f, 1.0f, 0.0f);
		Matrix.setRotateM(mRotZMatrix, 0, getRotationAngleZ(), 0.0f, 0.0f, 1.0f);

		Matrix.setIdentityM(mPositionMatrix, 0);
		Matrix.translateM(mPositionMatrix, 0, getPos[0], getPos[1], getPos[2]);

		// Set the ModelViewProjectionMatrix
		float tempMatrix[] = new float[16]; 
		//Matrix.multiplyMM(mMMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, mRotZMatrix, 0, tempMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, mScaleMatrix, 0, tempMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mPositionMatrix, 0, tempMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mvpMatrix, 0, mMVPMatrix, 0);

        // get handle to vertex shader's vPosition member
		//--------------------------------------
        _vb.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        ////Log.d(LOG_TAG, "ATTRIB LOCATION OF vPosition = "+ mPositionHandle);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		//--------------------------------------

   		// the normal info
		//--------------------------------------
        _vb.position(TRIANGLE_VERTICES_DATA_NOR_OFFSET);
        mNormalHandle = 1; //HARDCODING!!
        //mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        MyGLRenderer.checkGlError("mNormalHandle 1");

        // Enable a handle to the normal
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        MyGLRenderer.checkGlError("mNormalHandle 2");

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mNormalHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
        MyGLRenderer.checkGlError("mNormalHandle 3");
		//---------------------------------------

		// Create the normal modelview matrix
		// Invert + transpose of mvpmatrix
		Matrix.invertM(normalMatrix, 0, mMVPMatrix, 0);
		Matrix.transposeM(normalMatrix, 0, normalMatrix, 0);

				// send to the shader
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "normalMatrix"), 1, false, mMVPMatrix, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// lighting variables
		// send to shaders
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "lightPos");
		GLES20.glUniform4fv(mLightPosHandle, 1, lightPos, 0);

		mLightColorHandle = GLES20.glGetUniformLocation(mProgram, "lightColor");
		GLES20.glUniform4fv(mLightColorHandle, 1, lightColor, 0);

		// material 
		mMatAmbientHandle = GLES20.glGetUniformLocation(mProgram, "matAmbient");
		GLES20.glUniform4fv(mMatAmbientHandle, 1, matAmbient, 0);
		mMatDiffuseHandle = GLES20.glGetUniformLocation(mProgram, "matDiffuse");
		GLES20.glUniform4fv(mMatDiffuseHandle, 1, matDiffuse, 0);
		mMatSpecularHandle = GLES20.glGetUniformLocation(mProgram, "matSpecular");
		GLES20.glUniform4fv(mMatSpecularHandle, 1, matSpecular, 0);
		mMatShininessHandle = GLES20.glGetUniformLocation(mProgram, "matShininess");
		GLES20.glUniform1f(mMatShininessHandle, matShininess);

		mEyePosHandle = GLES20.glGetUniformLocation(mProgram, "eyePos");
		GLES20.glUniform3fv(mEyePosHandle, 1, eyePos, 0);

		// texture coordinates
		_vb.position(TRIANGLE_VERTICES_DATA_TEX_OFFSET);
		//mTextureCoordHandle = 1; //HARDCODING!!
		mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "textureCoord");
		////Log.d(LOG_TAG, "ATTRIB LOCATION OF mTextureCoordHandle = "+ mTextureCoordHandle);
		GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false,
				TRIANGLE_VERTICES_DATA_STRIDE_BYTES, _vb);
		GLES20.glEnableVertexAttribArray(mTextureCoordHandle);


		//if (ob.hasTexture()) {
			// number of textures
			//for(int i = 0; i < _texIDs.length; i++) {
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				////Log.d("TEXTURE BIND: ", i + " " + texIDs[i]);
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
				GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram,"texture1"), 0);
			//}
		//}

        // Draw Elements
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, _indices.length,
                GLES20.GL_UNSIGNED_SHORT, _ib);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);


	}

	ArrayList<Integer> myIndices = new ArrayList<Integer>(100); // normals

	public int loadFile(int resource_3D) {
		//Log.d(LOG_TAG, "Starting loadFile");
		try {
			// Read the file from the resource
			//Log.d(LOG_TAG, "Trying to buffer read");
			//int _object = R.raw.cube;
			//int _object = R.raw.cube2;
			//int _object = R.raw.cube3;
			//int _object = R.raw.cube4;
			//int _object = R.raw.atlas;
			//int _object = R.raw.sphere;
			//int _object = R.raw.square;
			//int _object = R.raw.house;
			//int _object = R.raw.land;
			//int _object = R.raw.cannon;
			int _object = resource_3D;

			InputStream inputStream = activity.getResources().openRawResource(_object);
			//Log.d(LOG_TAG, "Trying to buffer read2");
			// setup Bufferedreader
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			// Try to parse the file
			//Log.d(LOG_TAG, "Trying to buffer read3");
			
			String str = "";
			String type = "";
			StringTokenizer t = null;
			StringTokenizer t_face = null;

			ArrayList<Float> vs = new ArrayList<Float>(100); // vertices
			ArrayList<Float> tc = new ArrayList<Float>(100); // texture coords
			ArrayList<Float> ns = new ArrayList<Float>(100); // normals
			

			int numVertices = 0;
			int numTexCoords = 0;

			String fFace, sFace, tFace, nface_elements;
			ArrayList<Float> mainBuffer = new ArrayList<Float>(numVertices * 6);
			ArrayList<Short> indicesB = new ArrayList<Short>(numVertices * 3);
			StringTokenizer lt, ft; // the face tokenizer
			int numFaces = 0;
			short index = 0;
			String type_face =  "";


			// create the vertex buffer
			float[] _v = null;
			// create the normal buffer
			float[] _n = null;
			// texcoord
			_texCoords = null;

			boolean doThisOnce = true;
			int count = 0;

			//Read Whole File
			while((str = in.readLine()) != null) {

				//Print all the file content
				////Log.d(LOG_TAG, str);

				if (!str.equals("")) {
					t = new StringTokenizer(str);
					type = t.nextToken();
					////Log.d(LOG_TAG, "TOKEN: "+type);

					if(type.equals("v")) {
						////Log.d(LOG_TAG, "X: " + t.nextToken() + " Y: " + t.nextToken() + " Z: " + t.nextToken());

						float X = Float.parseFloat(t.nextToken());
						float Y = Float.parseFloat(t.nextToken());
						float Z = Float.parseFloat(t.nextToken());
						vs.add(X); 	// x
						vs.add(Y);	// y
						vs.add(Z);	// z

						ns.add(0.0f); 	// x
						ns.add(0.0f);	// y
						ns.add(0.0f);	// z

						numVertices++;
					}
					
					if(type.equals("vt")) {
						////Log.d(LOG_TAG, "X: " + t.nextToken() + " Y: " + t.nextToken() + " Z: " + t.nextToken());

						tc.add(Float.parseFloat(t.nextToken())); 	// u
						tc.add(Float.parseFloat(t.nextToken()));	// v

						numTexCoords++;
					}

					_surroundingFaces = new int[numVertices]; // # of surrounding faces for each vertex

					// now read all the faces
					if (type.equals("f")) {


						if(doThisOnce){

							// create the vertex buffer
							_v = new float[numVertices * 3];
							// create the normal buffer
							_n = new float[numVertices * 3];
							// texcoord
							_texCoords = new float[numTexCoords * 2];

							//Log.d(LOG_TAG, "DO this once");
							// copy over data - INEFFICIENT [SHOULD BE A BETTER WAY]
							for(int i = 0; i < numVertices; i++) {
								_v[i * 3] 	 = vs.get(i * 3); 
								_v[i * 3 + 1] = vs.get(i * 3 + 1);
								_v[i * 3 + 2] = vs.get(i * 3 + 2);

								////Log.d(LOG_TAG, "Positions [i * 3] = "+(i * 3)+" [i * 3 + 1] = "+(i * 3 + 1)+ " [i * 3 + 2] = "+(i * 3 + 2) );

								//float [] normal_temp = new float[3];
								//normal_temp = calculateFaceNormal(1,2,3);

								_n[i * 3 ] 	= -ns.get(i * 3);
								_n[i * 3 + 1] = -ns.get(i * 3 + 1);
								_n[i * 3 + 2] = -ns.get(i * 3 + 2);

								// transfer tex coordinates
							}

							for(int i = 0; i < numTexCoords; i++) {
								_texCoords[i * 2] 	  = tc.get(i * 2);
								_texCoords[i * 2 + 1] = tc.get(i * 2 + 1);

								//Log.d(LOG_TAG, "TEXT COORD [1] = "+_texCoords[i * 2]+" / TEXT COORD [1] = "+_texCoords[i * 2 + 1]);
							}


							//Log.d(LOG_TAG, "NUM_VERTICES :"+numVertices);
							//Log.d(LOG_TAG, "NUM_COORDS :"+numTexCoords);
							doThisOnce = false;

						} //do this once


						int num_elements = 0;
						try {
							t_face = new StringTokenizer(str);
							//Log.d(LOG_TAG, "String to evaluate :"+str);
							type_face = t_face.nextToken();
							while( ( type_face = t_face.nextToken() ) != null){
								//Log.d(LOG_TAG, "type_face :"+type_face);
								num_elements++;
							}
						} catch (Exception e) {
							//Log.d(LOG_TAG, "FOUND ERROR: " + e.toString());
						}
						//Log.d(LOG_TAG, "num_elements :"+num_elements);

						int [] vectorNodePos = new int[3];
						// Each line: f v1/vt1/vn1 v2/vt2/vn2 
						// Figure out all the vertices
						for (int j = 0; j < num_elements; j++) {
							fFace = t.nextToken();
							////Log.d(LOG_TAG, "fFace :"+fFace);
							// another tokenizer - based on /
							ft = new StringTokenizer(fFace, "/");
							int vert = Integer.parseInt(ft.nextToken()) - 1;
							int texc = Integer.parseInt(ft.nextToken()) - 1;
							//Log.d(LOG_TAG, "VERT :"+vert +"/TEXC :"+texc);
							//int vertN = Integer.parseInt(ft.nextToken()) - 1;
							
							// Add to the index buffer
							indicesB.add(index++);
							myIndices.add(vert);
							
							// Add all the vertex info
							mainBuffer.add(_v[vert * 3]); 	 // x
							mainBuffer.add(_v[vert * 3 + 1]);// y
							mainBuffer.add(_v[vert * 3 + 2]);// z
						
							// add the normal info
							//mainBuffer.add(_n[vertN * 3]); 	  // x
							//mainBuffer.add(_n[vertN * 3 + 1]); // y
							//mainBuffer.add(_n[vertN * 3 + 2]); // z

							mainBuffer.add( 0.0f ); // x
							mainBuffer.add( 0.0f ); // y
							mainBuffer.add( 0.0f ); // z
							
							// add the tex coord info
							mainBuffer.add(_texCoords[texc * 2]); 	  // u
							mainBuffer.add(_texCoords[texc * 2 + 1]); // v

							vectorNodePos[j] = vert;
							
						}

						//Assign Normals to the mainBuffer
						float [] vertexNormal = getVertexNormal( vectorNodePos , _v );
						for (int k = 0; k < num_elements; k++) {

							mainBuffer.set( count * VERTEX_ARRAY_SIZE + 3 , vertexNormal[0] ); // x
							mainBuffer.set( count * VERTEX_ARRAY_SIZE + 4 , vertexNormal[1] ); // y
							mainBuffer.set( count * VERTEX_ARRAY_SIZE + 5 , vertexNormal[2] ); // z

							//Log.d(LOG_TAG, "NORMAL X OF ["+(count)+"] = " + mainBuffer.get( numFaces * k * VERTEX_ARRAY_SIZE + 3) );
							//Log.d(LOG_TAG, "NORMAL Y OF ["+(count)+"] = " + mainBuffer.get( numFaces * k * VERTEX_ARRAY_SIZE + 4) );
							//Log.d(LOG_TAG, "NORMAL Z OF ["+(count)+"] = " + mainBuffer.get( numFaces * k * VERTEX_ARRAY_SIZE + 5) );

							count++;
						}
						
						// next face
						if (str != null) {
							t = new StringTokenizer(str);
							numFaces++;
							//type = t.nextToken();
						}
					}

				}

    			if (str.equals("")) {
        			//Log.d(LOG_TAG, "Found an empty line");
    			}
			} // End Read File


			// read faces and setup the index buffer
			// array size
			int arraySize = numFaces * 3;
			//Log.d(LOG_TAG, "NUM FACES :"+numFaces);
			
			mainBuffer.trimToSize();
			////Log.d("COMPLETED MAINBUFFER:", "" + mainBuffer.size());

			//VERY INNEFICIENT / Should be removed or optimized
			//normalizeNormals(mainBuffer);
			
			_vertices = new float[mainBuffer.size()];
			
			//Log.d(LOG_TAG, "MAINBUFFER --------- MB No. of BLOCKS:"+ (mainBuffer.size() / VERTEX_ARRAY_SIZE) );
			// copy over the mainbuffer to the vertex + normal array
			for(int i = 0; i < mainBuffer.size(); i++){
				_vertices[i] = mainBuffer.get(i);
				//Log.d(LOG_TAG, "["+i+"] = "+_vertices[i]);
			}
			
			////Log.d("COMPLETED TRANSFER:", "VERTICES: " + _vertices.length);

			//Log.d(LOG_TAG, "GET NORMALS ----- ");
			// setup the normals
			_normals = new float[numVertices * this.VERTEX_ARRAY_SIZE]; 
			_faceNormals = new float[arraySize]; // NEEDED?

			
			/*int [] tempVect = new int[3];
			// finally calculate the exact vertex normals
			for(int x = 0; x < mainBuffer.size() / VERTEX_ARRAY_SIZE ; x++) {
				
				tempVect[0] = _vertices[x * this.VERTEX_ARRAY_SIZE + 3];
				tempVect[1] = _vertices[x * this.VERTEX_ARRAY_SIZE + 4];
				tempVect[2] = _vertices[x * this.VERTEX_ARRAY_SIZE + 5];

				for(int y = 0; y < mainBuffer.size() / VERTEX_ARRAY_SIZE ; y++){

					if(tempVect[0] = _vertices[x * this.VERTEX_ARRAY_SIZE + 3])


				}

			}*/
			
			//Log.d(LOG_TAG, "MAINBUFFER WITH NORMALS---------");
			// copy over the mainbuffer to the vertex + normal array
			for(int i = 0; i < mainBuffer.size() / VERTEX_ARRAY_SIZE ; i++){
					
					//Log.d(LOG_TAG, "VERTEX X["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_POS_OFFSET]);
					//Log.d(LOG_TAG, "VERTEX Y["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_POS_OFFSET + 1]);
					//Log.d(LOG_TAG, "VERTEX Z["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_POS_OFFSET + 2]);

					//Log.d(LOG_TAG, "  NORMAL X["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_NOR_OFFSET]);
					//Log.d(LOG_TAG, "  NORMAL Y["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_NOR_OFFSET + 1]);
					//Log.d(LOG_TAG, "  NORMAL Z["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_NOR_OFFSET + 2]);

					//Log.d(LOG_TAG, "   TEXTURE["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_TEX_OFFSET]);
					//Log.d(LOG_TAG, "   TEXTURE["+i+"] = "+_vertices[i * VERTEX_ARRAY_SIZE + TRIANGLE_VERTICES_DATA_TEX_OFFSET + 1]);
			}

			// copy over indices buffer
			indicesB.trimToSize();
			_indices = new short[indicesB.size()];
			for(int i = 0; i < indicesB.size(); i++) {
				_indices[i] = indicesB.get(i);
				//Log.d(LOG_TAG, "indicesB.get("+i+") = " + indicesB.get(i) );
			}

			// close the reader
			in.close();

			meshInitialize();

			return 1;
		} catch (Exception e) {
			//Log.d(LOG_TAG, "FOUND ERROR: " + e.toString());
			return 0;
		}

	}

	// Normals
	private float[] _faceNormals;
	private int[]   _surroundingFaces; // # of surrounding faces for each vertex

	private float [] getVertexNormal( int [] vertexIndex , float [] _v ) {

		int  firstV = vertexIndex[0];
		int  secondV = vertexIndex[1];
		int  thirdV = vertexIndex[2];

		//Log.d(LOG_TAG, "firstV: " + firstV + " | secondV: " + secondV + " | thirdV: " + thirdV);

		// get coordinates of all the vertices
		float v1[] = {_v[firstV * 3], _v[firstV * 3 + 1], _v[firstV * 3 + 2]};
		float v2[] = {_v[secondV * 3], _v[secondV *3 + 1], _v[secondV * 3 + 2]};
		float v3[] = {_v[thirdV * 3], _v[thirdV * 3 + 1], _v[thirdV * 3 + 2]};

		//Log.d(LOG_TAG, "v1["+firstV*3+"] = "+v1[0] + " | v1["+(firstV*3+1)+"] = "+v1[1] + " | v1["+(firstV*3+2)+"] = "+v1[2]);
		//Log.d(LOG_TAG, "v2["+secondV*3+"] = "+v2[0] + " | v2["+(secondV*3+1)+"] = "+v2[1] + " | v1["+(secondV*3+2)+"] = "+v2[2]);
		//Log.d(LOG_TAG, "v3["+thirdV*3+"] = "+v3[0] + " | v3["+(thirdV*3+1)+"] = "+v3[1] + " | v1["+(thirdV*3+2)+"] = "+v3[2]);

		// calculate the cross product of v1-v2 and v2-v3
		float edge1[] = {v2[0]-v1[0], v2[1]-v1[1], v2[2]-v1[2]};
		float edge2[] = {v3[0]-v1[0], v3[1]-v1[1], v3[2]-v1[2]};

		float cp[] = crossProduct(edge1, edge2);

		// try normalizing here
		float sqrt = (float)Math.sqrt(cp[0] * cp[0] +
									  cp[1] * cp[1] +
									  cp[2] * cp[2]);

		//Log.d(LOG_TAG, "cp[0] = "+cp[0]+"]"+" / cp[1] = "+cp[1]+"]"+"cp[2] = "+cp[2]+"]" );
		//Log.d(LOG_TAG, "sqrt = "+sqrt);

		cp[0] /= sqrt;
		cp[1] /= sqrt;
		cp[2] /= sqrt;
		
		if (cp[0] == -0.0f)
			cp[0] = 0.0f;
		if (cp[1] == -0.0f)
			cp[1] = 0.0f;
		if (cp[2] == -0.0f)
			cp[2] = 0.0f;

		//Log.d(LOG_TAG, "NORMAL Result for Face = x["+cp[0]+"] y["+cp[1]+"] z["+cp[2]+"]" );

		// increment # of faces around the vertex
		_surroundingFaces[firstV]++;
		_surroundingFaces[secondV]++;
		_surroundingFaces[thirdV]++;

		return cp;

	}

	private void normalizeNormals( ArrayList<Float> mBuffer ){
		for(int i = 0; i < (mBuffer.size() / VERTEX_ARRAY_SIZE); i++){
			float [] tempVect = { mBuffer.get(i * VERTEX_ARRAY_SIZE),  mBuffer.get(i * VERTEX_ARRAY_SIZE + 1), mBuffer.get(i * VERTEX_ARRAY_SIZE + 2) };
			for(int j = 0 ; j < (mBuffer.size() / VERTEX_ARRAY_SIZE); j++){
				if(tempVect[0] == mBuffer.get(j * VERTEX_ARRAY_SIZE) && tempVect[1] == mBuffer.get(j * VERTEX_ARRAY_SIZE + 1) && tempVect[2] == mBuffer.get(j * VERTEX_ARRAY_SIZE + 2) & i != j ){
					//Log.d(LOG_TAG, "EQUAL!! Sum vectors ["+i+"]["+j+"]");

					float [] v1 = { mBuffer.get(i * VERTEX_ARRAY_SIZE + 3) , mBuffer.get(i * VERTEX_ARRAY_SIZE + 4) , mBuffer.get(i * VERTEX_ARRAY_SIZE + 5) };
					float [] v2 = { mBuffer.get(j * VERTEX_ARRAY_SIZE + 3) , mBuffer.get(j * VERTEX_ARRAY_SIZE + 4) , mBuffer.get(j * VERTEX_ARRAY_SIZE + 5) };

					float [] newNormal = sumVectors(v1, v2);

					mBuffer.set(i * VERTEX_ARRAY_SIZE + 3, newNormal[0]);
					mBuffer.set(i * VERTEX_ARRAY_SIZE + 4, newNormal[1]);
					mBuffer.set(i * VERTEX_ARRAY_SIZE + 5, newNormal[2]);

				}

			}
		}
	}

	private float [] sumVectors (  float [] v1 , float [] v2 ){
		float sum[] = {v1[0]+v2[0], v1[1]+v2[1], v1[2]+v2[2]};
		return sum;
	}

	/**
	 * Calculates the cross product of two 3d vectors
	 */
	public float[] crossProduct(float[] v0, float[] v1)
	{
		float crossProduct[] = new float[3];

		crossProduct[0] = v0[1] * v1[2] - v0[2] * v1[1];
		crossProduct[1] = v0[2] * v1[0] - v0[0] * v1[2];
		crossProduct[2] = v0[0] * v1[1] - v0[1] * v1[0];

		return crossProduct;
	}

	/***************************
	 * GET/SET
	 *************************/

	public float[] get_vertices() {
		return _vertices;
	}

	public void set_vertices(float[] _vertices) {
		this._vertices = _vertices;
	}
	public short[] get_indices() {
		return _indices;
	}

	public FloatBuffer get_vb() {
		return this._vb;
	}
	
	public FloatBuffer get_nb() {
		return this._nb;
	}
	
	public ShortBuffer get_ib() {
		return this._ib;
	}

	public float getRotationAngleX(){
		return rAngleX;
	}

	public float getRotationAngleY(){
		return rAngleY;
	}

	public float getRotationAngleZ(){
		return rAngleZ;
	}

	public void setRotationAngleX(float angle){
		rAngleX = angle;
	}

	public void setRotationAngleY(float angle){
		rAngleY = angle;
	}

	public void setRotationAngleZ(float angle){
		rAngleZ = angle;
	}

	public void setPosition(float x, float y, float z ){
		getPos[0] = x;
		getPos[1] = y;
		getPos[2] = z;
	}

	public void setLightPos(float x, float y, float z ){
		lightPos[0] = x;
		lightPos[1] = y;
		lightPos[2] = z;
	}	

	public float [] getPosition(){
		return getPos;
	}

	public void setScale(float x, float y, float z ){
		scaleX = x;
		scaleY = y;
		scaleZ = z;
	}


}