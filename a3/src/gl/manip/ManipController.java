package gl.manip;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import egl.math.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import blister.input.KeyboardEventDispatcher;
import blister.input.KeyboardKeyEventArgs;
import blister.input.MouseButton;
import blister.input.MouseButtonEventArgs;
import blister.input.MouseEventDispatcher;
import common.Scene;
import common.SceneObject;
import common.UUIDGenerator;
import common.event.SceneTransformationEvent;
import gl.PickingProgram;
import gl.RenderCamera;
import gl.RenderEnvironment;
import gl.RenderObject;
import gl.Renderer;
import form.ControlWindow;
import form.ScenePanel;
import egl.BlendState;
import egl.DepthState;
import egl.IDisposable;
import egl.RasterizerState;
import ext.csharp.ACEventFunc;
import org.lwjgl.util.vector.Matrix;

public class ManipController implements IDisposable {
	public final ManipRenderer renderer = new ManipRenderer();
	public final HashMap<Manipulator, UUIDGenerator.ID> manipIDs = new HashMap<>();
	public final HashMap<Integer, Manipulator> manips = new HashMap<>();
	
	private final Scene scene;
	private final ControlWindow propWindow;
	private final ScenePanel scenePanel;
	private final RenderEnvironment rEnv;
	private ManipRenderer manipRenderer = new ManipRenderer();
	
	private final Manipulator[] currentManips = new Manipulator[3];
	private RenderObject currentObject = null;
	
	private Manipulator selectedManipulator = null;
	
	/**
	 * Is parent mode on?  That is, should manipulation happen in parent rather than object coordinates?
	 */
	private boolean parentSpace = false;
	
	/**
	 * Last seen mouse position in normalized coordinates
	 */
	private final Vector2 lastMousePos = new Vector2();
	
	public ACEventFunc<KeyboardKeyEventArgs> onKeyPress = new ACEventFunc<KeyboardKeyEventArgs>() {
		@Override
		public void receive(Object sender, KeyboardKeyEventArgs args) {
			if(selectedManipulator != null) return;
			switch (args.key) {
			case Keyboard.KEY_T:
				setCurrentManipType(Manipulator.Type.TRANSLATE);
				break;
			case Keyboard.KEY_R:
				setCurrentManipType(Manipulator.Type.ROTATE);
				break;
			case Keyboard.KEY_Y:
				setCurrentManipType(Manipulator.Type.SCALE);
				break;
			case Keyboard.KEY_P:
				parentSpace = !parentSpace;
				break;
			}
		}
	};
	public ACEventFunc<MouseButtonEventArgs> onMouseRelease = new ACEventFunc<MouseButtonEventArgs>() {
		@Override
		public void receive(Object sender, MouseButtonEventArgs args) {
			if(args.button == MouseButton.Right) {
				selectedManipulator = null;
			}
		}
	};
	
	public ManipController(RenderEnvironment re, Scene s, ControlWindow cw) {
		scene = s;
		propWindow = cw;
		Component o = cw.tabs.get("Object");
		scenePanel = o == null ? null : (ScenePanel)o;
		rEnv = re;
		
		// Give Manipulators Unique IDs
		manipIDs.put(Manipulator.ScaleX, scene.objects.getID("ScaleX"));
		manipIDs.put(Manipulator.ScaleY, scene.objects.getID("ScaleY"));
		manipIDs.put(Manipulator.ScaleZ, scene.objects.getID("ScaleZ"));
		manipIDs.put(Manipulator.RotateX, scene.objects.getID("RotateX"));
		manipIDs.put(Manipulator.RotateY, scene.objects.getID("RotateY"));
		manipIDs.put(Manipulator.RotateZ, scene.objects.getID("RotateZ"));
		manipIDs.put(Manipulator.TranslateX, scene.objects.getID("TranslateX"));
		manipIDs.put(Manipulator.TranslateY, scene.objects.getID("TranslateY"));
		manipIDs.put(Manipulator.TranslateZ, scene.objects.getID("TranslateZ"));
		for(Entry<Manipulator, UUIDGenerator.ID> e : manipIDs.entrySet()) {
			manips.put(e.getValue().id, e.getKey());
		}
		
		setCurrentManipType(Manipulator.Type.TRANSLATE);
	}
	@Override
	public void dispose() {
		manipRenderer.dispose();
		unhook();
	}
	
	private void setCurrentManipType(int type) {
		switch (type) {
		case Manipulator.Type.TRANSLATE:
			currentManips[Manipulator.Axis.X] = Manipulator.TranslateX;
			currentManips[Manipulator.Axis.Y] = Manipulator.TranslateY;
			currentManips[Manipulator.Axis.Z] = Manipulator.TranslateZ;
			break;
		case Manipulator.Type.ROTATE:
			currentManips[Manipulator.Axis.X] = Manipulator.RotateX;
			currentManips[Manipulator.Axis.Y] = Manipulator.RotateY;
			currentManips[Manipulator.Axis.Z] = Manipulator.RotateZ;
			break;
		case Manipulator.Type.SCALE:
			currentManips[Manipulator.Axis.X] = Manipulator.ScaleX;
			currentManips[Manipulator.Axis.Y] = Manipulator.ScaleY;
			currentManips[Manipulator.Axis.Z] = Manipulator.ScaleZ;
			break;
		}
	}
	
	public void hook() {
		KeyboardEventDispatcher.OnKeyPressed.add(onKeyPress);
		MouseEventDispatcher.OnMouseRelease.add(onMouseRelease);
	}
	public void unhook() {
		KeyboardEventDispatcher.OnKeyPressed.remove(onKeyPress);		
		MouseEventDispatcher.OnMouseRelease.remove(onMouseRelease);
	}
	
	/**
	 * Get the transformation that should be used to draw <manip> when it is being used to manipulate <object>.
	 * 
	 * This is just the object's or parent's frame-to-world transformation, but with a rotation appended on to 
	 * orient the manipulator along the correct axis.  One problem with the way this is currently done is that
	 * the manipulator can appear very small or large, or very squashed, so that it is hard to interact with.
	 * 
	 * @param manip The manipulator to be drawn (one axis of the complete widget)
	 * @param mViewProjection The camera (not needed for the current, simple implementation)
	 * @param object The selected object
	 * @return
	 */
	public Matrix4 getTransformation(Manipulator manip, RenderCamera camera, RenderObject object) {
		Matrix4 mManip = new Matrix4();
		
		switch (manip.axis) {
		case Manipulator.Axis.X:
			Matrix4.createRotationY((float)(Math.PI / 2.0), mManip);
			break;
		case Manipulator.Axis.Y:
			Matrix4.createRotationX((float)(-Math.PI / 2.0), mManip);
			break;
		case Manipulator.Axis.Z:
			mManip.setIdentity();
			break;
		}
		if (parentSpace) {
			if (object.parent != null)
				mManip.mulAfter(object.parent.mWorldTransform);
		} else
			mManip.mulAfter(object.mWorldTransform);

		return mManip;
	}
	
	/**
	 * Apply a transformation to <b>object</b> in response to an interaction with <b>manip</b> in which the user moved the mouse from
 	 * <b>lastMousePos</b> to <b>curMousePos</b> while viewing the scene through <b>camera</b>.  The manipulation happens differently depending
 	 * on the value of ManipController.parentMode; if it is true, the manipulator is aligned with the parent's coordinate system, 
 	 * or if it is false, with the object's local coordinate system.  
	 * @param manip The manipulator that is active (one axis of the complete widget)
	 * @param camera The camera (needed to map mouse motions into the scene)
	 * @param object The selected object (contains the transformation to be edited)
	 * @param lastMousePos The point where the mouse was last seen, in normalized [-1,1] x [-1,1] coordinates.
	 * @param curMousePos The point where the mouse is now, in normalized [-1,1] x [-1,1] coordinates.
	 */
	public void applyTransformation(Manipulator manip, RenderCamera camera, RenderObject object, Vector2 lastMousePos, Vector2 curMousePos) {

		// There are three kinds of manipulators; you can tell which kind you are dealing with by looking at manip.type.
		// Each type has three different axes; you can tell which you are dealing with by looking at manip.axis.

		// For rotation, you just need to apply a rotation in the correct space (either before or after the object's current
		// transformation, depending on the parent mode this.parentSpace).

		// For translation and scaling, the object should follow the mouse.  Following the assignment writeup, you will achieve
		// this by constructing the viewing rays and the axis in world space, and finding the t values *along the axis* where the
		// ray comes closest (not t values along the ray as in ray tracing).  To do this you need to transform the manipulator axis
		// from its frame (in which the coordinates are simple) to world space, and you need to get a viewing ray in world coordinates.

		// There are many ways to compute a viewing ray, but perhaps the simplest is to take a pair of points that are on the ray,
		// whose coordinates are simple in the canonical view space, and map them into world space using the appropriate matrix operations.
		
		// You may find it helpful to structure your code into a few helper functions; ours is about 150 lines.	
	
		
		// TODO#A3#Part 4
		// Axis/Type to Number Relationships
		
		//    Axis           Type
		// 0 / 1 / 2      0 / 1 / 2
		// X / Y / Z      s / r / t

		//In words:
		//0 axis is X
		//1 axis is Y
		//2 axis is Z
		
		//0 type is scale
		//1 type is rotation
		//2 type is translation

		Matrix4 canToCam = camera.mProj.clone().invert();
		Matrix4 camToWorld = camera.mView.clone().invert();
		Matrix4 worldToObj = object.mWorldTransform.clone().invert();
		Matrix4 objToWorld = object.mWorldTransform;
		Matrix4 worldToCam = camera.mView;

		Vector4 manipDir4;		// Direction ray in manipulator frame

		// X-AXIS
		if(manip.axis == 0){
			manipDir4 = new Vector4(1,0,0,1);
			
		// Y-AXIS
		} else if (manip.axis == 1){
			manipDir4 = new Vector4(0,1,0,1);
			
		// Z-AXIS
		} else {
			manipDir4 = new Vector4(0,0,1,1);
		}

		// convert manipulator origin to camera space
		Vector4 origin4 = objToWorld.clone().mulAfter(worldToCam).mul(new Vector4(0,0,0,1)).homogenize();		// manipulator origin in world coordinates
		Vector3 origin = new Vector3(origin4.x, origin4.y, origin4.z);

		// convert manipulator direction vector into camera space
		manipDir4 = objToWorld.clone().mulAfter(worldToCam).mul(manipDir4).homogenize();
		Vector3 manipDir = new Vector3(manipDir4.x, manipDir4.y, manipDir4.z);
		manipDir.sub(origin).normalize();

		// camera points directly in -z direction in camera space
		Vector3 imgPlaneN = new Vector3(0,0,-1);
		Vector3 imgPlaneP = imgPlaneN.clone().cross(manipDir).normalize();

		// find normal vector of manipulator plane
		Vector3 manipN = imgPlaneP.clone().cross(manipDir).normalize();

		// convert mouse positions to camera space (from canonical)
		Vector4 lastMouseCam4 = new Vector4(lastMousePos.x, lastMousePos.y, 1, 1);
		Vector4 currMouseCam4 = new Vector4(curMousePos.x, curMousePos.y, 1, 1);
		lastMouseCam4 = canToCam.clone().mul(lastMouseCam4).normalize().homogenize();
		currMouseCam4 = canToCam.clone().mul(currMouseCam4).normalize().homogenize();
		Vector3 lastMouseCam = new Vector3(lastMouseCam4.x, lastMouseCam4.y, lastMouseCam4.z);
		Vector3 currMouseCam = new Vector3(currMouseCam4.x, currMouseCam4.y, currMouseCam4.z);

		// calculate position of mouse on manipulator plane
		lastMouseCam = rayPlaneIntersection(manipN, origin, lastMouseCam);
		currMouseCam = rayPlaneIntersection(manipN, origin, currMouseCam);

		// Find closest point to mouse along manipulator direction ray
		Vector3 ptLastCam = closestPt(origin, manipDir, lastMouseCam);
		Vector3 ptCurrCam = closestPt(origin, manipDir, currMouseCam);

		// convert closest point to manipulator (object) space
		Vector4 ptLast = new Vector4(ptLastCam.x, ptLastCam.y, ptLastCam.z, 1f);
		Vector4 ptCurr = new Vector4(ptCurrCam.x, ptCurrCam.y, ptCurrCam.z, 1f);
		ptLast = camToWorld.clone().mulAfter(worldToObj).mul(ptLast).homogenize();
		ptCurr = camToWorld.clone().mulAfter(worldToObj).mul(ptCurr).homogenize();
		Vector3 ptLast3 = new Vector3(ptLast.x, ptLast.y, ptLast.z);
		Vector3 ptCurr3 = new Vector3(ptCurr.x, ptCurr.y, ptCurr.z);

		// apply specific transformations in manipulator (object) space
		if (manip.type == 0) {			// scale
			applyScale(manip, object, ptLast3, ptCurr3);
		} else if (manip.type == 1) {	// rotation
			applyRotation(manip, object, lastMousePos, curMousePos);
		} else {						// translation
			applyTranslation(manip, object, ptLast3, ptCurr3);
		}
	}

	public void applyScale(Manipulator manip, RenderObject object, Vector3 ptLast, Vector3 ptCurr) {
		float ratio;
		Matrix4 T = new Matrix4();
		if (manip.axis == 0) {			// x-axis
			ratio = ptCurr.x / ptLast.x;
			Matrix4.createScale(ratio, 1, 1, T);
		} else if (manip.axis == 1) {	// y-axis
			ratio = ptCurr.y / ptLast.y;
			Matrix4.createScale(1, ratio, 1, T);
		} else {						// z-axis
			ratio = ptCurr.z / ptLast.z;
			Matrix4.createScale(1, 1, ratio, T);
		}

		if (this.parentSpace) {
			object.sceneObject.transformation.mulAfter(T);
		} else {
			object.sceneObject.transformation.mulBefore(T);
		}
	}

	public void applyRotation(Manipulator manip, RenderObject object, Vector2 ptLast, Vector2 ptCurr) {
		float dy = ptCurr.y-ptLast.y;
		float theta = dy*2f*(float)Math.PI;

		Matrix4 T = new Matrix4();
		if (manip.axis == 0) {			// x-axis
			Matrix4.createRotationX(theta, T);
		} else if (manip.axis == 1) {	// y-axis
			Matrix4.createRotationY(theta, T);
		} else {						// z-axis
			Matrix4.createRotationZ(theta, T);
		}

		if (this.parentSpace) {
			object.sceneObject.transformation.mulAfter(T);
		} else {
			object.sceneObject.transformation.mulBefore(T);
		}
	}

	public void applyTranslation(Manipulator manip, RenderObject object, Vector3 ptLast, Vector3 ptCurr) {
		float dist;
		Matrix4 T = new Matrix4();
		if (manip.axis == 0) {			// x-axis
			dist = ptCurr.x-ptLast.x;
			Matrix4.createTranslation(dist, 0, 0, T);
		} else if (manip.axis == 1) {	// y-axis
			dist = ptCurr.y-ptLast.y;
			Matrix4.createTranslation(0, dist, 0, T);
		} else {						// z-axis
			dist = ptCurr.z-ptLast.z;
			Matrix4.createTranslation(0, 0, dist, T);
		}

		if (Float.isNaN(dist)) {
			T = new Matrix4();
		}

		if (this.parentSpace) {
			object.sceneObject.transformation.mulAfter(T);
		} else {
			object.sceneObject.transformation.mulBefore(T);
		}
	}

	public Vector3 rayPlaneIntersection(Vector3 normal, Vector3 origin, Vector3 ray) {
		float d = normal.x*origin.x + normal.y*origin.y + normal.z*origin.z;
		float abc = normal.x*ray.x + normal.y*ray.y + normal.z*ray.z;
		float t = d/abc;
		ray.mul(t);
		return ray;
	}

	public Vector3 closestPt(Vector3 origin, Vector3 dir, Vector3 mousePt) {
		float oDotD = origin.clone().dot(dir);
		float mDotD = mousePt.clone().dot(dir);
		float comp1 = -(oDotD - mDotD);
		float tCoeff = dir.clone().dot(dir);
		float t = comp1/tCoeff;
		return origin.clone().add(dir.clone().mul(t));
	}
	
	public void checkMouse(int mx, int my, RenderCamera camera) {
		Vector2 curMousePos = new Vector2(mx, my).add(0.5f).mul(2).div(camera.viewportSize.x, camera.viewportSize.y).sub(1);
		if(curMousePos.x != lastMousePos.x || curMousePos.y != lastMousePos.y) {
			if(selectedManipulator != null && currentObject != null) {
				applyTransformation(selectedManipulator, camera, currentObject, lastMousePos, curMousePos);
				scene.sendEvent(new SceneTransformationEvent(currentObject.sceneObject));
			}
			lastMousePos.set(curMousePos);
		}
	}

	public void checkPicking(Renderer renderer, RenderCamera camera, int mx, int my) {
		if(camera == null) return;
		
		// Pick An Object
		renderer.beginPickingPass(camera);
		renderer.drawPassesPick();
		if(currentObject != null) {
			// Draw Object Manipulators
			GL11.glClearDepth(1.0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			
			DepthState.DEFAULT.set();
			BlendState.OPAQUE.set();
			RasterizerState.CULL_NONE.set();
			
			drawPick(camera, currentObject, renderer.pickProgram);
		}
		int id = renderer.getPickID(Mouse.getX(), Mouse.getY());
		
		selectedManipulator = manips.get(id);
		if(selectedManipulator != null) {
			// Begin Manipulator Operations
			System.out.println("Selected Manip: " + selectedManipulator.type + " " + selectedManipulator.axis);
			return;
		}
		
		SceneObject o = scene.objects.get(id);
		if(o != null) {
			System.out.println("Picked An Object: " + o.getID().name);
			if(scenePanel != null) {
				scenePanel.select(o.getID().name);
				propWindow.tabToForefront("Object");
			}
			currentObject = rEnv.findObject(o);
		}
		else if(currentObject != null) {
			currentObject = null;
		}
	}
	
	public RenderObject getCurrentObject() {
		return currentObject;
	}
	
	public void draw(RenderCamera camera) {
		if(currentObject == null) return;
		
		DepthState.NONE.set();
		BlendState.ALPHA_BLEND.set();
		RasterizerState.CULL_CLOCKWISE.set();
		
		for(Manipulator manip : currentManips) {
			Matrix4 mTransform = getTransformation(manip, camera, currentObject);
			manipRenderer.render(mTransform, camera.mViewProjection, manip.type, manip.axis);
		}
		
		DepthState.DEFAULT.set();
		BlendState.OPAQUE.set();
		RasterizerState.CULL_CLOCKWISE.set();
		
		for(Manipulator manip : currentManips) {
			Matrix4 mTransform = getTransformation(manip, camera, currentObject);
			manipRenderer.render(mTransform, camera.mViewProjection, manip.type, manip.axis);
		}

}
	public void drawPick(RenderCamera camera, RenderObject ro, PickingProgram prog) {
		for(Manipulator manip : currentManips) {
			Matrix4 mTransform = getTransformation(manip, camera, ro);
			prog.setObject(mTransform, manipIDs.get(manip).id);
			manipRenderer.drawCall(manip.type, prog.getPositionAttributeLocation());
		}
	}
	
}
