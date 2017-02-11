package meshgen;
import java.io.IOException;
import java.util.ArrayList;

import math.Vector3;
import math.Vector2;

public class MeshGen {
	public static void main(String[] args) {
	    if (args.length > 0) {
            String geo = args[0];
       		OBJMesh mesh = new OBJMesh();

            if (geo.equals("cylinder")) {
                int n;
                String f;
                if(args.length == 3){
                    n = Integer.parseInt(args[1]);
                    f = args[2];
                } else if (args.length == 2) {
                    n = 32;
                    f = args[1];
                } else {
                    System.out.println("Please input a valid filename.");
                    return;
                }

                // VERTICES
                // top and bottom cap center vertices
       			mesh.positions.add(new Vector3((float)0,(float)1,(float)0));
       			mesh.positions.add(new Vector3((float)0,(float)-1,(float)0));

       			// rim vertices
                float dtheta = (2.0f * (float)Math.PI / n);
                float y = 1.0f;

                for (int i=0; i<n; i++) {
                    float theta = i*dtheta;
                    float z = (float)Math.cos(theta);
                    float x = (float)Math.sin(theta);
                    mesh.positions.add(new Vector3(-x,y,-z));
                    mesh.positions.add(new Vector3(-x,-y,-z));

                }

                // side faces
                for (int k = 3; k < 2*n+2; k += 2) {
                	if(k<2*n){
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
                        face1.positions[1] = k+1;
                        face1.positions[2] = 3;

                        face2.positions[0] = 3;
                        face2.positions[1] = k+1;
                        face2.positions[2] = 4;

                        mesh.faces.add(face1);
                        mesh.faces.add(face2);
	                }
                }

                // cap faces
       			for(int h = 0; h < (2*n); h+=2){
       				if(h<(2*n-2)){
	                	OBJFace tophalf = new OBJFace(3, true, true);
	                	OBJFace bottomhalf = new OBJFace(3, true, true);

	                	tophalf.positions[0] = 1;
	                	tophalf.positions[1] = h+3;
	                	tophalf.positions[2] = h+5;

	                	bottomhalf.positions[0] = 2;
	                	bottomhalf.positions[1] = h+6;
	                	bottomhalf.positions[2] = h+4;

	                	mesh.faces.add(tophalf);
	                	mesh.faces.add(bottomhalf);
	                } else {
                		OBJFace tophalf = new OBJFace(3, true, true);
	                	OBJFace bottomhalf = new OBJFace(3, true, true);

	                	tophalf.positions[0] = 1;
	                	tophalf.positions[1] = h+3;
	                	tophalf.positions[2] = 3;

	                	bottomhalf.positions[0] = 2;
	                	bottomhalf.positions[1] = 4;
	                	bottomhalf.positions[2] = h+4;

	                	mesh.faces.add(tophalf);
	                	mesh.faces.add(bottomhalf);
                	}
                }

                // NORMALS
                // top and bottom cap vertices
                mesh.normals.add(new Vector3(0,1,0));
                mesh.normals.add(new Vector3(0,-1,0));

                // side normals, calculated from actual surface being approximated
                for (int i=0; i<2*n; i+=2) {
       			    mesh.normals.add((new Vector3(mesh.positions.get(i+2).x, 0, mesh.positions.get(i+2).z)).normalize());
                }

                // side normal indices
                for (int m=0; m<2*n; m+=2) {
                    if (m == 2*n-2) {
                        mesh.faces.get(m).normals[0] = n+2;
                        mesh.faces.get(m).normals[1] = n+2;
                        mesh.faces.get(m).normals[2] = 3;

                        mesh.faces.get(m+1).normals[0] = 3;
                        mesh.faces.get(m+1).normals[1] = n+2;
                        mesh.faces.get(m+1).normals[2] = 3;
                    }
                    else {
                        mesh.faces.get(m).normals[0] = m/2+3;
                        mesh.faces.get(m).normals[1] = m/2+3;
                        mesh.faces.get(m).normals[2] = m/2+4;

                        mesh.faces.get(m+1).normals[0] = m/2+4;
                        mesh.faces.get(m+1).normals[1] = m/2+3;
                        mesh.faces.get(m+1).normals[2] = m/2+4;
                    }
                }

                // cap normal indices
                for (int w=2*n; w<4*n; w+=2) {
                    mesh.faces.get(w).normals[0] = 1;
                    mesh.faces.get(w).normals[1] = 1;
                    mesh.faces.get(w).normals[2] = 1;

                    mesh.faces.get(w+1).normals[0] = 2;
                    mesh.faces.get(w+1).normals[1] = 2;
                    mesh.faces.get(w+1).normals[2] = 2;
                }


                // UV COORDINATES
                // side UVs
                float du = 1.0f/n;
                for (float i=0; i<=n; i++){
                    float u = i*du;
                    mesh.uvs.add(new Vector2(u, 0.5f));
                    mesh.uvs.add(new Vector2(u, 0.0f));
                }

                // cap UVs
                Vector2 oTop = new Vector2(0.75f,0.75f);
                Vector2 oBottom = new Vector2(0.25f,0.75f);
                mesh.uvs.add(oTop);
                mesh.uvs.add(oBottom);
                for (int j=0; j<n; j++) {
                    float theta = j*dtheta;
                    float u = 0.25f*(float)Math.cos(theta);
                    float v = 0.25f*(float)Math.sin(theta);
                    Vector2 offset = new Vector2(u,v);
                    mesh.uvs.add(new Vector2(oTop.clone().add(offset)));
                    mesh.uvs.add(new Vector2(oBottom.clone().add(offset)));
                }

                // side UV indices
                for (int h=0; h<2*n; h+=2) {
                    mesh.faces.get(h).uvs[0] = h+1;
                    mesh.faces.get(h).uvs[1] = h+2;
                    mesh.faces.get(h).uvs[2] = h+3;

                    mesh.faces.get(h+1).uvs[0] = h+3;
                    mesh.faces.get(h+1).uvs[1] = h+2;
                    mesh.faces.get(h+1).uvs[2] = h+4;
                }

                // cap UV indices
                for (int g=2*n; g<4*n; g+=2) {
                    if (g==4*n-2) {
                        mesh.faces.get(g).uvs[0] = 2*(n+1)+1;
                        mesh.faces.get(g).uvs[1] = g+5;
                        mesh.faces.get(g).uvs[2] = 2*(n+1)+3;

                        mesh.faces.get(g+1).uvs[0] = 2*(n+1)+2;
                        mesh.faces.get(g+1).uvs[1] = g+6;
                        mesh.faces.get(g+1).uvs[2] = 2*(n+1)+4;
                    } else {
                        mesh.faces.get(g).uvs[0] = 2*(n+1)+1;
                        mesh.faces.get(g).uvs[1] = g+5;
                        mesh.faces.get(g).uvs[2] = g+7;

                        mesh.faces.get(g+1).uvs[0] = 2*(n+1)+2;
                        mesh.faces.get(g+1).uvs[1] = g+6;
                        mesh.faces.get(g+1).uvs[2] = g+8;
                    }
                }

                // write to .obj file
                try {
                    mesh.writeOBJ(f);
                } catch (IOException e) {
                    System.out.println(".obj write failed");
                }
            } else if (geo.equals("sphere")){
                int n;
                int m;
                String f;
                if(args.length == 4){
                    n = Integer.parseInt(args[1]);
                    m = Integer.parseInt(args[2]);
                    f = args[3];
                } else if (args.length == 3){
                    n = Integer.parseInt(args[1]);
                    m = 16;
                    f = args[2];
                } else if (args.length == 2){
                    n = 4;
                    m = 4;
                    f = args[1];
                } else {
                    System.out.println("Please input a valid filename.");
                    return;
                }

                // top point
                mesh.positions.add(new Vector3((float)0,(float)1,(float)0));

                // middle points
                float degreeselevation = 180.0f / m;
                float dtheta = (float) degreeselevation * ((float)Math.PI/180.0f);
                float degreesrotation = 360.0f / n;
                float dphi = (float)degreesrotation * ((float)Math.PI/180.0f);

                for(int a=1; a<m; a++) {
                    float theta=a*dtheta;
                    for (float b = 0; b<n; b++) {
                        float phi = b*dphi;
                        float x = (float) Math.sin(theta) * (float) Math.sin(phi);
                        float y = (float) Math.cos(theta);
                        float z = (float) Math.sin(theta) * (float) Math.cos(phi);
                        mesh.positions.add(new Vector3(-x,y,-z));
                    }
                }

                // bottom point
                mesh.positions.add(new Vector3((float)0,(float)-1,(float)0));

                // top row of faces (top fan faces)
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

                // middle faces
                for (int p = 0; p < m-2; p++){
                    int down = p*n+1;
                    int up = (p+1)*n+1;
                    for (int q=1; q<(n+1); q++){
                        if (q != n){
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

                            face2.positions[0] = down+1;
                            face2.positions[1] = up;
                            face2.positions[2] = down+q+1;

                            face1.positions[0] = down+q;
                            face1.positions[1] = up+q;
                            face1.positions[2] = up+1;

                            mesh.faces.add(face2);
                            mesh.faces.add(face1);
                        }
                    }
                }

                int bottomCoord = mesh.positions.size();
                for (int j = bottomCoord-n; j < bottomCoord; j++){
                    if (j == (bottomCoord-1)){
                        OBJFace lastFace2 = new OBJFace(3,true,true);
                        lastFace2.positions[0]= bottomCoord;
                        lastFace2.positions[1]= bottomCoord - n;
                        lastFace2.positions[2]= bottomCoord - 1;
                        mesh.faces.add(lastFace2);
                    } else {
                        OBJFace bottomFanFace = new OBJFace(3, true, true);
                        bottomFanFace.positions[0]= bottomCoord;
                        bottomFanFace.positions[1]= j+1;
                        bottomFanFace.positions[2]= j;
                        mesh.faces.add(bottomFanFace);
                    }
                }

                // NORMALS
                for (int i=0; i<mesh.positions.size(); i++) {
                    mesh.normals.add((new Vector3(mesh.positions.get(i).x, mesh.positions.get(i).y, mesh.positions.get(i).z)).normalize());
                }

                for (int j=0; j<mesh.faces.size(); j++) {
                    mesh.faces.get(j).normals[0] = mesh.faces.get(j).positions[0];
                    mesh.faces.get(j).normals[1] = mesh.faces.get(j).positions[1];
                    mesh.faces.get(j).normals[2] = mesh.faces.get(j).positions[2];
                }

                // UVS
                float dv = 1.0f/m;
                float du = 1.0f/n;

                for (int j=0; j<=m; j++) {
                    if (j==0) {     // top UVs
                        for (int i=0; i<n; i++) {
                            mesh.uvs.add(new Vector2(i*du,1-j*dv));
                        }
                    } else if (j==m) {      // bottom UVs
                        for (int i=1; i<=n; i++) {
                            mesh.uvs.add(new Vector2(i*du,1-j*dv));
                        }
                    } else {        // middle UVs
                        for (int i=0; i<=n; i++) {
                            mesh.uvs.add(new Vector2(i*du,1-j*dv));
                        }
                    }
                }

                for (int j=0; j<m; j++) {
                    if (j==0) {     // top UVs
                        for (int i=0; i<n; i++) {
                            mesh.faces.get(i).uvs[0] = i+1;
                            mesh.faces.get(i).uvs[1] = n+i+1;
                            mesh.faces.get(i).uvs[2] = n+i+2;
                        }
                    }
                    else if (j==m-1) {      // bottom UVs
                        for (int i=0; i<n; i++) {
                            mesh.faces.get(n*2*(m-2)+n+i).uvs[0] = m*(n+1)+i;
                            mesh.faces.get(n*2*(m-2)+n+i).uvs[1] = j*(n+1)+i+1;
                            mesh.faces.get(n*2*(m-2)+n+i).uvs[2] = j*(n+1)+i;
                        }
                    } else {    // middle UVS
                        for (int i=0; i<2*n; i+=2) {
                            mesh.faces.get(n+((j-1)*2)*n+i).uvs[0] = j*(n+1)+(i/2)+1;;
                            mesh.faces.get(n+((j-1)*2)*n+i).uvs[1] = j*(n+1)+(i/2);
                            mesh.faces.get(n+((j-1)*2)*n+i).uvs[2] = (j+1)*(n+1)+(i/2)+1;

                            mesh.faces.get(n+((j-1)*2)*n+i+1).uvs[0] = j*(n+1)+(i/2);
                            mesh.faces.get(n+((j-1)*2)*n+i+1).uvs[1] = (j+1)*(n+1)+(i/2);
                            mesh.faces.get(n+((j-1)*2)*n+i+1).uvs[2] = (j+1)*(n+1)+(i/2)+1;
                        }
                    }
                }

                // write to .obj file
                try {
                    mesh.writeOBJ(f);
                } catch (IOException e) {
                    System.out.println(".obj write failed");
                }

            } else if (geo.equals("torus")) {
                int n;
                int m;
                String f;
                float littler;
                float bigR = 1.0f;
                if (args.length == 5){
                    n = Integer.parseInt(args[1]);
                    m = Integer.parseInt(args[2]);
                    littler = Float.parseFloat(args[3]);
                    f = args[4];
                } else if(args.length == 4){
                    n = Integer.parseInt(args[1]);
                    m = Integer.parseInt(args[2]);
                    littler = (float)0.25;
                    f = args[3];
                } else if (args.length == 3){
                    n = Integer.parseInt(args[1]);
                    m = 16;
                    littler = (float)0.25;
                    f = args[2];
                } else if (args.length == 2){
                    n = 32;
                    m = 16;
                    littler = (float)0.25;
                    f = args[1];
                } else {
                    System.out.println("Please input a valid filename.");
                    return;
                }

                float degreesAroundTorus = 360.0f/m;
                float degreesRing = 360.0f/n;
                float dphi = degreesAroundTorus * ((float)Math.PI/180.0f);
                float dtheta = degreesRing * ((float)Math.PI/180.0f);

                for (int a=0; a<n; a++){
                    float phi = a*dphi;
                    for(int b=0; b<n; b++){
                        float theta = b*dtheta;
                        float x = ((float)bigR + ((float)littler * (float)(Math.cos(phi)))) * (float)(Math.cos(theta));
                        float y = ((float)littler * ((float)Math.sin(phi)));
                        float z = ((float)bigR + ((float)littler * (float)(Math.cos(phi)))) * (float)(Math.sin(theta));
                        mesh.positions.add(new Vector3(x,y,z));

                        float xN = ((float)littler * (float)(Math.cos(phi))) * (float)(Math.cos(theta));
                        float yN = y;
                        float zN = ((float)littler * (float)(Math.cos(phi))) * (float)(Math.sin(theta));
                        mesh.normals.add((new Vector3(xN,yN,zN)).normalize());
                    }
                }

//                for (int i=0; i<mesh.positions.size(); i++) {
//                    System.out.print("v ");
//                    System.out.print(mesh.positions.get(i).x + " ");
//                    System.out.print(mesh.positions.get(i).y + " ");
//                    System.out.println(mesh.positions.get(i).z);
//                }

//                float dv = 1.0f/m;
//                float du = 1.0f/n;
//                for (int v=0; v<=m; v++) {
//                    for (int u=0; u<=n; u++) {
//                        mesh.uvs.add(new Vector2(u*du,v*dv));
//                    }
//                }

                for (int v = 0; v<m+1; v++){
                    float dv = (float)v/(float)m;
                    for (int u=n; u>0; u--){
                        float du = (float)u/(float)n;
                        mesh.uvs.add(new Vector2(du,dv));

                    }
                }

                for (int v = 0; v<m+1; v++) {
                    float dv = (float) v / (float) m;
                    mesh.uvs.add(new Vector2(0.0f, dv));
                }

//                for(int q=0; q<mesh.uvs.size(); q++){
//                    System.out.print("vt ");
//                    System.out.print(mesh.uvs.get(q).x + " ");
//                    System.out.println(mesh.uvs.get(q).y);
//                }

                // for(int d=1; d<m+1; d++){
                // 	for (int e; e<n+1; e++){
                // 		int fstV = (d-1)*()+(e-1);
                // 		int sndV = fstV + 1;
                // 		int trdV = fstV + n;
                // 		int fthV = trdV + 1;
                // 	}
                // }

//                for (int i=0; i<mesh.normals.size(); i++) {
//                    System.out.print("vn ");
//                    System.out.print(mesh.normals.get(i).x + " ");
//                    System.out.print(mesh.normals.get(i).y + " ");
//                    System.out.println(mesh.normals.get(i).z);
//                }



                for (int p = 0; p < m; p++){
                    int firstVert = (p*n) + 1;
                    for (int q=0; q<n; q++){
                        int fstV;
                        int sndV;
                        int trdV;
                        int fthV;
                        if(q == (n-1)){
                            if(p == m-1){
                                fstV = ((p)*(n))+ q ;
                                sndV = fstV - 1;
                                trdV = fstV + n;
                                fthV = trdV + 1;


                                OBJFace face3 = new OBJFace(3, true, true);
                                OBJFace face4 = new OBJFace(3, true, true);

                                face3.positions[0] = n;
                                face3.positions[1] = firstVert -(p*n);
                                face3.positions[2] = firstVert+n-1;

                                face4.positions[0] = firstVert-(p*n);
                                face4.positions[1] = firstVert;
                                face4.positions[2] = firstVert+n-1;

                                // //DO NOT CHANGE
                                mesh.faces.add(face3);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = fstV;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = trdV;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = fthV;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];

                                fstV = ((p)*(n))+(q) + m + n + 1;
                                sndV = fstV -n + 1;
                                trdV = fstV + n;
                                fthV = trdV -n;

                                mesh.faces.add(face4);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = firstVert+n-1;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = firstVert;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = firstVert+n-1;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];



                            } else {
                                fstV = ((p)*(n))+(q) +1;
                                sndV = fstV + 1;
                                trdV = fstV + n;
                                fthV = trdV + 1;

                                //DO NOT TOUCH


                                OBJFace face1 = new OBJFace(3, true, true);
                                OBJFace face2 = new OBJFace(3, true, true);

                                face1.positions[0] = firstVert;
                                face1.positions[1] = firstVert+ n-1;
                                face1.positions[2] = firstVert+ n;

                                face2.positions[0] = firstVert+n;
                                face2.positions[1] = firstVert+n-1;
                                face2.positions[2] = firstVert+(2*n)-1;

                                mesh.faces.add(face1);
                                mesh.faces.add(face2);

//                                mesh.faces.add(face1);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = firstVert + n;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = firstVert + n-1;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = firstVert + (2*n) ;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];
//
//                                mesh.faces.add(face2);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = firstVert+(2*n);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = firstVert+n-1;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = firstVert+(2*n)+1;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];
                            }

                        } else {

                            if(p==m-1){
                                fstV = ((p)*(n))+(q) ;
                                sndV = fstV + 1;
                                trdV = fstV + n;
                                fthV = trdV + 1;
                                OBJFace face3 = new OBJFace(3, true, true);
                                OBJFace face4 = new OBJFace(3, true, true);

                                face3.positions[0] = firstVert+n-q-1;
                                face3.positions[1] = firstVert+n-q-2;
                                face3.positions[2] = n-q;

                                face4.positions[0] = firstVert+n-q-2;
                                face4.positions[1] = n-q-1;
                                face4.positions[2] = n-q;

                                mesh.faces.add(face3);
                                mesh.faces.add(face4);

//                                mesh.faces.add(face3);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = fstV;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = trdV;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = fthV;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];
//
//                                mesh.faces.add(face4);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = fthV;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = sndV;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = fstV;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];



                            } else {
                                fstV = ((p)*(n))+(q) +1;
                                sndV = fstV + 1;
                                trdV = fstV + n;
                                fthV = trdV + 1;
                                //DO NOT TOUCH
                                OBJFace face1 = new OBJFace(3, true, true);
                                OBJFace face2 = new OBJFace(3, true, true);

                                face1.positions[0] = firstVert + q + 1;
                                face1.positions[1] = firstVert + q;
                                face1.positions[2] = firstVert + q + n + 1;

                                face2.positions[0] = firstVert + q;
                                face2.positions[1] = firstVert + q + n;
                                face2.positions[2] = firstVert + q + n + 1;

                                mesh.faces.add(face1);
                                mesh.faces.add(face2);

//                                mesh.faces.add(face1);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = firstVert+q+1;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = firstVert+q;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = firstVert+q+n+1;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];
//
//                                mesh.faces.add(face2);
//                                mesh.faces.get(mesh.faces.size()-1).uvs[0] = firstVert+q;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[1] = firstVert+q+n;
//                                mesh.faces.get(mesh.faces.size()-1).uvs[2] = firstVert+q+n+1;
//                                mesh.faces.get(mesh.faces.size()-1).normals[0] = mesh.faces.get(mesh.faces.size()-1).positions[0];
//                                mesh.faces.get(mesh.faces.size()-1).normals[1] = mesh.faces.get(mesh.faces.size()-1).positions[1];
//                                mesh.faces.get(mesh.faces.size()-1).normals[2] = mesh.faces.get(mesh.faces.size()-1).positions[2];


                            }
                        }

                    }
                }

                for (int j=0; j<m; j++) {
                    if (j==m-1) {
                        for (int i=0; i<2*n; i+=2) {
                            int faceIdx = 2*n*j+i;
                            if (i==2*n-2) {
                                System.out.println("Setting normals");
                                System.out.println(n*(m+1)+j+2);
                                mesh.faces.get(faceIdx).uvs[0] = n*(m+1)+j+2;
                                mesh.faces.get(faceIdx).uvs[1] = n*(m+1)+j+1;
                                mesh.faces.get(faceIdx).uvs[2] = mesh.faces.get(faceIdx).positions[2];
                                mesh.faces.get(faceIdx).normals[0] = mesh.faces.get(faceIdx).positions[0];
                                mesh.faces.get(faceIdx).normals[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).normals[2] = mesh.faces.get(faceIdx).positions[2];

                                mesh.faces.get(faceIdx+1).uvs[0] = n*(m+1);
                                mesh.faces.get(faceIdx+1).uvs[1] = n*(m+1)+j+2;
                                mesh.faces.get(faceIdx+1).uvs[2] = mesh.faces.get(faceIdx+1).positions[2];
                                mesh.faces.get(faceIdx+1).normals[0] = mesh.faces.get(faceIdx+1).positions[0];
                                mesh.faces.get(faceIdx+1).normals[1] = mesh.faces.get(faceIdx+1).positions[1];
                                mesh.faces.get(faceIdx+1).normals[2] = mesh.faces.get(faceIdx+1).positions[2];
                            } else {
                                System.out.println("Setting normals");
                                System.out.println(faceIdx);
                                System.out.println(i);
                                System.out.println("");
                                mesh.faces.get(faceIdx).uvs[0] = mesh.faces.get(faceIdx).positions[0];
                                mesh.faces.get(faceIdx).uvs[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).uvs[2] = n*(m+1)-i/2;
                                mesh.faces.get(faceIdx).normals[0] = mesh.faces.get(faceIdx).positions[0];
                                mesh.faces.get(faceIdx).normals[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).normals[2] = mesh.faces.get(faceIdx).positions[2];

                                mesh.faces.get(faceIdx+1).uvs[0] = mesh.faces.get(faceIdx+1).positions[0];
                                mesh.faces.get(faceIdx+1).uvs[1] = n*(m+1)-i/2-1;
                                mesh.faces.get(faceIdx+1).uvs[2] = n*(m+1)-i/2;
                                mesh.faces.get(faceIdx+1).normals[0] = mesh.faces.get(faceIdx+1).positions[0];
                                mesh.faces.get(faceIdx+1).normals[1] = mesh.faces.get(faceIdx+1).positions[1];
                                mesh.faces.get(faceIdx+1).normals[2] = mesh.faces.get(faceIdx+1).positions[2];
                            }
                        }
                    } else {
                        for (int i=0; i<2*n; i+=2) {
                            int faceIdx = 2*n*j+i;
                            if (i==2*n-2) {
                                System.out.println("Setting normals");
                                mesh.faces.get(faceIdx).uvs[0] = n*(m+1)+j+1;
                                mesh.faces.get(faceIdx).uvs[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).uvs[2] = n*(m+1)+j+2;
                                mesh.faces.get(faceIdx).normals[0] = mesh.faces.get(faceIdx).positions[0];
                                mesh.faces.get(faceIdx).normals[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).normals[2] = mesh.faces.get(faceIdx).positions[2];

                                mesh.faces.get(faceIdx+1).uvs[0] = n*(m+1)+j+2;
                                mesh.faces.get(faceIdx+1).uvs[1] = mesh.faces.get(faceIdx+1).positions[1];
                                mesh.faces.get(faceIdx+1).uvs[2] = mesh.faces.get(faceIdx+1).positions[2];
                                mesh.faces.get(faceIdx+1).normals[0] = mesh.faces.get(faceIdx+1).positions[0];
                                mesh.faces.get(faceIdx+1).normals[1] = mesh.faces.get(faceIdx+1).positions[1];
                                mesh.faces.get(faceIdx+1).normals[2] = mesh.faces.get(faceIdx+1).positions[2];
                            } else {
                                System.out.println("Setting normals");
                                mesh.faces.get(faceIdx).uvs[0] = mesh.faces.get(faceIdx).positions[0];
                                mesh.faces.get(faceIdx).uvs[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).uvs[2] = mesh.faces.get(faceIdx).positions[2];
                                mesh.faces.get(faceIdx).normals[0] = mesh.faces.get(faceIdx).positions[0];
                                mesh.faces.get(faceIdx).normals[1] = mesh.faces.get(faceIdx).positions[1];
                                mesh.faces.get(faceIdx).normals[2] = mesh.faces.get(faceIdx).positions[2];

                                mesh.faces.get(faceIdx+1).uvs[0] = mesh.faces.get(faceIdx+1).positions[0];
                                mesh.faces.get(faceIdx+1).uvs[1] = mesh.faces.get(faceIdx+1).positions[1];
                                mesh.faces.get(faceIdx+1).uvs[2] = mesh.faces.get(faceIdx+1).positions[2];
                                mesh.faces.get(faceIdx+1).normals[0] = mesh.faces.get(faceIdx+1).positions[0];
                                mesh.faces.get(faceIdx+1).normals[1] = mesh.faces.get(faceIdx+1).positions[1];
                                mesh.faces.get(faceIdx+1).normals[2] = mesh.faces.get(faceIdx+1).positions[2];
                            }
                        }
                    }
                }





//                for(int k=0; k<mesh.faces.size(); k++){
//                    System.out.print("f ");
//                    System.out.print(mesh.faces.get(k).positions[0] + "/" + mesh.faces.get(k).uvs[0] + "/" + mesh.faces.get(k).normals[0] + " ");
//                    System.out.print(mesh.faces.get(k).positions[1] + "/"+ mesh.faces.get(k).uvs[1] +"/" + mesh.faces.get(k).normals[1] + " ");
//                    System.out.println(mesh.faces.get(k).positions[2] + "/"+ mesh.faces.get(k).uvs[2] +"/" + mesh.faces.get(k).normals[2]);
//                }

                System.out.println("# FACES: " + mesh.faces.size());


                // write to .obj file
                try {
                    mesh.writeOBJ("C:\\Users\\Altair\\Documents\\Graphics\\torus.obj");
                } catch (IOException e) {
                    System.out.println(".obj write failed");
                }

            } else {        // usage 2
                String in = args[0];
                String out;
                if (args.length == 2) {
                    out = args[1];
                } else {
                    System.out.println("Please provide an output filename.");
                    return;
                }

                // parse the input file
                try {
                    mesh.parseOBJ(in);
                } catch (IOException e) {
                    System.out.println("Input parse failed.");
                }

                // calculate face normals
                ArrayList<Vector3> faceNormals = new ArrayList<Vector3>();
                for (int h=0; h<mesh.faces.size(); h++) {
                    OBJFace face = mesh.faces.get(h);
                    Vector3 v1 = mesh.positions.get(face.positions[0]-1);
                    Vector3 v2 = mesh.positions.get(face.positions[1]-1);
                    Vector3 v3 = mesh.positions.get(face.positions[2]-1);

                    Vector3 n = (v2.clone().sub(v1)).cross(v3.clone().sub(v1));       // (v2-v1) x (v3-v1): cross product btwn 2 planar lines gives normal vector
                    faceNormals.add(n);
                }

                // zero out all vertex normals
                mesh.normals.clear();
                for (int i=0; i<mesh.positions.size(); i++) {
                    mesh.normals.add(new Vector3());
                }

                // for each face, add the face's normal to its 3 vertices' normals
                for (int j=0; j<mesh.faces.size(); j++) {
                    Vector3 normal = faceNormals.get(j);
                    for (int k=0; k<3; k++) {
                        int idx = mesh.faces.get(j).positions[k];
                        mesh.normals.get(idx-1).add(normal);
                    }
                }

                // normalize the normals
                for (int k=0; k<mesh.positions.size(); k++) {
                    mesh.normals.get(k).normalize();
                }

                // set normals for each face
                for (int g=0; g<mesh.faces.size(); g++) {
                    OBJFace face = mesh.faces.get(g);
                    face.normals = new int[3];
                    face.normals[0] = face.positions[0];
                    face.normals[1] = face.positions[1];
                    face.normals[2] = face.positions[2];
                }

                // write to output
                try {
                    mesh.writeOBJ(out);
                } catch (IOException e) {
                    System.out.println(".obj write failed");
                }
            }
        }
	}
}
