package anim;

import egl.math.Matrix3;
import egl.math.Matrix4;
import egl.math.Quat;

/**
 * Created by Altair on 4/20/2017.
 */
public class Written2 {
    public static void main(String[] args) {
        Quat idQuat = new Quat();
        Matrix3 rotation0 = Matrix3.createRotation(0);
        idQuat.set(rotation0);
        System.out.println("q1: " + idQuat);

        Quat quat180 = new Quat();
        Matrix4 rotation180 = Matrix4.createRotationY((float)Math.PI);
        quat180.set(rotation180);
        System.out.println("q2: " + quat180);

        float psi = (float)Math.acos(idQuat.x * quat180.x + idQuat.y * quat180.y + idQuat.z * quat180.z + idQuat.w * quat180.w);
        System.out.println("psi: " + psi);
        float coeff1 = (float)Math.sin((1-(1/6.0f))*psi);
        System.out.println(coeff1);
        float coeff2 = (float)Math.sin(1/6.0f*psi);
        System.out.println(coeff2);
        float denom = (float)Math.sin(psi);
        System.out.println(denom);
        Quat quatOneSixth = ((idQuat.clone().scale(coeff1)).add(quat180.clone().scale(coeff2))).scale(1/denom);
        System.out.println("q3: " +  quatOneSixth);
        System.out.println("Slerp verify: " + Quat.slerp(idQuat, quat180, 1/6.0f));

        float theta = 2*psi;
        System.out.println("theta: " + theta);

        Matrix3 rotq3 = new Matrix3();
        quatOneSixth.toRotationMatrix(rotq3);

        Matrix3 mat180 = new Matrix3();
        quat180.toRotationMatrix(mat180);

        System.out.println("M(q3): \n" + rotq3);
        System.out.println("M(q2): \n" + mat180);

        for (int i=0; i<6; i++) {
            rotq3.mulBefore(rotq3);
            System.out.println("M(q3) x6: \n" + rotq3);
        }
        System.out.println(Math.acos(-0.5000038));
    }
}
