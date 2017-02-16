package ray1.camera;

import ray1.Ray;
import egl.math.Vector3;
import egl.math.Vector3d;


public class OrthographicCamera extends Camera {

    //TODO#A2: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.

    Vector3 u,v,w;

    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#A2: Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on viewDir and viewUp
        // 2) Set up the helper variables if needed
        w = getViewDir().negate().normalize();
        u = getViewUp().cross(w).normalize();
        v = w.clone().cross(u).normalize();
    }

    /**
     * Set outRay to be a ray from the camera through a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
    public void getRay(Ray outRay, float inU, float inV) {
        // TODO#A2: Fill in this function.
        // 1) Transform inU so that it lies between [-viewWidth / 2, +viewWidth / 2] 
        //    instead of [0, 1]. Similarly, transform inV so that its range is
        //    [-viewHeight / 2, +viewHeight / 2]
        // 2) Set the origin field of outRay for an orthographic camera. 
        //    In an orthographic camera, the origin should depend on your transformed
        //    inU and inV and your basis vectors u and v.
        // 3) Set the direction field of outRay for an orthographic camera.

        inU *= getViewWidth();
        inU -= getViewWidth()/2;

        inV *= getViewHeight();
        inV -= getViewHeight()/2;


        Vector3 uU = u.clone().mul(inU);
        Vector3 vV = v.clone().mul(inV);
        Vector3 uUvV = uU.clone().add(vV);
        Vector3d e = (new Vector3d()).addMultiple(1,getViewPoint());
        Vector3d origin = e.clone().addMultiple(1,uUvV);       // S = E + uU + vV;
        Vector3d direction = (new Vector3d()).addMultiple(1, w.clone().negate());

        outRay.set(origin, direction);
        outRay.makeOffsetRay();
    }
}
