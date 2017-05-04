package ray2.shader;

import egl.math.Vector2d;
import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import ray2.light.Light;
import ray2.light.LightSamplingRecord;
import egl.math.Color;
import egl.math.Colord;
import egl.math.Vector3d;

public abstract class BRDFShader extends Shader {

	/** The color of the diffuse reflection, if there is no texture. */
	protected final Colord diffuseColor = new Colord(Color.White);
	public void setDiffuseColor(Colord diffuseColor) { this.diffuseColor.set(diffuseColor); }

	/**
	 * Evaluate the BRDF for this material.
	 * @param L a unit vector toward the light
	 * @param V a unit vector toward the viewer
	 * @param N a unit surface normal
	 * @param kD the diffuse coefficient of the surface at the shading point.
	 * @param outColor the computed BRDF value.
	 */
	protected abstract void evalBRDF(Vector3d L, Vector3d V, Vector3d N,
			Colord kD, Colord outColor);

	public BRDFShader() {
		super();
	}


	/**
	 * Evaluate the intensity for a given intersection using the CookTorrance shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param iRec The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord iRec,
			int depth) {
		// TODO#A7 Fill in this function.
		// 1) Loop through each light in the scene.
		for (Light l : scene.getLights()) {
			// 3) Use Light.sample() to generate a direction toward the light.
			LightSamplingRecord lRec = new LightSamplingRecord();
			l.sample(lRec, iRec.location);
			
			// 2) If the intersection point is shadowed, skip the calculation for the light.
			//	  See Shader.java for a useful shadowing function.
			
			if (!isShadowed(scene, lRec, iRec, ray)) {
				// 4) Evaluate the BRDF using the abstract evalBRDF method.
				// L a unit vector toward the light
				Vector3d L = lRec.direction.clone().normalize();
				
				
				// normal of the intersect point
				Vector3d N = iRec.normal.clone().normalize();
				
				// V a unit vector toward the viewer
				Vector3d V = ray.direction.clone().negate().normalize();
				
				// dummy color for evalBRDF
				Colord BRDFval = new Colord();
				// find kD for evalBRDF
				Colord kD;
				if (texture == null) {
					kD = diffuseColor;
				} else {
					// appended from A2
					kD = this.texture.getTexColor(iRec.texCoords);
				}
				
				// populate dummy color
				evalBRDF(L, V, N, kD, BRDFval);
				// 5) Compute the final color using the BRDF value and the information in the
				//    light sampling record.
				outIntensity.add(BRDFval.mul(l.intensity).mul(L.dot(N)).mul(lRec.attenuation).div(lRec.probability));
			}
		}
	}
}
