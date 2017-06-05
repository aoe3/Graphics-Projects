package ray2.accel;

import ray2.Ray;
import egl.math.Vector3d;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Vector3d minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Vector3d();
		maxBound = new Vector3d();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Vector3d minBound, Vector3d maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Vector3d();
		this.minBound.set(minBound);
		this.maxBound = new Vector3d();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
	
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO#A7: fill in this function.
		float ax = 1.0f / (float)ray.direction.x;
		float ay = 1.0f / (float)ray.direction.y;
		float az = 1.0f / (float)ray.direction.z;

		float txMin, txMax;
		float tyMin, tyMax;
		float tzMin, tzMax;

		if (ax >= 0) {
			txMin = ax * (float) (minBound.x - ray.origin.x);
			txMax = ax * (float) (maxBound.x - ray.origin.x);
		} else {
			txMin = ax * (float) (maxBound.x - ray.origin.x);
			txMax = ax * (float) (minBound.x - ray.origin.x);
		}

		if (ay >= 0) {
			tyMin = ay * (float) (minBound.y - ray.origin.y);
			tyMax = ay * (float) (maxBound.y - ray.origin.y);
		} else {
			tyMin = ay * (float) (maxBound.y - ray.origin.y);
			tyMax = ay * (float) (minBound.y - ray.origin.y);
		}

		if (az >= 0) {
			tzMin = az * (float) (minBound.z - ray.origin.z);
			tzMax = az * (float) (maxBound.z - ray.origin.z);
		} else {
			tzMin = az * (float) (maxBound.z - ray.origin.z);
			tzMax = az * (float) (minBound.z - ray.origin.z);
		}

		if (txMin > tyMax || txMax < tyMin) {
			return false;
		}

		float tEnter = Math.max(Math.max(txMin, tyMin), txMin);
		float tExit = Math.min(Math.min(txMax, tyMax), txMax);

		if (tEnter > tzMax || tExit < tzMin) {
			return false;
		}

		return true;
	}
}
