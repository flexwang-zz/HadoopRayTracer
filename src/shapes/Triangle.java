package shapes;

import materials.Material;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import primitives.Intersection;
import primitives.Point3f;
import primitives.Ray;
import primitives.Vector3f;

public class Triangle extends Shape {
	public static final String P1 = "p1";
	public static final String P2 = "p2";
	public static final String P3 = "p3";
	
	public Point3f p1, p2, p3;
	private Vector3f n;

	@Override
	public boolean Intersect(Ray ray) {
		Vector3f e1 = new Vector3f(p1, p2);
		Vector3f e2 = new Vector3f(p1, p3);
		Vector3f s1 = ray.d.Cross(e2);
		float divisor = s1.Dot(e1);

		if (divisor <= Float.MIN_VALUE)
			return false;
		float invDivisor = 1.f / divisor;

		// Compute first barycentric coordinate
		Vector3f d = new Vector3f(p1, ray.o);
		float b1 = d.Dot(s1) * invDivisor;
		if (b1 < 0. || b1 > 1.)
			return false;

		// Compute second barycentric coordinate
		Vector3f s2 = d.Cross(e1);
		float b2 = ray.d.Dot(s2) * invDivisor;
		if (b2 < 0. || b1 + b2 > 1.)
			return false;

		// Compute _t_ to intersection point
		float t = e2.Dot(s2) * invDivisor;
		if (t < ray.mint || t > ray.maxt)
			return false;

		return true;
	}

	@Override
	public boolean Intersect(Ray ray, float[] tHit, Intersection intersect) {

		Vector3f e1 = new Vector3f(p1, p2);
		Vector3f e2 = new Vector3f(p1, p3);
		Vector3f s1 = ray.d.Cross(e2);
		float divisor = s1.Dot(e1);

		if (divisor <= Float.MIN_VALUE)
			return false;
		float invDivisor = 1.f / divisor;

		Vector3f d = new Vector3f(p1, ray.o);
		float b1 = d.Dot(s1) * invDivisor;
		if (b1 < 0. || b1 > 1.)
			return false;

		Vector3f s2 = d.Cross(e1);
		float b2 = ray.d.Dot(s2) * invDivisor;
		if (b2 < 0. || b1 + b2 > 1.)
			return false;

		float t = e2.Dot(s2) * invDivisor;
		if (t < ray.mint || t > ray.maxt)
			return false;

		tHit[0] = t;
		Point3f hitpoint = ray.getPoint(tHit[0]);
		intersect.p = hitpoint;
		intersect.n = n;
		intersect.material = material;
		intersect.rayEpsilon = 1e-3f * tHit[0];
		intersect.id = id;
		intersect.arealight = arealight;
		
		return true;
	}

	public void print(String prefix) {
		System.out.println(prefix+"Triangle");
		System.out.println(prefix+"\tid: "+id);
		p1.print(prefix+"\tp1: ");
		p2.print(prefix+"\tp2: ");
		p3.print(prefix+"\tp3: ");
		n.print(prefix+"\tn: ");
		material.print(prefix+"\t");
	}
	
	public Triangle(Node triNode) {
		Element tri = (Element) triNode;

		p1 = new Point3f(tri.getAttribute(P1));
		p2 = new Point3f(tri.getAttribute(P2));
		p3 = new Point3f(tri.getAttribute(P3));

		n = new Vector3f(p1,p2).Cross(new Vector3f(p1,p3)).Normalize();
		
		String diffuse = tri.getAttribute(DIFFUSE);
		String specular = tri.getAttribute(SPECULAR);
		String shiness = tri.getAttribute(SHINESS);
		String refract = tri.getAttribute(REFRACT);
		String index = tri.getAttribute(INDEX);
		material = new Material(diffuse, specular, shiness, refract, index);
	}

}
