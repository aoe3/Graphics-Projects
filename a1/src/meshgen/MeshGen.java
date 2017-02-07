package meshgen;
import java.util.ArrayList;
import math.Vector3;

public class MeshGen {
	public static void main(String[] args) {
	    if (args.length > 0) {
            String geo = args[0];
            if (geo.equals("cylinder")) {
                ArrayList<Vector3> positions = new ArrayList<Vector3>();
                int n = Integer.parseInt(args[1]);
                float dtheta = 360.0f / n;
                // top face, y=1
                float y = 1.0f;

                for (float theta=0.0f; theta<360.0f; theta=theta+dtheta) {
                    float x = (float)Math.sin(theta);
                    float z = (float)Math.cos(theta);
                    positions.add(new Vector3(x,y,z));
                }

                for (int i=0; i<positions.size(); i++) {
                    System.out.println(positions.get(i));
                }

            }
        }
	}
}