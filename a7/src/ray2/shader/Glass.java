package ray2.shader;

import egl.math.Vector2;
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
		Vector3d normal;
		Vector3d d = ray.direction.clone().normalize();
		double n1, n2, fresN;
		if (d.clone().negate().dot(record.normal.clone()) < 0) {
//			System.out.println("Ray is inside");
			// ray is inside
			normal = record.normal.clone().normalize().negate();
			n1 = refractiveIndex;
			n2 = 1.0f;
			fresN = 1.0/refractiveIndex;
		} else {
//			System.out.println("Ray is outside");
			// ray is outside
			normal = record.normal.clone().normalize();
			n1 = 1.0f;
			n2 = refractiveIndex;
			fresN = refractiveIndex;
		}
		Vector3d reflected = normal.clone().mul(normal.clone().dot(d.clone().negate()) * 2).sub(d.clone().negate()).normalize();

		double fresnel = fresnel(normal, reflected, fresN);

        // 2) Determine whether total internal reflection occurs.
		if (fresnel == 1) {
			Ray reflectedRay = new Ray(record.location, reflected.clone());
			reflectedRay.makeOffsetRay();
			// total internal reflection
			// 3a) Compute the reflected ray using Snell's law
			RayTracer.shadeRay(outIntensity, scene, reflectedRay, depth);
		}

		// 3b) Compute the reflected ray and refracted ray (if total internal reflection does not occur)
        //    using Snell's law and call RayTracer.shadeRay on them to shade them
		else {
			double theta1 = Math.acos(normal.clone().dot(d.clone().negate()));
			double theta2 = Math.asin(Math.sin(theta1) * n1/n2);
//			double cos2theta2 = 1 - ((n1*n1*(1-Math.cos(theta1)*Math.cos(theta1)))/(n2*n2));
//			double theta2 = Math.acos(Math.sqrt(cos2theta2));
//			System.out.println("ASIN" + Math.asin(Math.sin(theta1) * n1/n2));
//			System.out.println("THETA" + theta2);
//			reflected.mul(1.0-fresnel);
			Vector3d pt1 = (d.clone().add(normal.clone().mul(Math.cos(theta1)))).mul(n1).div(n2);
			Vector3d pt2 = normal.clone().mul(Math.cos(theta2));
			Vector3d refracted = pt1.clone().sub(pt2.clone()).normalize();
//			refracted.mul(fresnel);

			Colord reflectedColor = new Colord();
			Colord refractedColor = new Colord();

//			System.out.println(refracted);
			Ray reflectedRay = new Ray(record.location, reflected);
			Ray refractedRay = new Ray(record.location, refracted);

			RayTracer.shadeRay(reflectedColor, scene, reflectedRay, depth+1);
			RayTracer.shadeRay(refractedColor, scene, refractedRay, depth+1);

			outIntensity.add(reflectedColor.mul(fresnel));
			outIntensity.add(refractedColor.mul(1.0-fresnel));
		}
	}
}