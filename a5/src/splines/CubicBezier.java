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
		Vector2[] points = {this.p0.clone(),this.p1.clone(),this.p2.clone(),this.p3.clone()};
		tessellateRec(points, 0);
		curvePoints.add(this.p0.clone());
		Vector2 diff = (this.p0.clone().sub(this.p1.clone())).mul(3.0f).negate().normalize();
		curveTangents.add(diff);
		findNormals(this.p0, this.p0, this.p1, curveTangents.get(curveTangents.size()-1));
		for (int i=0; i<curvePoints.size(); i++) {
//			System.out.println("Pt :" + "(" +curvePoints.get(i).x +"," + curvePoints.get(i).y +")");
//			System.out.println("Tan: " + "(" +curveTangents.get(i).x +"," + curveTangents.get(i).y +")");
//			System.out.println("Norm: " + "(" +curveNormals.get(i).x +"," + curveNormals.get(i).y +")");
//			System.out.println();
		}
	}

	private void tessellateRec(Vector2[] points, int recLv) {
//    	curvePoints.add(points[0]);
		
		//set current level of points
		Vector2[] currentLv = points;
		
		//set dummy arrays to hold the left and right points
		Vector2[] lPoints = new Vector2[4];
		Vector2[] rPoints = new Vector2[4];
		
		//for each point in the current level 
		for (int i=0; i<3; i++) {
			
			//set dummy array for the next level
			Vector2[] nextLv = new Vector2[currentLv.length - 1];
			
			//iterate through points at this level
			for (int j=0; j<currentLv.length-1; j++) {
				//apply weights, 'u' and '1-u' to two adjacent points and add the result to the next level
				nextLv[j] = (currentLv[j].clone().mul(.5f)).add(currentLv[j+1].clone().mul(.5f));
			}
			
			//assign a point to the left curve
			lPoints[i] = currentLv[0];
			
			//assign a point to the right curve
			rPoints[3-i] = currentLv[3-i];
			
			//go down a recursion depth
			currentLv = nextLv;
		}
		//connect L and R at a point
		lPoints[3] = currentLv[0];
		rPoints[0] = currentLv[0];
		
		//Find distances between control points on left curve
		Vector2 a = lPoints[1].clone().sub(lPoints[0].clone());
		Vector2 b = lPoints[2].clone().sub(lPoints[1].clone());
		Vector2 c = lPoints[3].clone().sub(lPoints[2].clone());
		
		// find dot products of adjacent vectors
		float dotProd1 = a.clone().dot(b.clone());
		float dotProd2 = b.clone().dot(c.clone());
		
		//create angles to compare with epsilon
		float theta1 = (float)Math.acos((double)(dotProd1 / a.clone().len() / b.clone().len()));
		float theta2 = (float)Math.acos((double)(dotProd2 / b.clone().len() / c.clone().len()));

		//compare angles against epsilon for R
		if ((theta1 > epsilon / 2f || theta2 > epsilon / 2f) && recLv < 9) {
			tessellateRec(rPoints, recLv+1);
		}
		
		//add points and tangents for the first R point/last L point
		curvePoints.add(lPoints[3]);
		Vector2 dist = (lPoints[3].clone().sub(lPoints[2].clone())).mul(3.0f).normalize();
		curveTangents.add(dist);
		
		//get last added point and tangent
		Vector2 currentPt = curvePoints.get(curvePoints.size()-1);
		Vector2 currentTan = curveTangents.get(curveTangents.size()-1);
		
		//find normals
		findNormals(currentPt, lPoints[2], lPoints[3], currentTan);
		
		//compare angles against epsilon for L
		if ((theta1 > epsilon / 2f || theta2 > epsilon / 2f) && recLv < 9) {
			tessellateRec(lPoints, recLv+1);
		}
	}
	
	/**Tangent is in direction of controlPt2, FROM controlPt1*/
	private void findNormals(Vector2 point, Vector2 controlPt1, Vector2 controlPt2, Vector2 tangent){
		//find distance from point to tangent, need abs() because can't have negative distance
		Vector2 toTangent = point.clone().add(tangent.clone());
		Vector2 dist = (point.clone().sub(toTangent)).abs();
		//bool for where tangent is a vertical line, default = FALSE
		boolean StraightVerticalLine;
		
		if (dist.x == 0.0f){
			StraightVerticalLine = true;
		} else {
			StraightVerticalLine = false;
		}

		Vector2 norm;
		if(StraightVerticalLine){
			//handle case where tangent is a vertical line
			//look at control pts to find out if we need to add or subtract from vector
			if(controlPt1.y > controlPt2.y){ //pt1 higher than pt2
				norm = new Vector2(-1.0f, 0.0f);
			} else { //pt1 lower than pt 2
				norm = new Vector2(1.0f, 0.0f);
			}
		} else {
			//tangent was a horizontal line
			if(dist.y== 0.0f){
				//look at control pts to find out if we need to add or subtract from vector
				if(controlPt1.x > controlPt2.x){ //pt2 to the left of point 1
					norm = new Vector2(0.0f, -1.0f);
				} else { //pt2 to the right of point 1
					norm = new Vector2(0.0f, 1.0f);
				}
			//tangent was not a horizontal line or vertical line
			} else {
					norm = new Vector2((toTangent.y-point.y),-(toTangent.x-point.x));
				

/*NONE OF THIS SHIT BELOW WORKED **/
//				//find slope of tan
//				float slope = (dist.y/dist.x) / 1.0f;
//				
//				//invert slope of tan
//				float slopeinverted = (1.0f / slope);
//				
//				//negate inverted slope
//				float slopeinvnegate = -1.0f * slopeinverted;
//				
//				//now have the normal's slope
//				if(controlPt1.y > controlPt2.y){
//					if(controlPt1.x > controlPt2.x){ //pt1 high and right of pt2
//						norm = new Vector2(1.0f, point.clone().y - slopeinvnegate);
//					} else { //pt1 high and left of pt2
//						norm = new Vector2(-1.0f, point.clone().y - slopeinvnegate);
//					}
//				} else {
//					if(controlPt1.x > controlPt2.x){ //pt1 low and right of pt2
//						norm = new Vector2(1.0f, point.clone().y + slopeinvnegate);
//					} else { //pt1 low and left of pt2
//						norm = new Vector2(-1.0f, point.clone().y + slopeinvnegate);
//					}
//				}
/*END OF THE SHIT THAT DIDN'T WORK**/
			}
		}

		curveNormals.add(norm.normalize());
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
		CubicBezier cb = new CubicBezier(new Vector2(0,0), new Vector2(0,1), new Vector2(-1,1), new Vector2(-1,0), .25f);
	}
}
