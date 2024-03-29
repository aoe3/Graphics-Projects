package gl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.Cubemap;
import common.Material;
import common.Mesh;
import common.Scene;
import common.SceneCamera;
import common.SceneLight;
import common.SceneObject;
import common.Texture;
import egl.math.Matrix4;
import egl.math.Vector2;

/**
 * Class to hold functions that build the scene hierarchy for OpenGL rendering, starting from the common 
 * Scene hierarchy that is parsed from XML.  These rendering data structures correspond to the objects 
 * and resources in the scene but contain extra state and information used for rendering and interaction.
 * 
 * @author cristian
 */
public class RenderTreeBuilder {

	/**
	 * Build a RenderEnvironment that can be used to render the given scene, by constructing the corresponding
	 * rendering objects for the scene hierarchy and the meshes, textures, and materials it uses.
	 * 
	 * @param scene  the Scene environment to be rendered
	 * @param viewSize  the size of the viewport in pixels
	 * @return
	 */
	public static RenderEnvironment build(Scene scene, Vector2 viewSize) {
		RenderEnvironment env = new RenderEnvironment(viewSize);
		
		// Resources
		buildTextures(scene, env);
		buildMeshes(scene, env);
		buildCubemaps(scene, env);
		// Render-able State Is Created Here
		buildMaterials(scene, env);
		buildTree(scene, env);
		
		return env;
	}
	
	/**
	 * Build a tree of RenderObjects that mirrors the tree of SceneObjects in <scene>.
	 * 
	 * After the objects are created and linked up using the RenderObject.parent and RenderObject.children 
	 * fields, the frame-to-world transformations of all objects are computed.
	 *  
	 * @param scene
	 * @param env
	 */
	public static void buildTree(Scene scene, RenderEnvironment env) {
		// Clear Out Any Old Data
		env.cameras.clear();
		env.lights.clear();
		
		// Pass 1: Create The Render Object Mapping
		HashMap<String, RenderObject> dict = new HashMap<>();
		for(SceneObject so : scene.objects) {
			RenderObject ro;
			
			if(so instanceof SceneCamera) {
				ro = new RenderCamera(so, env.viewportSize);
				env.cameras.add((RenderCamera)ro);
			}
			else if(so instanceof SceneLight) {
				ro = new RenderLight(so);
				env.lights.add((RenderLight)ro);
			}
			else {
				ro = new RenderObject(so);
			}
			dict.put(so.getID().name, ro);

		}

		// Pass 2: Create Parent-Children Bindings
		for(SceneObject so : scene.objects) {
			if(so.parent != null) {
				// Get The Child
				RenderObject o = dict.get(so.getID().name);
				if(o != null) {
					// Get The Parent
					RenderObject p = dict.get(so.parent);
					if(p != null) {
						// Bind Child And Parent
						p.children.add(o);
						o.parent = p;
					}
				}
			}
		}

		// Pass 3: Find A Root Node If It Exists
		env.root = dict.get("World");
		rippleTransformations(env);
		
		// Set Up Render State
		env.linkObjectResources();
	}
	
	/**
	 * Compute the frame-to-world transformations for all objects in the hierarchy.
	 * 
	 * The root node's frame-to-world transformation is simply its own transformation, and
	 * the frame-to-world transformation of every other object in the scene is the composition
	 * of its parent's frame-to-world transformation with the object's own transformation.
	 * 
	 * The results are stored in the RenderObject.mWorldTransform field of each object
	 * in the hierarchy.  At the same time, the inverse-transpose matrices, which are used
	 * in transforming normal vectors, are stored in the RenderObject.mWorldTransformIT.
	 * 
	 * Additionally, for all the cameras in the scene (RenderEnvironment.cameras) you must
	 * recalculate the camera's ViewPerspectiveProjection matrix.
	 * 
	 * @param env  The environment containing the hierarchy to be processed.
	 */
	public static void rippleTransformations(RenderEnvironment env) {
		// TODO#A3#Part 1
		RenderObject root = env.root;
		Matrix4 rootTransform = root.sceneObject.transformation.clone();
		
		root.mWorldTransform.set(rootTransform.clone());
		root.mWorldTransformIT.set(rootTransform.clone().invert().transpose().getAxes());
		List<RenderObject> listOfNodes = returnAllNodes(root);
		for(int i = 0; i< listOfNodes.size(); i++){
			RenderObject currentNode = listOfNodes.get(i);
			currentNode.mWorldTransform.set(findParents(root, currentNode));
			currentNode.mWorldTransformIT.set(findParentsIT(root, listOfNodes.get(i)).getAxes());
		}
		
		for (RenderCamera cam : env.cameras){
			cam.updateCameraMatrix(cam.viewportSize);
		}
	}
	
	public static List<RenderObject> returnAllNodes(RenderObject theroot){
	    List<RenderObject> listOfNodes = new ArrayList<RenderObject>();
	    addAllNodes(theroot, listOfNodes);
	    return listOfNodes;
	}

	private static void addAllNodes(RenderObject theroot, List<RenderObject> listOfNodes) {
	    if (theroot != null) {
	        listOfNodes.add(theroot);
	        List<RenderObject> children = theroot.children;
	        if (children != null) {
	            for (RenderObject child: children) {
	                addAllNodes(child, listOfNodes);
	            }
	        }
	    }
	}
	
	public static Matrix4 findParents(RenderObject treeRoot, RenderObject node){
		Matrix4 mReturn = node.sceneObject.transformation.clone();
		
		if (node == treeRoot){
			return mReturn;
			
		} else {
			RenderObject parentNode = node.parent;
			return mReturn.mulAfter(findParents(treeRoot, parentNode));
			//
		}
	}
	
	public static Matrix4 findParentsIT(RenderObject treeRoot, RenderObject node){
		Matrix4 mReturnIT = node.sceneObject.transformation.clone().invert().transpose();
		
		if (node == treeRoot){
			return mReturnIT;
			
		} else {
			RenderObject parentNode = node.parent;
			return mReturnIT.mulAfter(findParentsIT(treeRoot, parentNode));
		}
	}
	/**
	 * Make a RenderMaterial for each Material in <scene>.
	 * @param scene  the Scene to read from
	 * @param env  the RenderEnvironment to write to
	 */
	public static void buildMaterials(Scene scene, RenderEnvironment env) {
		for(Material m : scene.materials) {
			env.addMaterial(m);
		}
	}

	/**
	 * Make a RenderMesh for each Mesh in <scene>.
	 * @param scene  the Scene to read from
	 * @param env  the RenderEnvironment to write to
	 */
	public static void buildMeshes(Scene scene, RenderEnvironment env) {
		for(Mesh m : scene.meshes) {
			env.addMesh(m);
		}
	}
	
	public static void buildCubemaps(Scene scene, RenderEnvironment env) {
		for(Cubemap c : scene.cubemaps) {
			env.addCubemap(c);
		}
	}

	/**
	 * Make a GLTexture for each Texture in <scene>.
	 * @param scene  the Scene to read from
	 * @param env  the RenderEnvironment to write to
	 */
	public static void buildTextures(Scene scene, RenderEnvironment env) {
		for(Texture t : scene.textures){ 
			env.addTexture(t);
		}
	}
}
