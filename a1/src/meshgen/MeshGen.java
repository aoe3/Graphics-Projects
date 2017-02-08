package meshgen;
import java.util.ArrayList;
import math.Vector2;
import math.Vector3;

public class MeshGen {
	public static void main(String[] args) {
	    if (args.length > 0) {
            String geo = args[0];
       		OBJMesh mesh = new OBJMesh();

            if (geo.equals("cylinder")) {
            	//center points
       			mesh.positions.add(new Vector3((float)0,(float)1,(float)0));
       			mesh.positions.add(new Vector3((float)0,(float)-1,(float)0));
                
                int n;
                if(args.length >= 3){
                	n = Integer.parseInt(args[1]);
                } else {
                	n = 32;
                }
                
                float dtheta = (2.0f * (float)Math.PI / n);
                // System.out.println(dtheta);
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

                mesh.uvs.add(new Vector2((float)0,(float)0));
       			mesh.uvs.add(new Vector2((float)0,(float)0.5));

       			for (int acc = 5; acc < mesh.positions.size(); acc+=2){
       				// System.out.println(acc);
       				float proportion = (float)(acc-3)/(2*n);

       				mesh.uvs.add(new Vector2(proportion, (float)0 ));
       				mesh.uvs.add(new Vector2(proportion, (float)0.5 ));
       			}

       			mesh.uvs.add(new Vector2((float)1,(float)0));
       			mesh.uvs.add(new Vector2((float)1,(float)0.5));

       			for(int q=0; q<mesh.uvs.size(); q++){
       				System.out.print("vt ");
                    System.out.print(mesh.uvs.get(q).x + " ");
                    System.out.println(mesh.uvs.get(q).y);
       			}
                
                for (int k = 3; k < 2 * n+2; k += 2) {
                	if(k!=(2*n+1)){
	                    OBJFace face1 = new OBJFace(3, true, true);
	                    OBJFace face2 = new OBJFace(3, true, true);

	                    face1.positions[0] = k;
	                    face1.positions[1] = k + 2;
	                    face1.positions[2] = k + 1;

	                    face2.positions[0] = k + 2;
	                    face2.positions[1] = k + 3;
	                    face2.positions[2] = k + 1;

	                    mesh.faces.add(face1);
	                    mesh.faces.add(face2);
	                } else {
	                	OBJFace face1 = new OBJFace(3, true, true);
	                    OBJFace face2 = new OBJFace(3, true, true);

	                    face1.positions[0] = k;
	                    face1.positions[1] = 3;
	                    face1.positions[2] = k+1;

	                    face2.positions[0] = 3;
	                    face2.positions[1] = 4;
	                    face2.positions[2] = k+1;

	                    mesh.faces.add(face1);
	                    mesh.faces.add(face2);
	                }
		                System.out.print("f ");
	                    System.out.print(mesh.faces.get(k-3).positions[0] + "/" + ((2*n+2)-(k-3)) + " ");
	                    System.out.print(mesh.faces.get(k-3).positions[1] + "/" + ((2*n+2)-(k-2))  + " ");
	                    System.out.println(mesh.faces.get(k-3).positions[2] + "/" + ((2*n+2)-(k-1)));
	                    System.out.print("f ");
	                    System.out.print(mesh.faces.get(k-2).positions[0] + "/" + ((2*n+2)-(k-2)) + " ");
	                    System.out.print(mesh.faces.get(k-2).positions[1] + "/" + ((2*n+2)-(k-1)) + " ");
	                    System.out.println(mesh.faces.get(k-2).positions[2] + "/" + ((2*n+2)-k));
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
	                	tophalf.positions[1] = h+5;
	                	tophalf.positions[2] = h+3;

	                	bottomhalf.positions[0] = 2;
	                	bottomhalf.positions[1] = h+4;
	                	bottomhalf.positions[2] = h+6;

	                	mesh.faces.add(tophalf);
	                	mesh.faces.add(bottomhalf);
	                } else {
                		OBJFace tophalf = new OBJFace(3, true, true);
	                	OBJFace bottomhalf = new OBJFace(3, true, true);

	                	tophalf.positions[0] = 1;
	                	tophalf.positions[1] = 3;
	                	tophalf.positions[2] = h+3;

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
                }
       			
            } else if (geo.equals("sphere")){
            	//center points
       			
       			
       			int n;
       			int m;
       			if(args.length == 4){
                	n = Integer.parseInt(args[1]);
                	m = Integer.parseInt(args[2]);
                } else if (args.length == 3){
                	n = Integer.parseInt(args[1]);
                	m = 16;
                } else {
                	n = 4;
                	m = 4;
                }

                //top point
                mesh.positions.add(new Vector3((float)0,(float)1,(float)0));

                float degreeselevation = 180.0f / m;
                float dtheta = (float)(degreeselevation * ((float)Math.PI))/180;
                float degreesrotation = 360.0f / n;
                float dphi = (float)(degreesrotation * ((float)Math.PI))/180;
                for(float theta=((float)(-Math.PI/2)+dtheta); theta<((float)Math.PI/2); theta=theta+dtheta){
					float y = (float)(Math.sin(theta));
                	for (float phi = 0; phi<((Math.PI * 2)); phi=phi+dphi){
                		float isNegative;
                		if(theta <= (float)Math.PI){
                			isNegative = 1.0f;
                		} else {
                			isNegative = -1.0f;
                		}
						float x = ((float)Math.cos(theta)*(float)Math.cos(phi)) * isNegative;
						float z = (float)Math.sin((float)phi) * (float)Math.sin((Math.acos(x))) ;
						//System.out.println("With theta = " + theta + " and phi = " + phi + " , we get (" + x + "," + y + "," + z + ").");
						mesh.positions.add(new Vector3(x,y,z));
						
					}
					//System.out.println(" ");
                }

                //bottom point
                mesh.positions.add(new Vector3((float)0,(float)-1,(float)0));
                for (int q=0; q<mesh.positions.size(); q++) {
                    System.out.print("v ");
                    System.out.print(mesh.positions.get(q).x + " ");
                    System.out.print(mesh.positions.get(q).y + " ");
                    System.out.println(mesh.positions.get(q).z);
                }

                for (int i = 1; i < n; i++){
                	OBJFace topFanFace = new OBJFace(3, true, true);
                		topFanFace.positions[0]= 0;
                		topFanFace.positions[1]= i;
                		topFanFace.positions[2]= i+1;
                		mesh.faces.add(topFanFace);
                }

               
                // for (int p = 1; p < mesh.positions.size()-1; p++){
                // 	for (int o = 0; o < mesh.positions.size()-2; p++){
                // 		OBJFace face1 = new OBJFace(3, true, true);
                // 		OBJFace face2 = new OBJFace(3, true, true);

                // 		face1.positions[0] = 
	               //      face1.positions[1] = 
	               //      face1.positions[2] = 

	               //      face2.positions[0] =
	               //      face2.positions[1] =
	               //      face2.positions[2] = 
                // 	}
                // }
                 int bottomCoord = mesh.positions.size();
                for (int j = bottomCoord-1; j > (bottomCoord - n); j--){
                	OBJFace bottomFanFace = new OBJFace(3, true, true);
                		bottomFanFace.positions[0]= bottomCoord;
                		bottomFanFace.positions[1]= j;
                		bottomFanFace.positions[2]= j+1;
                		mesh.faces.add(bottomFanFace);
                }

                for (int k=0; k<mesh.faces.size(); k++){
                	System.out.print("f ");
                	System.out.print(mesh.faces.get(k).positions[0] + " ");
                    System.out.print(mesh.faces.get(k).positions[1] + " ");
                    System.out.println(mesh.faces.get(k).positions[2]);
                }

            } else {
            	int n;
       			int m;
       			float r;
       			if (args.length == 5){
                	n = Integer.parseInt(args[1]);
                	m = Integer.parseInt(args[2]);
                	r = Float.parseFloat(args[3]);
       			} else if(args.length == 4){
                	n = Integer.parseInt(args[1]);
                	m = Integer.parseInt(args[2]);
                	r = (float)0.25;
                } else if (args.length == 3){
                	n = Integer.parseInt(args[1]);
                	m = 16;
                	r = (float)0.25;
                } else {
                	n = 4;
                	m = 4;
                	r = (float)0.25;
                }
            }
        }
	}
}
