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

// normals to pass on
varying vec3 vNormal;
varying vec3 EyespaceNormal;

varying vec3 lightDir, eyeVec;

// dot function between two vectors
float Dot(vec3 v1, vec3 v2) {
	return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
}


void main() { 

	tCoord = textureCoord;

	// calculate the tangent & binormal
	vec3 tangent; 
	vec3 binormal; 
	
	vec3 c1 = cross(aNormal, vec3(0.0, 0.0, 1.0)); 
	vec3 c2 = cross(aNormal, vec3(0.0, 1.0, 0.0));

	if(length(c1)>length(c2))
	{
		tangent = c1;	
	}
	else
	{
		tangent = c2;	
	}

	tangent = normalize(tangent);

	// eye normal
	EyespaceNormal = vec3(normalMatrix * vec4(aNormal, 1.0));
	vec3 eyespaceTangent = vec3(normalMatrix * vec4(tangent, 1.0));
	binormal = cross(EyespaceNormal, eyespaceTangent);

	// the vertex position
	vec4 position = uMVPMatrix * vPosition; 

	// light dir
	vec3 tmpVec = lightPos.xyz - position.xyz;
	// convert to tangent space
	lightDir.x = Dot(tmpVec, eyespaceTangent);
	lightDir.y = Dot(tmpVec, binormal);
	lightDir.z = Dot(tmpVec, EyespaceNormal);

	// eye space
	tmpVec = -position.xyz;
	// convert to tangent space
	eyeVec.x = Dot(tmpVec, eyespaceTangent);
	eyeVec.y = Dot(tmpVec, binormal);
	eyeVec.z = Dot(tmpVec, EyespaceNormal);

	gl_Position = position;

}
