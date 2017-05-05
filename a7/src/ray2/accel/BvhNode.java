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
		float txMin = (float)((minBound.x - ray.origin.x) / ray.direction.x);
		float tyMin = (float)((minBound.y - ray.origin.y) / ray.direction.y);
		float tzMin = (float)((minBound.z - ray.origin.z) / ray.direction.z);

		float txMax = (float)((maxBound.x - ray.origin.x) / ray.direction.x);
		float tyMax = (float)((maxBound.y - ray.origin.y) / ray.direction.y);
		float tzMax = (float)((maxBound.z - ray.origin.z) / ray.direction.z);

		float txEnter = Math.min(txMin, txMax);
		float txExit = Math.max(txMin, txMax);

		float tyEnter = Math.min(tyMin, tyMax);
		float tyExit = Math.max(tyMin, tyMax);

		if (txEnter > tyExit || txExit < tyEnter) {
			return false;
		}

		float tEnter = Math.max(Math.max(txEnter, tyEnter), txEnter);
		float tExit = Math.min(Math.min(txExit, tyExit), txExit);

		float tzEnter = Math.min(tzMin, tzMax);
		float tzExit = Math.max(tzMin, tzMax);

		if (tEnter > tzExit || tExit < tzEnter) {
			return false;
		}

		return true;
	}
}
