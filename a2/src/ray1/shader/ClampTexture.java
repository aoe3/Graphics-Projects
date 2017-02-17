package ray1.shader;

import egl.math.Color;
import ray1.shader.Texture;
import egl.math.Colorf;
import egl.math.Vector2;

/**
 * A Texture class that treats UV-coordinates outside the [0.0, 1.0] range as if they
 * were at the nearest image boundary.
 * @author eschweic zz335
 *
 */
public class ClampTexture extends Texture {

	public Colorf getTexColor(Vector2 texCoord) {
		if (image == null) {
			System.err.println("Warning: Texture uninitialized!");
			return new Colorf();
		}
				
		// TODO#A2 Fill in this function.
		// 1) Convert the input texture coordinates to integer pixel coordinates. Adding 0.5
		//    before casting a double to an int gives better nearest-pixel rounding.
		// 2) Clamp the resulting coordinates to the image boundary.
		// 3) Create a Color object based on the pixel coordinate (use Color.fromIntRGB
		//    and the image object from the Texture class), convert it to a Colord, and return it.
		// NOTE: By convention, UV coordinates specify the lower-left corner of the image as the
		//    origin, but the ImageBuffer class specifies the upper-left corner as the origin.

		double inX = texCoord.x;
		double inY = texCoord.y;

		int nX = image.getWidth();
		int nY = image.getHeight();

		int intInX = (int)((inX * nX) - 0.5);
		int intInY = (int)((inY * nY) - 0.5);

		int outnX;
		int outnY;

		if(intInX > nX){
			outnX = nX;
		} else if (intInX < 0) {
			outnX = 0;
		} else {
			outnX = intInX;
		}

		if(intInY > nY){
			outnY = nY;
		} else if (intInY < 0) {
			outnY = 0;
		} else {
			outnY = intInY;
		}

		int intColor = image.getRGB(outnX, image.getHeight()-outnY);
		Color c = Color.fromIntRGB(intColor);
		return new Colorf(c);
	}

}
