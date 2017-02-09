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

          //       mesh.uvs.add(new Vector2((float)0,(float)0));
       			// mesh.uvs.add(new Vector2((float)0,(float)0.5));

       			// for (int acc = 5; acc < mesh.positions.size(); acc+=2){
       			// 	// System.out.println(acc);
       			// 	float proportion = (float)(acc-3)/(2*n);

       			// 	mesh.uvs.add(new Vector2(proportion, (float)0 ));
       			// 	mesh.uvs.add(new Vector2(proportion, (float)0.5 ));
       			// }

       			// mesh.uvs.add(new Vector2((float)1,(float)0));
       			// mesh.uvs.add(new Vector2((float)1,(float)0.5));

       			// for(int q=0; q<mesh.uvs.size(); q++){
       			// 	System.out.print("vt ");
          //           System.out.print(mesh.uvs.get(q).x + " ");
          //           System.out.println(mesh.uvs.get(q).y);
       			// }
                
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
                float dtheta = (float) degreeselevation * ((float)Math.PI/180.0f);
                float degreesrotation = 360.0f / n;
                float dphi = (float)degreesrotation * ((float)Math.PI/180.0f);
                for(float theta=(dtheta); theta<((float)Math.PI); theta=theta+dtheta){
                	for (float phi = 0; phi<((Math.PI * 2)); phi=phi+dphi){
                		float x = (float) Math.sin(theta) * (float) Math.sin(phi);
                		float y = (float) Math.cos(theta);
                		float z = (float) Math.sin(theta) * (float) Math.cos(phi);
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

                for (int i = 2; i < n+1; i++){
                	if(i == n){
                		OBJFace topFanFace = new OBJFace(3, true, true);
                		topFanFace.positions[0]= 1;
                		topFanFace.positions[1]= i;
                		topFanFace.positions[2]= i+1;
                		mesh.faces.add(topFanFace);

                		OBJFace lastFace = new OBJFace(3,true,true);
                		lastFace.positions[0]= 1;
                		lastFace.positions[1]= n+1;
                		lastFace.positions[2]= 2;
                		mesh.faces.add(lastFace);
                	} else {
                	OBJFace topFanFace = new OBJFace(3, true, true);
                		topFanFace.positions[0]= 1;
                		topFanFace.positions[1]= i;
                		topFanFace.positions[2]= i+1;
                		mesh.faces.add(topFanFace);
                	}
                }

               
                for (int p = 0; p < m-1; p++){
                	int down = p*n+1;
                	int up = (p+1)*n+1;
                	for (int q=1; q<(n+2); q++){
                		if(q != (n)){
	                		OBJFace face1 = new OBJFace(3, true, true);
	                		OBJFace face2 = new OBJFace(3, true, true);

	                		face1.positions[0] = down+q+1;
	                		face1.positions[1] = down+q;
	                		face1.positions[2] = up+q+1;

	                		face2.positions[0] = down+q;
	                		face2.positions[1] = up+q;
	                		face2.positions[2] = up+q+1;

	                		mesh.faces.add(face1);
	                		mesh.faces.add(face2);
                		} else {
                			OBJFace face1 = new OBJFace(3, true, true);
	                		OBJFace face2 = new OBJFace(3, true, true);

	                		face1.positions[0] = down+q;
	                		face1.positions[1] = up+q;
	                		face1.positions[2] = up+1;

	                		face2.positions[0] = down+q+1;
	                		face2.positions[1] = down+1;
	                		face2.positions[2] = up;


	                		mesh.faces.add(face1);
	                		mesh.faces.add(face2);
                		}
                		
                	}
                 	OBJFace face3 = new OBJFace(3, true, true);
                	OBJFace face4 = new OBJFace(3, true, true);  

                	face3.positions[0] = down+n-1;
            		face3.positions[1] = up+n;
            		face3.positions[2] = up;

            		face4.positions[0] = down+n-1;
            		face4.positions[1] = up;
            		face4.positions[2] = down-1;   

            		mesh.faces.add(face3);
                	mesh.faces.add(face4);       	
                }

                int bottomCoord = mesh.positions.size();
                for (int j = bottomCoord-1; j > (bottomCoord - n); j--){
	                if (j == (bottomCoord-n)+1){
	                	OBJFace bottomFanFace = new OBJFace(3, true, true);
	                		bottomFanFace.positions[0]= bottomCoord;
	                		bottomFanFace.positions[1]= j;
	                		bottomFanFace.positions[2]= j-1;
	                		mesh.faces.add(bottomFanFace);

	                	OBJFace lastFace2 = new OBJFace(3,true,true);
	                		lastFace2.positions[0]= bottomCoord;
	                		lastFace2.positions[1]= bottomCoord - n;
	                		lastFace2.positions[2]= bottomCoord - 1;

	                		mesh.faces.add(bottomFanFace);
	                		mesh.faces.add(lastFace2);
	                } else {
	                	OBJFace bottomFanFace = new OBJFace(3, true, true);
	                		bottomFanFace.positions[0]= bottomCoord;
	                		bottomFanFace.positions[1]= j;
	                		bottomFanFace.positions[2]= j-1;
	                		mesh.faces.add(bottomFanFace);
	                }
	            }

                for (int k=0; k<mesh.faces.size(); k++){
                	System.out.print("f ");
                	System.out.print(mesh.faces.get(k).positions[0] + " ");
                    System.out.print(mesh.faces.get(k).positions[1] + " ");
                    System.out.println(mesh.faces.get(k).positions[2]);
                }

            } else if (geo.equals("torus")){
            	int n;
       			int m;
       			float littler;
       			float bigR = 1.0f;
       			if (args.length == 5){
                	n = Integer.parseInt(args[1]);
                	m = Integer.parseInt(args[2]);
                	littler = Float.parseFloat(args[3]);
       			} else if(args.length == 4){
                	n = Integer.parseInt(args[1]);
                	m = Integer.parseInt(args[2]);
                	littler = (float)0.25;
                } else if (args.length == 3){
                	n = Integer.parseInt(args[1]);
                	m = 16;
                	littler = (float)0.25;
                } else {
                	n = 32;
                	m = 16;
                	littler = (float)0.25;
                }

                float degreesAroundTorus = 360.0f/m;
                float degreesRing = 360.0f/n;
                float dphi = degreesAroundTorus * ((float)Math.PI/180.0f);
                float dtheta = degreesRing * ((float)Math.PI/180.0f);
                for (float phi = 0; phi<((Math.PI * 2)); phi=phi+dphi){
                	for(float theta=0; theta<(Math.PI * 2); theta=theta+dtheta){
                		float x = ((float)bigR + ((float)littler * (float)(Math.cos(phi)))) * (float)(Math.cos(theta));
                		float y = ((float)littler * ((float)Math.sin(phi)));
                		float z = ((float)bigR + ((float)littler * (float)(Math.cos(phi)))) * (float)(Math.sin(theta));
						mesh.positions.add(new Vector3(x,y,z));
                	}
                }
                
                for (int i=0; i<mesh.positions.size(); i++) {
                    System.out.print("v ");
                    System.out.print(mesh.positions.get(i).x + " ");
                    System.out.print(mesh.positions.get(i).y + " ");
                    System.out.println(mesh.positions.get(i).z);
                }
                

                for (int p = 0; p < m; p++){
                	int down = p*n+1;
                	int up = (p+1)*n+1;
                	for (int q=0; q<(n); q++){
                		if(q != (n-1)){
                			
	                		OBJFace face1 = new OBJFace(3, true, true);
	                		OBJFace face2 = new OBJFace(3, true, true);

	                		face1.positions[0] = down+q+1;
	                		face1.positions[1] = down+q;
	                		face1.positions[2] = up+q+1;

	                		face2.positions[0] = down+q;
	                		face2.positions[1] = up+q;
	                		face2.positions[2] = up+q+1;

	                		mesh.faces.add(face1);
	                		mesh.faces.add(face2);
		                	
                		} else {
	                			OBJFace face1 = new OBJFace(3, true, true);
		                		OBJFace face2 = new OBJFace(3, true, true);

		                		face1.positions[0] = up-n;
		                		face1.positions[1] = down-1 ;
		                		face1.positions[2] = down+q;

		                		face2.positions[0] = down+q;
		                		face2.positions[1] = up;
		                		face2.positions[2] = down;


		                		mesh.faces.add(face1);
		                		mesh.faces.add(face2);
                		}
                		
                	}
              //    	OBJFace face3 = new OBJFace(3, true, true);
              //   	OBJFace face4 = new OBJFace(3, true, true);  

              //   	face3.positions[0] = down+n-1;
            		// face3.positions[1] = up+n;
            		// face3.positions[2] = up;

            		// face4.positions[0] = down+n-1;
            		// face4.positions[1] = up;
            		// face4.positions[2] = down-1;   

            		// mesh.faces.add(face3);
              //   	mesh.faces.add(face4);       	
                }

                int bottomCoord = mesh.positions.size();
                for (int j = bottomCoord; j > (bottomCoord - n); j--){
                	if(j == (bottomCoord - n + 1)){
		                OBJFace bottomStrip1= new OBJFace(3,true,true);
		                OBJFace bottomStrip2= new OBJFace(3,true,true);

		             //    bottomStrip1.positions[0]= n - (bottomCoord - j);
		             //    bottomStrip1.positions[1]= j-1;
		             //    bottomStrip1.positions[2]= n- (bottomCoord - (j-1));

		             //    bottomStrip2.positions[0]= n - (bottomCoord - (j-1)) + 1;
		             //    bottomStrip2.positions[1]= j;
		             //    bottomStrip2.positions[2]= j-1;

		             //    mesh.faces.add(bottomStrip1);
			            // mesh.faces.add(bottomStrip2);

			            OBJFace specialCase1 = new OBJFace(3, true, true);
			            OBJFace specialCase2 = new OBJFace(3, true, true);

			            specialCase1.positions[0] = n ;
			            specialCase1.positions[1] = 1;
			            specialCase1.positions[2] = bottomCoord;

			            specialCase2.positions[0] = 1;
			            specialCase2.positions[1] = j;
			            specialCase2.positions[2] = bottomCoord;

			            mesh.faces.add(specialCase1);
			            mesh.faces.add(specialCase2);
			        } else {

			        OBJFace bottomStrip1= new OBJFace(3,true,true);
	                OBJFace bottomStrip2= new OBJFace(3,true,true);

	                bottomStrip1.positions[0]= n - (bottomCoord - j);
	                bottomStrip1.positions[1]= j-1;
	                bottomStrip1.positions[2]= n- (bottomCoord - (j-1));

	                bottomStrip2.positions[0]= n - (bottomCoord - (j-1)) + 1;
	                bottomStrip2.positions[1]= j;
	                bottomStrip2.positions[2]= j-1;

	                mesh.faces.add(bottomStrip1);
		            mesh.faces.add(bottomStrip2);
		        	}
	            }

                for (int k=0; k<mesh.faces.size(); k++){
                	System.out.print("f ");
                	System.out.print(mesh.faces.get(k).positions[0] + " ");
                    System.out.print(mesh.faces.get(k).positions[1] + " ");
                    System.out.println(mesh.faces.get(k).positions[2]);
                }

            } else {
            	System.out.println("Please input a valid geometry");
            }
        }
	}
}
