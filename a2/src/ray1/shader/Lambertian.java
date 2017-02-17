package ray1.shader;

import egl.math.*;
import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.Scene;

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
			Light light = scene.getLights().get(i);
			if (!isShadowed(scene,light,record,ray)) {
				if (texture != null) {
					setDiffuseColor(texture.getTexColor(new Vector2(record.texCoords)));
				}

				Vector3d dir = (record.location.clone().sub(light.position)).normalize().negate();
				double nDotL = (record.normal.clone()).dot(dir.clone());
				double color = (Math.max(nDotL, 0.0));
				double r = (new Vector3d()).addMultiple(1,light.position).dist(record.location);
				float red = (float) (diffuseColor.clone().x * light.intensity.clone().x * color / Math.pow(r,2));
				float grn = (float) (diffuseColor.clone().y * light.intensity.clone().y * color / Math.pow(r,2));
				float blu = (float) (diffuseColor.clone().z * light.intensity.clone().z * color / Math.pow(r,2));
				outIntensity.set((outIntensity.clone().x + red), (outIntensity.clone().y + grn), (outIntensity.clone().z + blu));
			}
		}
	}
}
