package lights;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import core.Color3f;
import core.Scene;
import primitives.Point3f;
import primitives.Ray;
import primitives.Vector3f;

public class DirectLight extends Light {
	
	public static final String DIRECTION="dir";
	public static final String RGB="rgb";
	public DirectLight(Node node)
	{
		Element directlight = (Element)node;
		dir = new Vector3f(directlight.getAttribute(DIRECTION)).Normalize();
		rgb = new Color3f(directlight.getAttribute(RGB));
		
	}
	
	private Vector3f dir;
	private Color3f rgb;

	public void print(String prefix) {
		System.out.println(prefix+"DirectLight:");
		dir.print(prefix+"\t");
		rgb.print(prefix+"\t");
	}

	public Color3f SampleLight(Scene scene, Point3f p, Vector3f wi, float rayEpsilon) {
		Ray ray = new Ray(p, dir.Scale(-1.f), rayEpsilon, Float.MAX_VALUE);
		
		if (scene.Intersect(ray)) {
			return new Color3f(0.f);
		}

		wi.x = -dir.x;
		wi.y = -dir.y;
		wi.z = -dir.z;

		float length = wi.length();
		
		wi.x /= length;
		wi.y /= length;
		wi.z /= length;
		
		return rgb;
	}
}
