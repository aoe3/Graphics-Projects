package splines;
import java.util.ArrayList;

import mesh.OBJFace;
import mesh.OBJMesh;
import mesh.OBJMesh_Archive;
import egl.NativeMem;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3i;
import egl.math.Vector4;


public abstract class SplineCurve {
	private float epsilon;
	
	//Spline Control Points
	private ArrayList<Vector2> controlPoints;
	
	//Bezier Curves that make up this Spline
	private ArrayList<CubicBezier> bezierCurves;
	
	//Whether or not this curve is a closed curve
	private boolean isClosed;
	
	public static final float DIST_THRESH = 0.15f;
	public static final int MIN_OPEN_CTRL_POINTS= 4,
			                           MIN_CLOSED_CTRL_POINTS= 3,
			                           MAX_CTRL_POINTS= 20;

	public SplineCurve(ArrayList<Vector2> controlPoints, boolean isClosed, float epsilon) throws IllegalArgumentException {
		if(isClosed) {
			if(controlPoints.size() < MIN_CLOSED_CTRL_POINTS)
				throw new IllegalArgumentException("Closed Splines must have at least 3 control points.");
		} else {
			if(controlPoints.size() < MIN_OPEN_CTRL_POINTS)
				throw new IllegalArgumentException("Open Splines must have at least 4 control points.");
		}

		this.controlPoints = controlPoints;
		this.isClosed = isClosed;
		this.epsilon = epsilon;
		setBeziers();
	}
	
	public boolean isClosed() {
		return this.isClosed;
	}
	
	public boolean setClosed(boolean closed) {
		if(this.isClosed && this.controlPoints.size() == 3) {
			System.err.println("You must have at least 4 control points to make an open spline.");
			return false;
		}
		this.isClosed= closed;
		setBeziers();
		return true;
	}
	
	public ArrayList<Vector2> getControlPoints() {
		return this.controlPoints;
	}
	
	public void setControlPoint(int index, Vector2 point) {
		this.controlPoints.set(index, point);
		setBeziers();
	}
	
	public boolean addControlPoint(Vector2 point) {
		if(this.controlPoints.size() == MAX_CTRL_POINTS) {
			System.err.println("You can only have "+ SplineCurve.MAX_CTRL_POINTS + " control points per spline.");
			return false;
		}
		/* point= (x0, y0), prev= (x1, y1), curr= (x2,y2)
		 * 
		 * v= [ (y2-y1), -(x2-x1) ]
		 * 
		 * r= [ (x1-x0), (y1-y0) ]
		 * 
		 * distance between point and line prev -> curr is v . r
		 */
		Vector2 curr, prev;
		Vector2 r= new Vector2(), v= new Vector2();
		float distance= Float.POSITIVE_INFINITY;
		int index= -1;
		for(int i= 0; i < controlPoints.size(); i++) {
			curr= controlPoints.get(i);
			if(i == 0) {
				if(isClosed) {
					// add line between first and last ctrl points
					prev= controlPoints.get(controlPoints.size()-1);
				} else {
					continue;
				}
			} else {
				prev= controlPoints.get(i-1);
			}
			v.set(curr.y-prev.y, -(curr.x-prev.x)); v.normalize();
			r.set(prev.x-point.x, prev.y-point.y);
			float newDist = Math.abs(v.dot(r));
			Vector2 v2 = curr.clone().sub(prev);
			v2.mul(1.0f / v2.lenSq());
			float newParam = -v2.dot(r);
			if(newDist < DIST_THRESH && newDist <= distance && 0 < newParam && newParam < 1) {
				distance= newDist;
				index= i;
			}
		}
		
		if (index >= 0) {
			controlPoints.add(index, point);
			setBeziers();
			return true;
		}
		System.err.println("Invalid location, try selecting a point closer to the spline.");
		return false;
	}
	
	public boolean removeControlPoint(int index) {
		if(this.isClosed) {
			if(this.controlPoints.size() == MIN_CLOSED_CTRL_POINTS) {
				System.err.println("You must have at least "+MIN_CLOSED_CTRL_POINTS+" for a closed Spline.");
				return false;
			}
		} else {
			if(this.controlPoints.size() == MIN_OPEN_CTRL_POINTS) {
				System.err.println("You must have at least "+MIN_OPEN_CTRL_POINTS+" for an open Spline.");
				return false;
			}
		}
		this.controlPoints.remove(index);
		setBeziers();
		return true;
	}
	
	public void modifyEpsilon(float newEps) {
		epsilon = newEps;
		setBeziers();
	}
	
	public float getEpsilon() {
		return epsilon;
	}
	
	/**
	 * Returns the sequence of 2D vertices on this Spline specified by the sequence of Bezier curves
	 */
	public ArrayList<Vector2> getPoints() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : bezierCurves)
			for(Vector2 p : b.getPoints())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Returns the sequence of normals on this Spline specified by the sequence of Bezier curves
	 */
	public ArrayList<Vector2> getNormals() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : bezierCurves)
			for(Vector2 p : b.getNormals())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Returns the sequence of tangents on this Spline specified by the sequence of Bezier curves
	 */
	public ArrayList<Vector2> getTangents() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : bezierCurves)
			for(Vector2 p : b.getTangents())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Using this.controlPoints, create the CubicBezier objects that make up this curve and
	 * save them to this.bezierCurves. Assure that the order of the Bezier curves that you
	 * add to bezierCurves is the order in which the overall Spline is chained together.
	 * If the spline is closed, include additional CubicBeziers to account for this.
	 */
	private void setBeziers() {
		//TODO A5
		//initalize bezier curves
		this.bezierCurves = new ArrayList<CubicBezier>();
		//iterate through points
		for (int i=this.controlPoints.size()-3; i>=1; i--){
			Vector2 controlPt1 = this.controlPoints.get(i-1);
			Vector2 controlPt2 = this.controlPoints.get(i);
			Vector2 controlPt3 = this.controlPoints.get(i+1);
			Vector2 controlPt4 = this.controlPoints.get(i+2);
			
			CubicBezier bezPoints = toBezier(controlPt1, controlPt2, controlPt3, controlPt4, epsilon);
			this.bezierCurves.add(bezPoints);
		}
		//if closed, connect to 0
		if (isClosed){
			//connect last, first, second, third
			int i = this.controlPoints.size()-1;
			Vector2 controlPt1 = this.controlPoints.get(i);
			Vector2 controlPt2 = this.controlPoints.get(0);
			Vector2 controlPt3 = this.controlPoints.get(1);
			Vector2 controlPt4 = this.controlPoints.get(2);
			CubicBezier bezPoints = toBezier(controlPt1, controlPt2, controlPt3, controlPt4, epsilon);
			this.bezierCurves.add(bezPoints);
			
			//second-to-last, last, first, and second
			Vector2 controlPt5 = this.controlPoints.get(i-1);
			Vector2 controlPt6 = this.controlPoints.get(i);
			Vector2 controlPt7 = this.controlPoints.get(0);
			Vector2 controlPt8 = this.controlPoints.get(1);
			CubicBezier bezPoints2 = toBezier(controlPt5, controlPt6, controlPt7, controlPt8, epsilon);
			this.bezierCurves.add(bezPoints2);
			
			//third-to_last, second-to-last, last, first
			Vector2 controlPt9 = this.controlPoints.get(i-2);
			Vector2 controlPt10 = this.controlPoints.get(i-1);
			Vector2 controlPt11 = this.controlPoints.get(i);
			Vector2 controlPt12 = this.controlPoints.get(0);
			CubicBezier bezPoints3 = toBezier(controlPt9, controlPt10, controlPt11, controlPt12, epsilon);
			this.bezierCurves.add(bezPoints3);
		} 
	}
	
	/**
	 * Reverses the tangents and normals associated with this Spline
	 */
	public void reverseNormalsAndTangents() {
		for(CubicBezier b : bezierCurves) {
			for(Vector2 p : b.getNormalReferences())
				p.mul(-1);
			for(Vector2 p : b.getTangentReferences())
				p.mul(-1);
		}
	}
	
	//Debug code
	public double getMaxAngle() {
		ArrayList<Vector2> myPoints = getPoints();
		double max = 0;
		for(int i = 0; i < myPoints.size() - 2; ++i) {
			Vector2 A = myPoints.get(i);
			Vector2 B = myPoints.get(i+1);
			Vector2 C = myPoints.get(i+2);
			
			Vector2 v1 = B.clone().sub(A);
			Vector2 v2 = C.clone().sub(B);
			
			v1.normalize();
			v2.normalize();
			
			double cur = Math.acos(v1.dot(v2));
			if (cur > max) max = cur;
		}
		return max;
	}
	
	
	public abstract CubicBezier toBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, float eps);
	
	
	/**
	 * Given a curve that defines the cross section along the axis, fill the three GLBuffer objects appropriately.
	 * Here, we revolve the crossSection curve about the positive Z-axis.
	 * @param crossSection, the 2D spline for which every point defines the cross section of the surface
	 * @param data, a MeshData where we will output our triangle mesh
	 * @param scale > 0, parameter that controls how much the resulting surface should be scaled
	 * @param sliceTolerance > 0, the maximum angle in radians between adjacent vertical slices.
	 */
	public static void build3DRevolution(SplineCurve crossSection, OBJMesh mesh, float scale, float sliceTolerance) {
		//TODO A5
		
		//Determine the proper angle to divide each slice by
		float maxAngleInRads;
		
		//If sliceTolerance is not perfect divisor of 360
		float remainder = (float)(Math.PI * 2.0f) % sliceTolerance;
		if (remainder != 0.0f){
			//subtract the remainder from sliceTolerance to find the perfect divisor
			maxAngleInRads = sliceTolerance - (sliceTolerance - remainder) / (float)Math.floor((float)Math.PI * 2.0f / sliceTolerance);
		
		//sliceTolerance was perfect divisor of 360
		} else {
			//can use sliceTolerance
			maxAngleInRads = sliceTolerance;
		}
		
		//the total number of slices we will make
		int numberSlices = (int)Math.floor((float)(Math.PI * 2.0f) / maxAngleInRads);
		float theta = maxAngleInRads;

		int nLevels = crossSection.getPoints().size();
		
		for(int i = 0; i < nLevels; i++){
			//pick out point on spline
			Vector2 splinePoint = crossSection.getPoints().get(i).clone();
			Vector2 normal = crossSection.getNormals().get(i);
			
			//plot that point in 3D space
			Vector3 XYZPoint = new Vector3(splinePoint.x, 0.0f, splinePoint.y);

			float zPos = splinePoint.y*scale;
			float r = splinePoint.x*scale;
			float rN = normal.x;

			for (int j=0; j<numberSlices; j++) {
				float xPos = r*((float)Math.cos(j*theta));
				float yPos = r*((float)Math.sin(j*theta));
				mesh.positions.add(new Vector3(xPos, yPos, zPos));

				float xNorm = rN*((float)Math.cos(j*theta));
				float yNorm = rN*((float)Math.sin(j*theta));;
				mesh.normals.add((new Vector3(xNorm, yNorm, normal.y)).normalize());
			}
		}
		OBJFace face;
		for(int i = 0; i < nLevels-1; i++){
			for (int j=0; j<numberSlices-1; j++) {
				face = new OBJFace(3, false, true);
				face.positions[0] = i*numberSlices + j;
				face.positions[2] = i*numberSlices + j+1;
				face.positions[1] = (i+1)*numberSlices + j;
				face.normals[0] = i*numberSlices + j;
				face.normals[2] = i*numberSlices + j+1;
				face.normals[1] = (i+1)*numberSlices + j;
				mesh.faces.add(face);

				face = new OBJFace(3, false, true);
				face.positions[0] = (i+1)*numberSlices + j;
				face.positions[2] = i*numberSlices + j+1;
				face.positions[1] = (i+1)*numberSlices + j+1;
				face.normals[0] = (i+1)*numberSlices + j;
				face.normals[2] = i*numberSlices + j+1;
				face.normals[1] = (i+1)*numberSlices + j+1;
				mesh.faces.add(face);
			}
			face = new OBJFace(3, false, true);
			face.positions[0] = i*numberSlices + (numberSlices-1);
			face.positions[2] = i*numberSlices;
			face.positions[1] = (i+1)*numberSlices + (numberSlices-1);
			face.normals[0] = i*numberSlices + (numberSlices-1);
			face.normals[2] = i*numberSlices;
			face.normals[1] = (i+1)*numberSlices + (numberSlices-1);
			mesh.faces.add(face);

			face = new OBJFace(3, false, true);
			face.positions[0] = (i+1)*numberSlices + (numberSlices-1);
			face.positions[2] = i*numberSlices;
			face.positions[1] = (i+1)*numberSlices;
			face.normals[0] = (i+1)*numberSlices + (numberSlices-1);
			face.normals[2] = i*numberSlices;
			face.normals[1] = (i+1)*numberSlices;
			mesh.faces.add(face);
		}
		if (crossSection.isClosed()) {
			for (int j=0; j<numberSlices-1; j++) {
				face = new OBJFace(3, false, true);
				face.positions[0] = (nLevels-1)*numberSlices + j;
				face.positions[2] = (nLevels-1)*numberSlices + j+1;
				face.positions[1] = j;
				face.normals[0] = (nLevels-1)*numberSlices + j;
				face.normals[2] = (nLevels-1)*numberSlices + j+1;
				face.normals[1] = j;
				mesh.faces.add(face);

				face = new OBJFace(3, false, true);
				face.positions[0] = j;
				face.positions[2] = (nLevels-1)*numberSlices + j+1;
				face.positions[1] = j+1;
				face.normals[0] = j;
				face.normals[2] = (nLevels-1)*numberSlices + j+1;
				face.normals[1] = j+1;
				mesh.faces.add(face);
			}
			face = new OBJFace(3, false, true);
			face.positions[0] = (nLevels-1)*numberSlices + (numberSlices-1);
			face.positions[2] = (nLevels-1)*numberSlices;
			face.positions[1] = numberSlices-1;
			face.normals[0] = (nLevels-1)*numberSlices + (numberSlices-1);
			face.normals[2] = (nLevels-1)*numberSlices;
			face.normals[1] = numberSlices-1;
			mesh.faces.add(face);

			face = new OBJFace(3, false, true);
			face.positions[0] = numberSlices-1;
			face.positions[2] = (nLevels-1)*numberSlices;
			face.positions[1] = 0;
			face.normals[0] = numberSlices-1;
			face.normals[2] = (nLevels-1)*numberSlices;
			face.normals[1] = 0;
			mesh.faces.add(face);
		}
	}
}
