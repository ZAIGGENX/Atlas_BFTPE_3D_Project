// Vertex Shader
uniform mat4 normalMatrix;
uniform mat4 uMVPMatrix; 

// eye pos
uniform vec3 eyePos;

// position and normal of the vertices
attribute vec4 vPosition; 
attribute vec3 aNormal;

// texture variables
attribute vec2 textureCoord;
varying vec2 tCoord;

// lighting
uniform vec4 lightPos;
uniform vec4 lightColor;
varying vec4 mEyePos;

// normals to pass on
varying vec3 vNormal;
varying vec3 EyespaceNormal;

varying vec3 lightDir, eyeVec;

// Varyings
varying vec3 v_ecNormal;
varying vec4 v_lightColor;

void main() { 

	tCoord = textureCoord;
	vec3 mcNormal = aNormal;

	//mEyePos = vec4(0.0, 10.0, -13.0, 0.0);
	mEyePos = lightPos;
	v_lightColor = lightColor;

    // Calculate and normalize eye space normal
    vec3 ecNormal = vec3(mEyePos * vec4(mcNormal, 0.0));
    ecNormal = ecNormal / length(ecNormal);
    v_ecNormal = ecNormal;

	// the vertex position
	vec4 position = uMVPMatrix * vPosition; 

	gl_Position = position;

}
