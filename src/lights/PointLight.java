package lights;

import javax.naming.spi.DirStateFactory.Result;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import core.Color3f;
import core.Scene;
import primitives.Intersection;
import primitives.Point3f;
import primitives.Ray;
import primitives.Vector3f;

public class PointLight extends Light {
	public static final String POSITION = "pos";
	public static final String RGB = "rgb";

	public PointLight(Node node) {
		Element pointlight = (Element) node;

		p = new Point3f(pointlight.getAttribute(POSITION));
		rgb = new Color3f(pointlight.getAttribute(RGB));
	}

	public void print(String prefix) {
		System.out.println(prefix + "PointLight:");
		p.print(prefix + "\t");
		rgb.print(prefix + "\t");
	}

	private Point3f p;
	private Color3f rgb;

	public Color3f SampleLight(Scene scene, Point3f p, Vector3f wi, float rayEpsilon) {
		Color3f li = new Color3f(rgb);
		
		//p.x = 1.f;
		
		wi.x = this.p.x - p.x;
		wi.y = this.p.y - p.y;
		wi.z = this.p.z - p.z;

		float length = wi.length();
		
		wi.x /= length;
		wi.y /= length;
		wi.z /= length;
		
		Intersection inter = new Intersection();
		Ray ray = new Ray(p, new Vector3f(p,this.p), rayEpsilon, new Vector3f(p, this.p).length());
		
		while (true) {
			if (scene.Intersect(ray, inter)) {
				if (inter.material.refract.isBlack()) {
					return new Color3f(0.f);
				}
				else
				{
					li = li.Scale(inter.material.refract);
					ray = new Ray(inter.p, new Vector3f(inter.p,this.p), rayEpsilon, new Vector3f(inter.p, this.p).length());
				}
			}
			else {
				return li;
			}
		}

	}
}
