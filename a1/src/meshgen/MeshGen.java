package meshgen;
import java.util.ArrayList;
import math.Vector3;

public class MeshGen {
	public static void main(String[] args) {
	    if (args.length > 0) {
            String geo = args[0];
       		OBJMesh mesh = new OBJMesh();

            if (geo.equals("cylinder")) {
       			mesh.positions.add(new Vector3((float)0,(float)1,(float)0));
       			mesh.positions.add(new Vector3((float)0,(float)-1,(float)0));
                
                int n = Integer.parseInt(args[1]);
                float dtheta = (2.0f * (float)Math.PI / n);
                System.out.println(dtheta);
                // top face, y=1
                float y = 1.0f;

                for (float theta=0.0f; theta<(Math.PI * 2); theta=theta+dtheta) {
                    float z = (float)Math.sin(theta);
                    float x = (float)Math.cos(theta);
                    mesh.positions.add(new Vector3(x,y,z));
                    mesh.positions.add(new Vector3(x,-y,z));
                }

                for (int i=0; i<mesh.positions.size(); i++) {
                    System.out.print("v ");
                    System.out.print(mesh.positions.get(i).x + " ");
                    System.out.print(mesh.positions.get(i).y + " ");
                    System.out.println(mesh.positions.get(i).z);
                }
                
                for (int k = 3; k < 2 * n+2; k += 2) {
                	if(k!=(2*n+1)){
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
	                    face1.positions[2] = 3;

	                    face2.positions[0] = 3;
	                    face2.positions[1] = k + 1;
	                    face2.positions[2] = 4;

	                    mesh.faces.add(face1);
	                    mesh.faces.add(face2);
	                }
	                System.out.print("f ");
                    System.out.print(mesh.faces.get(k-3).positions[0] + " ");
                    System.out.print(mesh.faces.get(k-3).positions[1] + " ");
                    System.out.println(mesh.faces.get(k-3).positions[2]);
                    System.out.print("f ");
                    System.out.print(mesh.faces.get(k-2).positions[0] + " ");
                    System.out.print(mesh.faces.get(k-2).positions[1] + " ");
                    System.out.println(mesh.faces.get(k-2).positions[2]);
                }
//                for(int l=0; l<(2*n); l++){
//                  	System.out.println(mesh.faces);
//                }
//                System.out.println("");

       			for(int h = 0; h < (2*n); h+=2){
       				if(h<(2*n-2)){
	                	OBJFace tophalf = new OBJFace(3, true, true);
	                	OBJFace bottomhalf = new OBJFace(3, true, true);

	                	tophalf.positions[0] = 1;
	                	tophalf.positions[1] = h+3;
	                	tophalf.positions[2] = h+5;

	                	bottomhalf.positions[0] = 2;
	                	bottomhalf.positions[1] = h+4;
	                	bottomhalf.positions[2] = h+6;

	                	mesh.faces.add(tophalf);
	                	mesh.faces.add(bottomhalf);
	                } else {
                		OBJFace tophalf = new OBJFace(3, true, true);
	                	OBJFace bottomhalf = new OBJFace(3, true, true);

	                	tophalf.positions[0] = 1;
	                	tophalf.positions[1] = h+3;
	                	tophalf.positions[2] = 3;

	                	bottomhalf.positions[0] = 2;
	                	bottomhalf.positions[1] = h+4;
	                	bottomhalf.positions[2] = 4;

	                	mesh.faces.add(tophalf);
	                	mesh.faces.add(bottomhalf);
                	}
                }
                for (int j = (2*n); j < (4*n); j++){
       			    System.out.print("f ");
                	System.out.print(mesh.faces.get(j).positions[0] + " ");
                    System.out.print(mesh.faces.get(j).positions[1] + " ");
                    System.out.println(mesh.faces.get(j).positions[2]);
//                    System.out.println("");
                }
            }
        }
	}
}
