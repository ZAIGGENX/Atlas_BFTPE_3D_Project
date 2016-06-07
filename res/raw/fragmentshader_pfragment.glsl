precision highp float;

// texture variables
uniform sampler2D texture1; // color texture

// texture variables
varying vec2 tCoord;

struct DirectionalLight {
    vec3 direction;
    vec3 halfplane;
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
};

struct Material {
    vec4 ambientFactor;
    vec4 diffuseFactor;
    vec4 specularFactor;
    float shininess;
};

// Light
DirectionalLight u_directionalLight;

// Material
Material u_material;

varying vec3 v_ecNormal;
varying vec4 v_lightColor;

void main() { 

    // get the base color
    vec4 baseColor = texture2D(texture1, tCoord);

    u_directionalLight.direction = vec3(-5.0, 4.0, 0.25);
    u_directionalLight.ambientColor = vec4(0.3, 0.3, 0.35, 1.0);
    u_directionalLight.diffuseColor = v_lightColor;
    u_directionalLight.specularColor = vec4(0.3, 0.2, 0.25, 1.0);
    
    u_material.ambientFactor = vec4(2.0, 2.0, 2.0, 1.0);
    u_material.diffuseFactor = vec4(0.5, 0.5, 0.5, 1.0);
    u_material.specularFactor = vec4(1.0, 1.0, 1.0, 1.0);
    u_material.shininess = 1.0;

    // Normalize v_ecNormal
    vec3 ecNormal = v_ecNormal / length(v_ecNormal);

    float ecNormalDotLightDirection = max(0.0, dot(ecNormal, u_directionalLight.direction));
    float ecNormalDotLightHalfplane = max(0.0, dot(ecNormal, u_directionalLight.halfplane));

    // Calculate ambient light
    vec4 ambientLight = u_directionalLight.ambientColor * u_material.ambientFactor;

    // Calculate diffuse light
    vec4 diffuseLight = ecNormalDotLightDirection * u_directionalLight.diffuseColor * u_material.diffuseFactor;

    // Calculate specular light
    vec4 specularLight = vec4(0.0);
    if (ecNormalDotLightHalfplane > 0.0) {
        specularLight = pow(ecNormalDotLightHalfplane, u_material.shininess) * u_directionalLight.specularColor * u_material.specularFactor;
    } 


    vec4 light = baseColor * (diffuseLight + ambientLight + specularLight);

    gl_FragColor = light;
}