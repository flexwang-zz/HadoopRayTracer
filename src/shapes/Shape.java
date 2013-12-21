package shapes;

import materials.Material;

import org.w3c.dom.Node;

import core.Color3f;
import primitives.Intersection;
import primitives.Ray;

public abstract class Shape {
	public static final String SPHERE = "sphere";
	public static final String TRIANGLE = "triangle";
	
	public static final String DIFFUSE = "diffuse";
	public static final String SPECULAR = "specular";
	public static final String SHINESS = "shiness";
	public static final String REFRACT = "refract";
	public static final String INDEX = "index";
	
	public Material material;

	public abstract boolean Intersect(Ray r);
	public abstract boolean Intersect(Ray r, float t[], Intersection intersect);

	public abstract void print(String prefix);
	
	public static Shape ShapeFactory(Node node) {
		String nodename = node.getNodeName();
		if (nodename.equals(Shape.SPHERE)) {
			return new Sphere(node);
		}
		else if (nodename.equals(Shape.TRIANGLE)) {
			return new Triangle(node);
		}
		
		return null;
	}
}
