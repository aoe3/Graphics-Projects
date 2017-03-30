package splines;

import java.util.ArrayList;

import egl.math.Vector2;
/*
 * Cubic Bezier class for the splines assignment
 */

public class CubicBezier {
	
	//This Bezier's control points
	public Vector2 p0, p1, p2, p3;
	
	//Control parameter for curve smoothness
	float epsilon;
	
	//The points on the curve represented by this Bezier
	private ArrayList<Vector2> curvePoints;
	
	//The normals associated with curvePoints
	private ArrayList<Vector2> curveNormals;
	
	//The tangent vectors of this bezier
	private ArrayList<Vector2> curveTangents;
	
	
	/**
	 * 
	 * Cubic Bezier Constructor
	 * 
	 * Given 2-D BSpline Control Points correctly set self.{p0, p1, p2, p3},
	 * self.uVals, self.curvePoints, and self.curveNormals
	 * 
	 * @param bs0 First Bezier Spline Control Point
	 * @param bs1 Second Bezier Spline Control Point
	 * @param bs2 Third Bezier Spline Control Point
	 * @param bs3 Fourth Bezier Spline Control Point
	 * @param eps Maximum angle between line segments
	 */
	public CubicBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, float eps) {
		curvePoints = new ArrayList<Vector2>();
		curveTangents = new ArrayList<Vector2>();
		curveNormals = new ArrayList<Vector2>();
		epsilon = eps;
		
		this.p0 = new Vector2(p0);
		this.p1 = new Vector2(p1);
		this.p2 = new Vector2(p2);
		this.p3 = new Vector2(p3);
		
		tessellate();
	}

    /**
     * Approximate a Bezier segment with a number of vertices, according to an appropriate
     * smoothness criterion for how many are needed.  The points on the curve are written into the
     * array self.curvePoints, the tangents into self.curveTangents, and the normals into self.curveNormals.
     * The final point, p3, is not included, because cubic Beziers will be "strung together".
     */
    private void tessellate() {
    	 // TODO A5
		Vector2[] points = {p0,p1,p2,p3};
		tessellateRec(points, 0);
		for (int i=0; i<curvePoints.size(); i++) {
			System.out.println(curvePoints.get(i));
		}
	}

	private void tessellateRec(Vector2[] points, int recLv) {
//    	curvePoints.add(points[0]);
		Vector2[] currentLv = points;
		Vector2[] lPoints = new Vector2[4];
		Vector2[] rPoints = new Vector2[4];
		for (int i=0; i<3; i++) {
			Vector2[] nextLv = new Vector2[currentLv.length - 1];
			for (int j=0; j<currentLv.length-1; j++) {
				nextLv[j] = (currentLv[j].clone().mul(.5f)).add(currentLv[j+1].clone().mul(.5f));
			}
			lPoints[i] = currentLv[0];
			rPoints[3-i] = currentLv[3-i];
			currentLv = nextLv;
		}
		lPoints[3] = currentLv[0];
		rPoints[0] = currentLv[0];

		curvePoints.add(lPoints[0]);
		curvePoints.add(rPoints[0]);

		Vector2 a = lPoints[1].clone().sub(lPoints[0]);
		Vector2 b = lPoints[2].clone().sub(lPoints[1]);
		float dotProd = a.clone().dot(b);
		float theta = (float)Math.acos((double)(dotProd / a.clone().len() / b.clone().len()));

		if (theta > epsilon / 2f && recLv < 9) {
			tessellateRec(lPoints, recLv+1);
			tessellateRec(rPoints, recLv+1);
		}
	}
	
    
    /**
     * @return The points on this cubic bezier
     */
    public ArrayList<Vector2> getPoints() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curvePoints) returnList.add(p.clone());
    	return returnList;
    }
    
    /**
     * @return The tangents on this cubic bezier
     */
    public ArrayList<Vector2> getTangents() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveTangents) returnList.add(p.clone());
    	return returnList;
    }
    
    /**
     * @return The normals on this cubic bezier
     */
    public ArrayList<Vector2> getNormals() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveNormals) returnList.add(p.clone());
    	return returnList;
    }
    
    
    /**
     * @return The references to points on this cubic bezier
     */
    public ArrayList<Vector2> getPointReferences() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curvePoints) returnList.add(p);
    	return returnList;
    }
    
    /**
     * @return The references to tangents on this cubic bezier
     */
    public ArrayList<Vector2> getTangentReferences() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveTangents) returnList.add(p);
    	return returnList;
    }
    
    /**
     * @return The references to normals on this cubic bezier
     */
    public ArrayList<Vector2> getNormalReferences() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveNormals) returnList.add(p);
    	return returnList;
    }

    public static void main(String[] args) {
		CubicBezier cb = new CubicBezier(new Vector2(0,0), new Vector2(0,1), new Vector2(1,1), new Vector2(1,0), 1);
	}
}
