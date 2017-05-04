package ray2.shader;

import egl.math.*;
import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import ray2.light.Light;
import ray2.light.LightSamplingRecord;

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
				Vector3d L = lRec.direction.clone().normalize();
				Ray VRay = new Ray();
				scene.getCamera().getRay(VRay, iRec.location.x, iRec.location.y);
				Vector3d V = VRay.direction.clone().normalize();
				Colord BRDFval = new Colord();
				Colord kD;
				if (texture == null) {
					kD = diffuseColor;
				} else {
					kD = texture.getTexColor(new Vector2d(iRec.location.x, iRec.location.y));
				}
				Vector3d N = iRec.normal;
				evalBRDF(L, V, iRec.normal, kD, BRDFval);
				// 5) Compute the final color using the BRDF value and the information in the
				//    light sampling record.
				outIntensity.add(BRDFval.clone().mul(l.intensity).mul(L.clone().dot(N)).mul(lRec.attenuation).div(lRec.probability));
			}
		}
	}
}