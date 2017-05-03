package ray2.light;


import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;

import java.util.Random;

/**
 * This class represents an area source that is rectangular, specified by a
 * frame in the same way as a camera.  It has constant radiance across the
 * whole surface.
 *
 * @author srm, zechenz
 */
public class RectangleLight extends Light {
	
	/** Where the light is located in space. */
	public final Vector3d position = new Vector3d();
	public void setPosition(Vector3d position) { this.position.set(position); }

	/** The direction the light is facing. */
	protected final Vector3d normalDir = new Vector3d(0, 0, -1);
	public void setNormalDir(Vector3d normalDir) { this.normalDir.set(normalDir); }
	
	/** The upwards direction, which is aligned with the light's height axis. */
	protected final Vector3d upDir = new Vector3d(0, 1, 0);
	public void setUpDir(Vector3d upDir) { this.upDir.set(upDir); }
	
	/** The height of the source, in world units. */
	protected double height = 1.0;
	public void setHeight(double height) { this.height = height; }
	
	/** The width of the source, in world units. */
	protected double width = 1.0;
	public void setWidth(double width) { this.width = width; }
	
	/*
	 * TODO#A7: declare necessary variables 
	 * e.g. the orthonormal basis vectors for the rect area light
	 */
	Vector3d u, v, w;

	/**
	 * Initialize the derived view variables to prepare for using the camera.
	 */
	public void init() {
		// TODO#A7: Fill in this function
		// 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on normalDir and upDir
        // 2) Set up the helper variables if needed
//		w.set(normalDir).negate().normalize();
//		u.set(upDir).cross(w).normalize();
//		v.set(w).cross(u).normalize();
	}

	/**
	 * Sample the illumination due to this light source at a given shading point.
	 * A rectantle light provides illumination from a range of directions; a direction
	 * is chosen by selecting a point on the source.  A rectangle source is one-sided;
	 * it provides no illumination to points that are behind it.
	 *
	 *    lRec.direction is the direction from the shading point to the source
	 *    lRec.distance is the distance between the shading point and the source
	 *    lRec.attenuation is the inverse square of the distance to the source
	 *    lRec.probability is a probability density over the source's area.
	 *
	 * @param record the record where the output is written:
	 * @param shadingPoint the surface point where illumination is being computed
	 */
	@Override
	public void sample(LightSamplingRecord lRec, Vector3d shadingPoint) {
		// TODO#A7: Fill in this function
		// 1. sample light source point on the rectangle area light in uniform-random fashion
//		Random r = new Random();
//		double x = r.nextDouble()*width;
//		double y = r.nextDouble()*height;
//		Vector2d samplePoint = position.clone().add();
//		// 2. compute the l vector, i.e. the direction the light incidents on the shading point
//		Vector3d l =
//		// 3. compute the distance between light point and shading point, and get attenuation
//		// 4. compute the probablity this light point is sampled, which is used for Monte-Carlo integration
//		// 5. write relevant info to LightSamplingRecord object
	}

	/**
	 * Default constructor.  Produces a unit square light at the origin facing -z.
	 */
	public RectangleLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "RectangleLight: " + width + "x" + height + " @ " + position + " " + intensity + "; normal " + normalDir + "; up " + upDir;
	}
}