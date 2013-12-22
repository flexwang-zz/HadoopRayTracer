package lights;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import primitives.Intersection;
import primitives.Point3f;
import primitives.Ray;
import primitives.Vector3f;
import shapes.Shape;
import shapes.Sphere;
import shapes.Triangle;
import core.Color3f;
import core.Scene;

public class AreaLight extends Light {

	public AreaLight(Node node, ArrayList<Shape> objs) {
		NodeList nodes = node.getChildNodes();
		
		rgb = new Color3f(((Element) node).getAttribute(RGB));
		for (int i=0,size=nodes.getLength(); i<size; i++) {
			if (nodes.item(i) instanceof Element) {
				shape = Shape.ShapeFactory(nodes.item(i), objs.size()+1);
				shape.arealight = rgb;
				objs.add(shape);
				break;
			}
		}
	}

	@Override
	public void print(String prefix) {
		// TODO Auto-generated method stub
		System.out.println(prefix+"Arealight:");
		rgb.print(prefix+"\t");
		shape.print(prefix+"\t");
	}

	@Override
	public Color3f SampleLight(Scene scene, Point3f p, Vector3f wi,
			float rayEpsilon) {

		if (shape instanceof Sphere) {
			Sphere sphere = (Sphere) shape;
			Color3f li = new Color3f(rgb);
			Point3f o = sphere.o;

			Intersection inter = new Intersection();
			Ray ray = new Ray(p, new Vector3f(p, o), rayEpsilon, new Vector3f(
					p, o).length());

			while (true) {
				if (scene.Intersect(ray, inter) && inter.id != shape.id) {

					if (inter.material.refract.isBlack()) {
						return new Color3f(0.f);
					} else {
						li = li.Scale(inter.material.refract);
						ray = new Ray(inter.p, new Vector3f(inter.p, o),
								inter.rayEpsilon,
								new Vector3f(inter.p, o).length());
					}

				} else {
					wi.x = o.x - p.x;
					wi.y = o.y - p.y;
					wi.z = o.z - p.z;

					float length = wi.length();

					wi.x /= length;
					wi.y /= length;
					wi.z /= length;
					return li;
				}
			}
		} else if (shape instanceof Triangle) {
			final int nsampler = 12;
			int nsample = 0;
			Triangle tri = (Triangle) shape;
			Vector3f v1 = new Vector3f(tri.p1, tri.p2);
			Vector3f v2 = new Vector3f(tri.p1, tri.p3);
			Color3f li = new Color3f(0.f);
			// nsampler x nsampler sample
			for (int j = 0; j < nsampler; j++) {
				for (int k = 0; k < nsampler; k++) {
					float t1 = 1.f / (float) nsampler * j;
					float t2 = 1.f / (float) nsampler * k;

					if ((t1 + t2) >= 1.f) {
						continue;
					}
					Color3f thisli = new Color3f(rgb);
					nsample++;

					Point3f s = tri.p1.Translate(v1.Scale(t1)).Translate(
							v2.Scale(t2));
					//s.print(nsample+"");
					Intersection inter = new Intersection();
					Ray ray = new Ray(p, new Vector3f(p, s), rayEpsilon,
							new Vector3f(p, s).length());
					while (true) {
						if (scene.Intersect(ray, inter) && inter.id != shape.id) {
							//System.out.println(nsample);
							if (inter.material.refract.isBlack()) {
								break;
							} else {
								thisli = thisli.Scale(inter.material.refract);
								ray = new Ray(inter.p,
										new Vector3f(inter.p, s),
										inter.rayEpsilon, new Vector3f(inter.p,
												s).length());
							}
						}
						else {
							li = li.Add(thisli);
							break;
						}
					}
				}
			}
			
			if (li.isBlack()) {
				return li;
			}
			else {
				Point3f o = new Point3f(0.f, 0.f, 0.f);
				o = o.Add(tri.p1).Add(tri.p2).Add(tri.p3).Scale(1.f/3.f);
				wi.x = o.x - p.x;
				wi.y = o.y - p.y;
				wi.z = o.z - p.z;
				float length = wi.length();
				
				wi.x /= length;
				wi.y /= length;
				wi.z /= length;
				return li.Scale(1.f/(float)nsample);
			}
		}
		return rgb;
	}

	private Shape shape;
	public Color3f rgb;
}
