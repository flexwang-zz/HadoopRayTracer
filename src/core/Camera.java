package core;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import primitives.Point3f;
import primitives.Ray;
import primitives.Vector3f;


public class Camera {
	// namespace in scene.xml
	public static final String CAMERA_EYE = "eye";
	public static final String CAMERA_LOOK = "look";
	public static final String CAMERA_UP = "up";
	public static final String CAMERA_ANGLE = "angle";
	public static final String CAMERA_ZNEAR = "znear";
	public static final String CAMERA_ZFAR = "zfar";
	public static final String CAMERA_XRES = "xres";
	public static final String CAMERA_YRES = "yres";
	public static final String CAMERA_MAXDEPTH = "maxdepth";

	private Point3f eye;
	private Point3f look;
	private Vector3f up;
	private float znear, zfar;
	private float angle;

	private int xres, yres;
	
	private Vector3f xdir, ydir;
	private float dx, dy;
	private Point3f o;	//left-top of the view plane
	
	public int maxdepth;

	public Camera(Node cameranode) {
		Element camera = (Element) cameranode;

		eye = new Point3f(camera.getAttribute(CAMERA_EYE));
		look = new Point3f(camera.getAttribute(CAMERA_LOOK));
		up = new Vector3f(camera.getAttribute(CAMERA_UP));

		angle = (float) (Float.parseFloat(camera.getAttribute(CAMERA_ANGLE))/180.f*Math.PI);
		znear = Float.parseFloat(camera.getAttribute(CAMERA_ZNEAR));
		zfar = Float.parseFloat(camera.getAttribute(CAMERA_ZFAR));
		xres = Integer.parseInt(camera.getAttribute(CAMERA_XRES));
		yres = Integer.parseInt(camera.getAttribute(CAMERA_YRES));
		maxdepth = Integer.parseInt(camera.getAttribute(CAMERA_MAXDEPTH));

		Vector3f dir = new Vector3f(eye, look);

		xdir = dir.Cross(up).Normalize();
		ydir = up.Normalize();
		
		float xrange = (float) (dir.length()*Math.tan(angle/2.f));
		float yrange = xrange/xres*yres;
		
		dx = xrange/xres*2.f;
		dy = yrange/yres*2.f;
		
		o = look.Translate(xdir.Scale(-xrange))
				.Translate(ydir.Scale(-yrange));
	}

	public int getXRes() {
		return xres;
	}

	public int getYRes() {
		return yres;
	}

	public Ray GenerateRay(int x, int y) {
		y = yres-y;
		Point3f to = o.Translate(xdir.Scale(dx*x)).Translate(ydir.Scale(dy*y));
		return new Ray(new Point3f(eye), new Vector3f(eye, to));

	}

	public void print(String prefix) {
		System.out.println(prefix + "Camera:");
		// eye
		System.out.print(prefix + "\t" + "eye: ");
		eye.print();

		// look
		System.out.print(prefix + "\tlook: ");
		look.print();

		// up
		System.out.print(prefix + "\tup: ");
		up.print("");

		// angle
		System.out.println(prefix + "\tAngle: " + angle);

		// znear zfar
		System.out.println(prefix + "\tZnear: " + znear + " Zfar: " + zfar);

		// xres yres
		System.out.println(prefix + "\txres: " + xres + " yres: " + yres);
	}
}
