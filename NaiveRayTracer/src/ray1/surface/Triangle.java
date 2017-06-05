package ray1.surface;

import egl.math.*;
import ray1.IntersectionRecord;
import ray1.Ray;
import ray1.shader.Shader;
import ray1.OBJFace;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
    /**
     * The normal vector of this triangle, if vertex normals are not specified
     */
    Vector3 norm;

    /**
     * The mesh that contains this triangle
     */
    Mesh owner;

    /**
     * The face that contains this triangle
     */
    OBJFace face = null;

    double a, b, c, d, e, f;

    public Triangle(Mesh owner, OBJFace face, Shader shader) {
        this.owner = owner;
        this.face = face;

        Vector3 v0 = owner.getMesh().getPosition(face, 0);
        Vector3 v1 = owner.getMesh().getPosition(face, 1);
        Vector3 v2 = owner.getMesh().getPosition(face, 2);

        if (!face.hasNormals()) {
            Vector3 e0 = new Vector3(), e1 = new Vector3();
            e0.set(v1).sub(v0);
            e1.set(v2).sub(v0);
            norm = new Vector3();
            norm.set(e0).cross(e1).normalize();
        }

        a = v0.x - v1.x;
        b = v0.y - v1.y;
        c = v0.z - v1.z;

        d = v0.x - v2.x;
        e = v0.y - v2.y;
        f = v0.z - v2.z;

        this.setShader(shader);
    }

    /**
     * Tests this surface for intersection with ray. If an intersection is found
     * record is filled out with the information about the intersection and the
     * method returns true. It returns false otherwise and the information in
     * outRecord is not modified.
     *
     * @param outRecord the output IntersectionRecord
     * @param rayIn     the ray to intersect
     * @return true if the surface intersects the ray
     */
    public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
        // TODO#A2: fill in this function.
        Vector3 v0 = owner.getMesh().getPosition(face, 0);
        Vector3 v1 = owner.getMesh().getPosition(face, 1);
        Vector3 v2 = owner.getMesh().getPosition(face, 2);

        Vector3d origin = rayIn.origin;
        Vector3d direction = rayIn.direction;

        double eX = origin.x;
        double eY = origin.y;
        double eZ = origin.z;

//	  double xAP = (v0.clone().x) - eX;
//	  double yAP = (v0.clone().y) - eY;
//	  double zAP = (v0.clone().z) - eZ;

//	  Vector3d abc = new Vector3d(a,b,c);
//	  Vector3d def = new Vector3d(d,e,f);
//
//	  Matrix3d bigA = new Matrix3d(abc,def,direction);
//	  Vector3d xMinP = new Vector3d(xAP, yAP, zAP);
//
//	  Vector3d betaGammaT = bigA.clone().invert().mul(xMinP.clone());
//
//	  double beta = betaGammaT.clone().x;
//	  double gamma = betaGammaT.clone().y;
//	  double t = betaGammaT.clone().z;


        double g = direction.x;
        double h = direction.y;
        double i = direction.z;
        double j = v0.x - eX;
        double k = v0.y - eY;
        double l = v0.z - eZ;
        double M = a * (e * i - h * f) + b * (g * f - d * i) + c * (d * h - e * g);

        double t = -(f * (a * k - j * b) + e * (j * c - a * l) + d * (b * l - k * c)) / M;
//        System.out.println(t);
        if (t < rayIn.start || t > rayIn.end) {
            return false;
        }

        double gamma = (i * (a * k - j * b) + h * (j * c - a * l) + g * (b * l - k * c)) / M;
        if (gamma < 0 || gamma > 1) {
            return false;
        }

        double beta = (j * (e * i - h * f) + k * (g * f - d * i) + l * (d * h - e * g)) / M;
        if (beta < 0 || beta > 1-gamma) {
            return false;
        }

        //USED FOR TEXTURES
        double alpha = (1.0 - beta - gamma);

        if (beta > 0.0) {
            if (gamma > 0.0) {
                if (beta + gamma < 1) {
                    //set position
                    Vector3d p = origin.clone().add(direction.clone().mul(t));
                    outRecord.location.set(p);
                    //set normals
                    if (!face.hasNormals()) {
                        outRecord.normal.set(this.norm);
                    } else {
                        Vector3 bMinA = v1.clone().sub(v0.clone());
                        Vector3 cMinA = v2.clone().sub(v0.clone());
                        Vector3 norm = bMinA.clone().cross(cMinA.clone());
                        outRecord.normal.set(norm).normalize();
                    }
                    //ADD TEXTURES
                    //set textures
                    if (face.hasUVs()) {
                        Vector2 uvA = owner.getMesh().getUV(face, 0).clone();
                        Vector2 uvB = owner.getMesh().getUV(face, 1).clone();
                        Vector2 uvC = owner.getMesh().getUV(face, 2).clone();

                        double uvAX = (uvA.clone().x * alpha);
                        double uvAY = (uvA.clone().y * alpha);
                        double uvBX = (uvB.clone().x * beta);
                        double uvBY = (uvB.clone().y * beta);
                        double uvCX = (uvC.clone().x * gamma);
                        double uvCY = (uvC.clone().y * gamma);

                        Vector2d uvAlpha = new Vector2d(uvAX, uvAY);
                        Vector2d uvBeta = new Vector2d(uvBX, uvBY);
                        Vector2d uvGamma = new Vector2d(uvCX, uvCY);

                        Vector2d uvFinal = uvAlpha.clone().add(uvBeta.clone()).add(uvGamma.clone());
                        outRecord.texCoords.set(uvFinal);

                    }
                    //set surface
                    outRecord.surface = this;
                    //set t
                    outRecord.t = t;
                    rayIn.end = t;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "Triangle ";
    }
}
