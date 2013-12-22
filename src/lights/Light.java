package lights;

import java.util.ArrayList;

import org.w3c.dom.Node;

import core.Color3f;
import core.Scene;
import primitives.Point3f;
import primitives.Vector3f;
import shapes.Shape;

public abstract class Light {
	public static final String POINTLIGHT = "pointlight";
	public static final String DIRECTLIGHT = "directlight";
	private static final String AREALIGHT = "arealight";
	public static final String RGB="rgb";
	
	public static Light LightFactory(Node node, ArrayList<Shape> objs) {
		String nodename = node.getNodeName();

		if (nodename.equals(POINTLIGHT)) {
			return new PointLight(node);
		} else if (nodename.equals(DIRECTLIGHT)) {
			return new DirectLight(node);
		} else if (nodename.equals(AREALIGHT)) {
			return new AreaLight(node, objs);
		}
		return null;
	}

	public abstract void print(String string);
	public abstract Color3f SampleLight(Scene scene, Point3f p, Vector3f wi, float rayEpsilon);
}
