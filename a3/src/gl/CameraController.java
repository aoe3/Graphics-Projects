package gl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import common.Scene;
import common.event.SceneTransformationEvent;
import egl.math.Matrix4;
import egl.math.Vector3;

public class CameraController {
	protected final Scene scene;
	public RenderCamera camera;
	protected final RenderEnvironment rEnv;
	
	protected boolean prevFrameButtonDown = false;
	protected int prevMouseX, prevMouseY;
	
	protected boolean orbitMode = false;
	
	public CameraController(Scene s, RenderEnvironment re, RenderCamera c) {
		scene = s;
		rEnv = re;
		camera = c;
	}
	
	/**
	 * Update the camera's transformation matrix in response to user input.
	 * 
	 * Pairs of keys are available to translate the camera in three direction oriented to the camera,
	 * and to rotate around three axes oriented to the camera.  Mouse input can also be used to rotate 
	 * the camera around the horizontal and vertical axes.  All effects of these controls are achieved
	 * by altering the transformation stored in the SceneCamera that is referenced by the RenderCamera
	 * this controller is associated with.
	 * 
	 * @param et  time elapsed since previous frame
	 */
	public void update(double et) {
		Vector3 motion = new Vector3();
		Vector3 rotation = new Vector3();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) { motion.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) { motion.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { motion.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { motion.add(0, 1, 0); }

		if(Keyboard.isKeyDown(Keyboard.KEY_E)) { rotation.add(0, 0, -1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) { rotation.add(0, 0, 1); }
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { rotation.add(-1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) { rotation.add(1, 0, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { rotation.add(0, -1, 0); }
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { rotation.add(0, 1, 0); }
		
		if(Keyboard.isKeyDown(Keyboard.KEY_O)) { orbitMode = true; } 
		if(Keyboard.isKeyDown(Keyboard.KEY_F)) { orbitMode = false; } 
		
		boolean thisFrameButtonDown = Mouse.isButtonDown(0) && !(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
		int thisMouseX = Mouse.getX(), thisMouseY = Mouse.getY();
		if (thisFrameButtonDown && prevFrameButtonDown) {
			rotation.add(0, -0.1f * (thisMouseX - prevMouseX), 0);
			rotation.add(0.1f * (thisMouseY - prevMouseY), 0, 0);
		}
		prevFrameButtonDown = thisFrameButtonDown;
		prevMouseX = thisMouseX;
		prevMouseY = thisMouseY;
		
		RenderObject parent = rEnv.findObject(scene.objects.get(camera.sceneObject.parent));
		Matrix4 pMat = parent == null ? new Matrix4() : parent.mWorldTransform;
		if(motion.lenSq() > 0.01) {
			motion.normalize();
			motion.mul(5 * (float)et);
			translate(pMat, camera.sceneObject.transformation, motion);
		}
		if(rotation.lenSq() > 0.01) {
			rotation.mul((float)(100.0 * et));
			rotate(pMat, camera.sceneObject.transformation, rotation);
		}
		scene.sendEvent(new SceneTransformationEvent(camera.sceneObject));
	}

	/**
	 * Apply a rotation to the camera.
	 * 
	 * Rotate the camera about one ore more of its local axes, by modifying <b>transformation</b>.  The 
	 * camera is rotated by rotation.x about its horizontal axis, by rotation.y about its vertical axis, 
	 * and by rotation.z around its view direction.  The rotation is about the camera's viewpoint, if 
	 * this.orbitMode is false, or about the world origin, if this.orbitMode is true.
	 * 
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param rotation  The rotation in degrees, as Euler angles (rotation angles about x, y, z axes)
	 */
	protected void rotate(Matrix4 parentWorld, Matrix4 transformation, Vector3 rotation) {
		
		// TODO#A3#Part 3
		float horizontal = (rotation.clone().x * (float)Math.PI) / 180.0f;
		float vertical = (rotation.clone().y * (float)Math.PI) / 180.0f;
		float zaxis = (rotation.clone().z * (float)Math.PI) / 180.0f;
		
		Matrix4 hM = Matrix4.createRotationX(horizontal);
		Matrix4 vM = Matrix4.createRotationY(vertical);
		Matrix4 zM = Matrix4.createRotationZ(zaxis);
		
		boolean orbit = this.orbitMode;
		
		if(orbit){
			
			Matrix4 toWorld = this.camera.sceneCamera.transformation.clone().invert().mulBefore(parentWorld.clone().invert());
			transformation.mulBefore(toWorld);
			transformation.mulBefore(zM);
			transformation.mulBefore(vM);
			transformation.mulBefore(hM);
			transformation.mulBefore(toWorld.invert());
		} else {
			transformation.mulBefore(hM);
			transformation.mulBefore(vM);
			transformation.mulBefore(zM);
		}
		
		
	}
	
	/**
	 * Apply a translation to the camera.
	 * 
	 * Translate the camera by an offset measured in camera space, by modifying <b>transformation</b>.
	 * @param parentWorld  The frame-to-world matrix of the camera's parent
	 * @param transformation  The camera's transformation matrix (in/out parameter)
	 * @param motion  The translation in camera-space units
	 */
	protected void translate(Matrix4 parentWorld, Matrix4 transformation, Vector3 motion) {
		// TODO#A3#Part 3
		Matrix4 localTransform = this.camera.sceneObject.transformation.clone();
		Matrix4 axes = localTransform.invert().mulAfter(parentWorld.clone().invert());
		
		Matrix4 camTranslate = Matrix4.createTranslation(motion);
		
		axes.mulBefore(transformation.mulBefore(camTranslate));
	}
}
