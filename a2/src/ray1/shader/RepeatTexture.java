package ray1.shader;

import egl.math.Color;
import ray1.shader.Texture;
import egl.math.Colorf;
import egl.math.Vector2;

import java.awt.image.BufferedImage;

/**
 * A Texture class that repeats the texture image as necessary for UV-coordinates
 * outside the [0.0, 1.0] range.
 * 
 * @author eschweic zz335
 *
 */
public class RepeatTexture extends Texture {

	public Colorf getTexColor(Vector2 texCoord) {
		if (image == null) {
			System.err.println("Warning: Texture uninitialized!");
			return new Colorf();
		}
				
		// TODO#A2 Fill in this function.
		// 1) Convert the input texture coordinates to integer pixel coordinates. Adding 0.5
		//    before casting a double to an int gives better nearest-pixel rounding.
		// 2) If these coordinates are outside the image boundaries, modify them to read from
		//    the correct pixel on the image to give a repeating-tile effect.
		// 3) Create a Color object based on the pixel coordinate (use Color.fromIntRGB
		//    and the image object from the Texture class), convert it to a Colorf, and return it.
		// NOTE: By convention, UV coordinates specify the lower-left corner of the image as the
		//    origin, but the ImageBuffer class specifies the upper-left corner as the origin.
		double inX = texCoord.x;
		System.out.println("inX is " + inX);
		double inY = texCoord.y;
		System.out.println("inY is " + inY);
		
		int nX = image.getWidth();
		int nY = image.getHeight();
		
		int intInX = (int)((inX * nX) - 0.5);
		int intInY = (int)((inY * nY) - 0.5);
		
		int outnX;
		int outnY;
		
		if(intInX > nX){
			outnX = (intInX % nX);
		} else if (intInX < 0) {
			outnX = nX - (-intInX % nX);
		} else {
			outnX = intInX;
		}
		System.out.println("intInX is "+intInX);
		System.out.println("intInY is "+intInY);
		
		if(intInY > nY){
			outnY = (intInY % nY);
		} else if (intInY < 0) {
			outnY = nY - (-intInY % nY);
		} else {
			outnY = intInY;
		}
		System.out.println("nX is "+nX);
		System.out.println("outnX is "+outnX);
		System.out.println("nY is "+nY);
		System.out.println("outnY is "+outnY);
		System.out.println("");

		int intColor = image.getRGB(outnX, image.getHeight()-outnY);
		Color c = Color.fromIntRGB(intColor);
//		System.out.println(new Colorf(c));
		return new Colorf(c);
	}
}
