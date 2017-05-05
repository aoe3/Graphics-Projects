package ray2.shader;

import egl.math.Vector3;
import egl.math.Vector3d;
import ray2.RayTracer;
import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import egl.math.Colord;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Glass extends Shader {

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }


	public Glass() { 
		refractiveIndex = 1.0;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "glass " + refractiveIndex + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Glass shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		// TODO#A7: fill in this function.
        // 1) Determine whether the ray is coming from the inside of the surface or the outside.
//		Vector3d normal;
//		float n1, n2;
//		if (ray.direction.clone().dot(record.normal) < 0) {
//			// ray is inside
//			normal = record.normal.clone().negate();
//			n1 = refractiveIndex;
//			n2 = 1.0f;
//		} else {
//			normal = record.normal;
//			n1 = 1.0f;
//			n2 = refractiveIndex;
//		}
//		Vector3d reflected = normal.clone().mul(normal.clone().dot(ray.direction) * 2).sub(ray.direction);
//
//		double fresnel = fresnel(normal, reflected, refractiveIndex);
//        // 2) Determine whether total internal reflection occurs.
//		if (fresnel == 1) {
//			// total internal reflection
//		}
//
//		// 3) Compute the reflected ray and refracted ray (if total internal reflection does not occur)
//        //    using Snell's law and call RayTracer.shadeRay on them to shade them
//		else {
//			float theta1 = Math.acos(normal.clone().dot(ray.direction));
//			float theta2 = n1 * Math.sin(theta1) / n2;
////			Vector3d refracted = normal.clone().mul(normal.clone().dot(ray.direction) * 2).sub(ray.direction).mul(fresnel);
//			reflected.mul(fresnel);
//			reflected.mul(1-fresnel);
//		}
	}

}