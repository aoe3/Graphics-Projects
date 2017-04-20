package anim;

import java.util.HashMap;
import common.Scene;
import common.SceneObject;
import common.event.SceneTransformationEvent;
import egl.math.Matrix4;
import egl.math.Vector3;
import egl.math.Matrix3;
import egl.math.Quat;

/**
 * A Component Resting Upon Scene That Gives
 * Animation Capabilities
 * @author Cristian
 *
 */
public class AnimationEngine {
	/**
	 *	Enum for the mode of rotation
	 */
	private enum RotationMode {
		EULER, QUAT_LERP, QUAT_SLERP;
	};
	
	/**
	 * The First Frame In The Global Timeline
	 */
	private int frameStart = 0;
	/**
	 * The Last Frame In The Global Timeline
	 */
	private int frameEnd = 100;
	/**
	 * The Current Frame In The Global Timeline
	 */
	private int curFrame = 0;
	/**
	 * Scene Reference
	 */
	private final Scene scene;
	/*
	 * Rotation Mode
	 */
	private RotationMode rotationMode = RotationMode.EULER;
	/**
	 * Animation Timelines That Map To Object Names
	 */
	public final HashMap<String, AnimTimeline> timelines = new HashMap<>();

	/**
	 * An Animation Engine That Works Only On A Certain Scene
	 * @param s The Working Scene
	 */
	public AnimationEngine(Scene s) {
		scene = s;
	}
	
	/**
	 * Set The First And Last Frame Of The Global Timeline
	 * @param start First Frame
	 * @param end Last Frame (Must Be Greater Than The First
	 */
	public void setTimelineBounds(int start, int end) {
		// Make Sure Our End Is Greater Than Our Start
		if(end < start) {
			int buf = end;
			end = start;
			start = buf;
		}
		
		frameStart = start;
		frameEnd = end;
		moveToFrame(curFrame);
	}
	/**
	 * Add An Animating Object
	 * @param oName Object Name
	 * @param o Object
	 */
	public void addObject(String oName, SceneObject o) {
		timelines.put(oName, new AnimTimeline(o));
	}
	/**
	 * Remove An Animating Object
	 * @param oName Object Name
	 */
	public void removeObject(String oName) {
		timelines.remove(oName);
	}

	/**
	 * Set The Frame Pointer To A Desired Frame (Will Be Bounded By The Global Timeline)
	 * @param f Desired Frame
	 */
	public void moveToFrame(int f) {
		if(f < frameStart) f = frameStart;
		else if(f > frameEnd) f = frameEnd;
		curFrame = f;
	}
	/**
	 * Looping Forwards Play
	 * @param n Number Of Frames To Move Forwards
	 */
	public void advance(int n) {
		curFrame += n;
		if(curFrame > frameEnd) curFrame = frameStart + (curFrame - frameEnd - 1);
	}
	/**
	 * Looping Backwards Play
	 * @param n Number Of Frames To Move Backwards
	 */
	public void rewind(int n) {
		curFrame -= n;
		if(curFrame < frameStart) curFrame = frameEnd - (frameStart - curFrame - 1);
	}

	public int getCurrentFrame() {
		return curFrame;
	}
	public int getFirstFrame() {
		return frameStart;
	}
	public int getLastFrame() {
		return frameEnd;
	}
	public int getNumFrames() {
		return frameEnd - frameStart + 1;
	}

	/**
	 * Adds A Keyframe For An Object At The Current Frame
	 * Using The Object's Transformation - (CONVENIENCE METHOD)
	 * @param oName Object Name
	 */
	public void addKeyframe(String oName) {
		AnimTimeline tl = timelines.get(oName);
		if(tl == null) return;
		tl.addKeyFrame(getCurrentFrame(), tl.object.transformation);
	}
	/**
	 * Removes A Keyframe For An Object At The Current Frame
	 * Using The Object's Transformation - (CONVENIENCE METHOD)
	 * @param oName Object Name
	 */
	public void removeKeyframe(String oName) {
		AnimTimeline tl = timelines.get(oName);
		if(tl == null) return;
		tl.removeKeyFrame(getCurrentFrame(), tl.object.transformation);
	}
	
	/**
	 * Toggles rotation mode that will be applied to all animated objects.
	 */
	public void toggleRotationMode() {
		switch(this.rotationMode) {
		case EULER:
			this.rotationMode = RotationMode.QUAT_LERP;
			break;
		case QUAT_LERP:
			this.rotationMode = RotationMode.QUAT_SLERP;
			break;
		case QUAT_SLERP:
			this.rotationMode = RotationMode.EULER;
			break;
		default:
			break;
		}
		System.out.println("Now in rotation mode " + this.rotationMode.name());
	}
	
	/**
	 * Loops Through All The Animating Objects And Updates Their Transformations To
	 * The Current Frame - For Each Updated Transformation, An Event Has To Be 
	 * Sent Through The Scene Notifying Everyone Of The Change
	 */
	
	public void updateTransformations() {
		// Loop Through All The Timelines
		// And Update Transformations Accordingly
		// (You WILL Need To Use this.scene)
		for (HashMap.Entry<String, AnimTimeline> entry : this.timelines.entrySet()){
			AnimTimeline val = entry.getValue();
			SceneObject currObj = val.object;

			// get pair of surrounding frames
			// (function in AnimTimeline)
			AnimKeyframe[] pair = new AnimKeyframe[2];
			val.getSurroundingFrames(curFrame, pair);
			System.out.println(pair[0].frame);
			System.out.println(pair[1].frame);


			Matrix4 lastObjTransform = pair[0].transformation.clone();
			Matrix4 nextObjTransform = pair[1].transformation.clone();

			// get interpolation ratio
			float t = getRatio(pair[0].frame, pair[1].frame, curFrame);
			if (t==0) { t=1; };
			
			//create translation matrix
			Vector3 lastTranslation = new Vector3(lastObjTransform.getTrans());
			Vector3 nextTranslation = new Vector3(nextObjTransform.getTrans());

			// interpolate translations linearly
			Vector3 translationVector = lastTranslation;
			translationVector.lerp(nextTranslation, t);
			Matrix4 translationMatrix = Matrix4.createTranslation(translationVector);
//			System.out.println("T" + t);

			Quat q1 = new Quat();
			Quat q2 = new Quat();
			
			q1.set(pair[0].transformation);
			q2.set(pair[1].transformation);

			// polar decompose axis matrices
			
			//get RS out of the transformation matrix
			Matrix3 lastMatrixRS = lastObjTransform.getAxes();
			Matrix3 nextMatrixRS = nextObjTransform.getAxes();

			//create empty matrixes for decomp into R and S
			Matrix3 lastRotationMatrix = new Matrix3();
			Matrix3 nextRotationMatrix = new Matrix3();
			Matrix3 lastSymmetricMatrix = new Matrix3();
			Matrix3 nextSymmetricMatrix = new Matrix3();

			lastMatrixRS.polar_decomp(lastRotationMatrix, lastSymmetricMatrix);
			nextMatrixRS.polar_decomp(nextRotationMatrix, nextSymmetricMatrix);

			// interpolate scale linearly
			Matrix3 s = lastSymmetricMatrix.clone();
			s.interpolate(s, nextSymmetricMatrix, t);

			Matrix3 r = new Matrix3();
			Quat q;
			// interpolate rotation matrix (3 modes of interpolation) and linearly interpolate scales
			switch(this.rotationMode) {
				case EULER:
					//decomp RS into R and S
					Vector3 lastEulerAngles = eulerDecomp(lastRotationMatrix);
					Vector3 nextEulerAngles = eulerDecomp(nextRotationMatrix);

					Vector3 v = lastEulerAngles.clone();
					v.lerp(nextEulerAngles, t);

					Matrix3 rx = new Matrix3();
					Matrix3 ry = new Matrix3();
					Matrix3 rz = new Matrix3();

					Matrix3.createRotationX(v.x, rx);
					Matrix3.createRotationY(v.y, ry);
					Matrix3.createRotationZ(v.z, rz);

					rx.mulBefore(ry.mulBefore(rz), r);
					break;

				case QUAT_LERP:
					// interpolate rotation linearly
					q = q1.scale(1-t).add(q2.scale(t));
					q.normalize();
					r = q.toRotationMatrix(r);
					break;

				case QUAT_SLERP:
					q = Quat.slerp(q1, q2, t);
					q.normalize();
					r = q.toRotationMatrix(r);
					break;
				default:
					break;
			}
	
			// combine interpolated R,S,and T
			Matrix3 rs = new Matrix3();
			r.mulBefore(s, rs);
			Matrix4 rs4 = new Matrix4(rs);
			Matrix4 m = new Matrix4();
			translationMatrix.mulBefore(rs4, m);

			currObj.transformation.set(m);
			this.scene.sendEvent(new SceneTransformationEvent(currObj));
		}
	}

	public static float getRatio(int min, int max, int cur) {
		if(min == max) return 0f;
		float total = max - min;
		float diff = cur - min;
		return diff / total;
	}
	
	/**
	 * Takes a rotation matrix and decomposes into Euler angles. 
	 * Returns a Vector3 containing the X, Y, and Z degrees in radians.
	 * Formulas from http://nghiaho.com/?page_id=846
	 */
	public static Vector3 eulerDecomp(Matrix3 mat) {
		double theta_x = Math.atan2(mat.get(2, 1), mat.get(2, 2));
		double theta_y = Math.atan2(-mat.get(2, 0), Math.sqrt(Math.pow(mat.get(2, 1), 2) + Math.pow(mat.get(2, 2), 2)));
		double theta_z = Math.atan2(mat.get(1, 0), mat.get(0, 0));
		
		return new Vector3((float)theta_x, (float)theta_y, (float)theta_z);
	}
}

