package lights;

import org.w3c.dom.Node;

import core.Color3f;
import core.Scene;
import primitives.Point3f;
import primitives.Vector3f;

public abstract class Light {
	public static final String POINTLIGHT = "pointlight";
	public static final String DIRECTLIGHT = "directlight";

	public static Light LightFactory(Node node) {
		String nodename = node.getNodeName();

		if (nodename.equals(POINTLIGHT)) {
			return new PointLight(node);
		} else if (nodename.equals(DIRECTLIGHT)) {
			return new DirectLight(node);
		}
		return null;
	}

	public abstract void print(String string);
	public abstract Color3f SampleLight(Scene scene, Point3f p, Vector3f wi, float rayEpsilon);
}
