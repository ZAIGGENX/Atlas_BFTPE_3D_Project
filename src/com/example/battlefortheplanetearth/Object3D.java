package com.example.battlefortheplanetearth;

import android.content.Context;

public class Object3D {

	public Mesh mMesh;
	private int meshID;
	
	public Object3D(int meshID, boolean hasTexture, int textureID, Context context){
		mMesh = new Mesh(textureID, context);
		this.meshID = meshID;
	}

	public void loadFile(){
		mMesh.loadFile(meshID);
	}

	public void draw(float[] mvpMatrix){
		if(mMesh != null)
			mMesh.draw(mvpMatrix);
	}

	//// SET/GET ////

	public float getRotationAngleX(){
		return mMesh.getRotationAngleX();
	}

	public float getRotationAngleY(){
		return mMesh.getRotationAngleY();
	}

	public float getRotationAngleZ(){
		return mMesh.getRotationAngleY();
	}

	public void setRotationAngle(float x_angle, float y_angle , float z_angle){
		mMesh.setRotationAngleX(x_angle);
		mMesh.setRotationAngleY(y_angle);
		mMesh.setRotationAngleZ(z_angle);
	}

	public void setRotationAngleX(float angle){
		mMesh.setRotationAngleX(angle);
	}

	public void setRotationAngleY(float angle){
		mMesh.setRotationAngleY(angle);
	}

	public void setRotationAngleZ(float angle){
		mMesh.setRotationAngleZ(angle);
	}

	public void setPosition(float x, float y, float z ){
		mMesh.setPosition(x, y, z);
	}

	public float [] getPosition(){
		return mMesh.getPosition();
	}
	
	public void setScale(float x, float y, float z ){
		mMesh.setScale(x, y, z);
	}

	public void setLightPos(float x, float y, float z ){
		mMesh.setLightPos(x, y, z);
	}

	public void setLightColor(float r, float g, float b, float a){
		mMesh.setLightColor(r, g, b, a);
	}

}