
package ray1.shader;

import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.Scene;
import egl.math.Color;
import egl.math.Colorf;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Phong extends Shader {

	/** The color of the diffuse reflection. */
	protected final Colorf diffuseColor = new Colorf(Color.White);
	public void setDiffuseColor(Colorf diffuseColor) { this.diffuseColor.set(diffuseColor); }
	public Colorf getDiffuseColor() {return new Colorf(diffuseColor);}

	/** The color of the specular reflection. */
	protected final Colorf specularColor = new Colorf(Color.White);
	public void setSpecularColor(Colorf specularColor) { this.specularColor.set(specularColor); }
	public Colorf getSpecularColor() {return new Colorf(specularColor);}

	/** The exponent controlling the sharpness of the specular reflection. */
	protected float exponent = 1.0f;
	public void setExponent(float exponent) { this.exponent = exponent; }
	public float getExponent() {return exponent;}

	public Phong() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Phong shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colorf outIntensity, Scene scene, Ray ray, IntersectionRecord record) {
		// TODO#A2: Fill in this function.
		// 1) Loop through each light in the scene.
		// 2) If the intersection point is shadowed, skip the calculation for the light.
		//	  See Shader.java for a useful shadowing function.
		// 3) Compute the incoming direction by subtracting
		//    the intersection point from the light's position.
		// 4) Compute the color of the point using the Phong shading model. Add this value
		//    to the output.

		for (int i=0; i<scene.getLights().size(); i++) {
			Light light = scene.getLights().get(i);
			if (!isShadowed(scene,light,record,ray)) {
				Vector3d wi = record.location.clone().sub(light.position).normalize().negate();
				double nw = record.normal.clone().dot(wi);
				if (nw > 0) {
					Vector3d kl = new Vector3d(light.intensity);
					Vector3d kd = new Vector3d(diffuseColor);
					Vector3d ks = new Vector3d(specularColor);
					double r = record.location.clone().dist(light.position);
					double r2 = Math.pow(r,2);
					Vector3d w0 = ray.direction;
					Vector3d h = wi.clone().add(w0).normalize();
					double maxnhp = Math.pow(Math.max(record.normal.clone().dot(h), 0), exponent);
					Vector3d contribution = kl.clone().div(r2).mul(kd.mul(nw).add(ks.mul(maxnhp)));
					outIntensity.add(new Colorf((float)contribution.x, (float)contribution.y, (float)contribution.z));
				}
			}
		}
	}
}