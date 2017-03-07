package gl;

import common.SceneCamera;
import common.SceneObject;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector2d;

public class RenderCamera extends RenderObject {
	/**
	 * Reference to Scene counterpart of this camera
	 */
	public final SceneCamera sceneCamera;
	
	/**
	 * The view transformation matrix
	 */
	public final Matrix4 mView = new Matrix4();
	
	/**
	 * The projection matrix
	 */
	public final Matrix4 mProj = new Matrix4();
	
	/**
	 * The viewing/projection matrix (The product of the view and projection matrices)
	 */
	public final Matrix4 mViewProjection = new Matrix4();
	
	/**
	 * The size of the viewport, in pixels.
	 */
	public final Vector2 viewportSize = new Vector2();
	
	public RenderCamera(SceneObject o, Vector2 viewSize) {
		super(o);
		sceneCamera = (SceneCamera)o;
		viewportSize.set(viewSize);
	}

	/**
	 * Update the camera's viewing/projection matrix.
	 * 
	 * Update the camera's viewing/projection matrix in response to any changes in the camera's transformation
	 * or viewing parameters.  The viewing and projection matrices are computed separately and multiplied to 
	 * form the combined viewing/projection matrix.  When computing the projection matrix, the size of the view
	 * is adjusted to match the aspect ratio (the ratio of width to height) of the viewport, so that objects do 
	 * not appear distorted.  This is done by increasing either the height or the width of the camera's view,
	 * so that more of the scene is visible than with the original size, rather than less.
	 *  
	 * @param viewportSize
	 */
	public void updateCameraMatrix(Vector2 viewportSize) {
		this.viewportSize.set(viewportSize);
		// The camera's transformation matrix is found in this.mWorldTransform (inherited from RenderObject).
		// The other camera parameters are found in the scene camera (this.sceneCamera).
		// Look through the methods in Matrix4 before you type in any matrices from the book or the OpenGL specification.
		
		// TODO#A3#Part 2
		Vector2d viewSize = this.sceneCamera.imageSize;
		double w;
		double h;
		
		double ratioVS = viewSize.x / viewSize.y;
		double ratioVPS = viewportSize.x / viewportSize.y;
		
		if(ratioVS != ratioVPS){
			if (viewportSize.x > viewportSize.y){
				w = (viewportSize.x/viewportSize.y) * viewSize.x;
				h = viewSize.y;
			} else {
				w = viewSize.x;
				h = (viewportSize.y/viewportSize.x) * viewSize.y;
			} 
		} else {
			w = viewSize.x;
			h = viewSize.y;
		}

		System.out.println(viewportSize.x);
		System.out.println(viewportSize.y);
		System.out.println("");
		System.out.println(w);
		System.out.println(h);
		System.out.println("");
		System.out.println(viewSize.x);
		System.out.println(viewSize.y);
		System.out.println("");
		
		boolean perspective = this.sceneCamera.isPerspective;
		double nearPoint = this.sceneCamera.zPlanes.x;
		double farPoint = this.sceneCamera.zPlanes.y;
		Matrix4 camTransform = this.mWorldTransform.invert().clone();
		
		if (perspective){
			Matrix4 mPer;
			
			mPer = Matrix4.createPerspective((float)w, (float)h, (float)nearPoint, (float)farPoint);
			
			Matrix4 viewProj = mPer.clone().mulBefore(camTransform.clone());
			System.out.println(viewProj);
			System.out.println("");
			
			this.mViewProjection.set(viewProj.clone());
			
		} else {
			Matrix4 mOrth;
			
			mOrth = Matrix4.createOrthographic((float)w, (float)h, (float)nearPoint, (float)farPoint);
			
			Matrix4 viewProj = mOrth.clone().mulBefore(camTransform.clone());
			System.out.println(viewProj);
			System.out.println("");
			
			this.mViewProjection.set(viewProj.clone());
		}
		
	}	
}
