package ray1.surface;

import egl.math.Vector3d;
import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector3;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {

    /**
     * The center of the sphere.
     */
    protected final Vector3 center = new Vector3();

    public void setCenter(Vector3 center) {
        this.center.set(center);
    }

    /**
     * The radius of the sphere.
     */
    protected float radius = 1.0f;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    protected final double M_2PI = 2 * Math.PI;

    public Sphere() {
    }

    /**
     * Tests this surface for intersection with ray. If an intersection is found
     * record is filled out with the information about the intersection and the
     * method returns true. It returns false otherwise and the information in
     * outRecord is not modified.
     *
     * @param outRecord the output IntersectionRecord
     * @param ray       the ray to intersect
     * @return true if the surface intersects the ray
     */
    public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
        // TODO#A2: fill in this function.

        Vector3d d = rayIn.direction;
        Vector3d e = rayIn.origin;
        Vector3d c = (new Vector3d()).addMultiple(1, center);

        Vector3d ec = e.clone().sub(c);
        double dec = d.clone().dot(ec);
        double dd = d.clone().dot(d);

        double discr = Math.sqrt(Math.pow(dec, 2) - dd * (ec.clone().dot(ec) - Math.pow(radius, 2)));

        if (discr > 0) {
            double t1 = (-dec + discr) / dd;
            double t2 = (-dec - discr) / dd;

            if ((t1 > rayIn.start && t1 < rayIn.end) || (t2 > rayIn.start && t2 < rayIn.end)) {
                double t;
                if (t1 < t2) {
                    if (t1 > rayIn.start) {
                        t = t1;
                    } else {
                        t = t2;
                    }
                } else {
                    if (t2 > rayIn.start) {
                        t = t2;
                    } else {
                        t = t1;
                    }
                }

                Vector3d p = e.clone().add(d.clone().mul(t));
                outRecord.location.set(p);
                outRecord.normal.set(p.clone().sub(c).div(radius).normalize());

                double theta = Math.acos((p.z-c.z) / radius);
                double phi = Math.atan2(p.y-c.y, p.x-c.x);

                if (phi < 0) {
                    phi += M_2PI;
                }

                double u = phi / M_2PI;
                double v = (Math.PI-theta) / Math.PI;

                outRecord.texCoords.set(u,v);
                outRecord.surface = this;
                outRecord.t = t;

                rayIn.end = t;

                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + shader + " end";
    }

}