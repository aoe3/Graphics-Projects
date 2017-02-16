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
 * A Lambertian material scatters light equally in all directions. BRDF value is
 * a constant
 *
 * @author ags, zz
 */
public class Lambertian extends Shader {

	/** The color of the surface. */
	protected final Colorf diffuseColor = new Colorf(Color.White);
	public void setDiffuseColor(Colorf inDiffuseColor) { diffuseColor.set(inDiffuseColor); }
	public Colorf getDiffuseColor() {return new Colorf(diffuseColor);}

	public Lambertian() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "lambertian: " + diffuseColor;
	}

	/**
	 * Evaluate the intensity for a given intersection using the Lambert shading model.
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
		// 4) Compute the color of the point using the Lambert shading model. Add this value
		//    to the output.
		for (int i=0; i<scene.getLights().size(); i++) {
			Light light = new Light();
			if (!isShadowed(scene,light,record,ray)) {
//				Vector3d dir = record.location.clone().sub(light.position);
//				Colorf kl = light.intensity;
//				Colorf kd = diffuseColor;
//				Vector3d nw = record.normal.clone().dot(dir);
//				double r = (light.position.clone().sub(record.location)).dist();
//				outIntensity.set(kl/Math.pow(r,2)*kd*Math.max(nw,0));

				Vector3d dir = (record.location.clone().sub(scene.getLights().get(i).position)).normalize();
				double nDotL = (record.normal.clone()).dot(dir.clone());
				double color =(Math.max(nDotL, 0.0));
				float red = (float) (diffuseColor.clone().x * scene.getLights().get(i).intensity.clone().x * color);
				float grn = (float) (diffuseColor.clone().y * scene.getLights().get(i).intensity.clone().y * color);
				float blu = (float) (diffuseColor.clone().z * scene.getLights().get(i).intensity.clone().z * color);
				outIntensity.set((outIntensity.clone().x + red), (outIntensity.clone().y + grn), (outIntensity.clone().z + blu));
			}
		}
	}
}
