package meshgen;
import java.util.ArrayList;
import math.Vector3;
import meshgen.OBJMesh;
import meshgen.OBJFace;

public class MeshGen {
	public static void main(String[] args) {
	    if (args.length > 0) {
            String geo = args[0];
            OBJMesh mesh = new OBJMesh();
            if (geo.equals("cylinder")) {
                int n = Integer.parseInt(args[1]);
                float dtheta = 2.0f * (float)Math.PI / n;

                // top face, y=1, bottom face, y=-1
                float y = -1.0f;
                for (float theta=0.0f; theta<2.0f*Math.PI; theta+=dtheta) {
                    float x = (float)Math.sin(theta);
                    float z = (float)Math.cos(theta);
                    mesh.positions.add(new Vector3(x,y,z));
                    mesh.positions.add(new Vector3(x,-y,z));
                }
//                for (int i=0; i<mesh.positions.size(); i++) {
//                    System.out.println(mesh.positions.get(i));
//                }

                for (int k = 1; k < 2 * n; k += 2) {
                	if(k!=(2*n-1)){
	                    OBJFace face1 = new OBJFace(3, true, true);
	                    OBJFace face2 = new OBJFace(3, true, true);

	                    face1.positions[0] = k;
	                    face1.positions[1] = k + 1;
	                    face1.positions[2] = k + 2;

	                    face2.positions[0] = k + 2;
	                    face2.positions[1] = k + 1;
	                    face2.positions[2] = k + 3;

	                    mesh.faces.add(face1);
	                    mesh.faces.add(face2);
	                } else {
	                	OBJFace face1 = new OBJFace(3, true, true);
	                    OBJFace face2 = new OBJFace(3, true, true);

	                    face1.positions[0] = k;
	                    face1.positions[1] = k + 1;
	                    face1.positions[2] = 1;

	                    face2.positions[0] = 1;
	                    face2.positions[1] = k + 1;
	                    face2.positions[2] = 2;

	                    mesh.faces.add(face1);
	                    mesh.faces.add(face2);
	                }
                    System.out.println(mesh.faces.get(k-1).positions[0]);
                    System.out.println(mesh.faces.get(k-1).positions[1]);
                    System.out.println(mesh.faces.get(k-1).positions[2]);
                    System.out.println("");
                    System.out.println(mesh.faces.get(k).positions[0]);
                    System.out.println(mesh.faces.get(k).positions[1]);
                    System.out.println(mesh.faces.get(k).positions[2]);
                    System.out.println("");
                }
            }
        }
	}
}
