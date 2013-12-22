package shapes;

import materials.Material;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import primitives.Intersection;
import primitives.Point3f;
import primitives.Ray;
import primitives.Vector3f;

public class Sphere extends Shape {
	public static final String POSITION = "pos";
	public static final String RADIUS = "radius";

	public Point3f o;
	public float radius;

	@Override
	public boolean Intersect(Ray r) {
		float phi;
		Point3f phit;
		// Transform _Ray_ to object space
		Ray ray;
		ray = new Ray(r);
		ray.o = ray.o.Translate(-o.x, -o.y, -o.z);

		// Compute quadratic sphere coefficients
		float A = ray.d.x * ray.d.x + ray.d.y * ray.d.y + ray.d.z * ray.d.z;
		float B = 2 * (ray.d.x * ray.o.x + ray.d.y * ray.o.y + ray.d.z
				* ray.o.z);
		float C = ray.o.x * ray.o.x + ray.o.y * ray.o.y + ray.o.z * ray.o.z
				- radius * radius;

		// Solve quadratic equation for _t_ values
		float[] t = new float[2];
		if (!Quadratic(A, B, C, t))
			return false;

		// Compute intersection distance along ray
		if (t[0] > ray.maxt || t[1] < ray.mint)
			return false;
		float thit = t[0];
		if (t[0] < ray.mint) {
			thit = t[1];
			if (thit > ray.maxt)
				return false;
		}
		
		return true;
	}

	@Override
	public boolean Intersect(Ray r, float[] tHit, Intersection intersect) {
		float phi;
		Point3f phit;
		// Transform _Ray_ to object space
		Ray ray;
		ray = new Ray(r);
		ray.o = ray.o.Translate(-o.x, -o.y, -o.z);

		// Compute quadratic sphere coefficients
		float A = ray.d.x * ray.d.x + ray.d.y * ray.d.y + ray.d.z * ray.d.z;
		float B = 2 * (ray.d.x * ray.o.x + ray.d.y * ray.o.y + ray.d.z
				* ray.o.z);
		float C = ray.o.x * ray.o.x + ray.o.y * ray.o.y + ray.o.z * ray.o.z
				- radius * radius;

		// Solve quadratic equation for _t_ values
		float[] t = new float[2];
		if (!Quadratic(A, B, C, t))
			return false;

		// Compute intersection distance along ray
		if (t[0] > ray.maxt || t[1] < ray.mint)
			return false;
		float thit = t[0];
		if (t[0] < ray.mint) {
			thit = t[1];
			if (thit > ray.maxt)
				return false;
		}

		// Compute sphere hit position
		phit = ray.getPoint(thit);
		if (phit.x == 0.f && phit.y == 0.f)
			phit.x = 1e-5f * radius;
		phi = (float) Math.atan2(phit.y, phit.x);
		if (phi < 0.)
			phi += 2.f * Math.PI;

		// Update _tHit_ for quadric intersection
		tHit[0] = thit;
		
		Point3f hitpoint = ray.getPoint(tHit[0]);
		
		intersect.p = hitpoint;
		intersect.p = intersect.p.Translate(o.x, o.y, o.z);
		intersect.n = new Vector3f(this.o, intersect.p).Normalize();
		intersect.material = material;
		intersect.rayEpsilon = tHit[0]*5e-4f;
		intersect.id = id;
		intersect.arealight = arealight;
		return true;
	}

	@Override
	public void print(String prefix) {
		System.out.println(prefix + "Sphere:");
		o.print(prefix + "\t");
		System.out.println(prefix + "\tRadius: " + radius);
		material.print(prefix+"\t");
	}

	public Sphere(Node sphereNode) {
		Element sphere = (Element) sphereNode;

		o = new Point3f(sphere.getAttribute(POSITION));
		radius = Float.parseFloat(sphere.getAttribute(RADIUS));
		
		String diffuse = sphere.getAttribute(DIFFUSE);
		String specular = sphere.getAttribute(SPECULAR);
		String shiness = sphere.getAttribute(SHINESS);
		String refract = sphere.getAttribute(REFRACT);
		String index = sphere.getAttribute(INDEX);
		material = new Material(diffuse, specular, shiness, refract, index);
	}
	
	public Sphere(Point3f o, float r)
	{
		this.o = new Point3f(o);
		radius = r;
	}
	
	private boolean Quadratic(float A, float B, float C, float t[]) {
		// Find quadratic discriminant
		float discrim = B * B - 4.f * A * C;
		if (discrim < 0.)
			return false;
		float rootDiscrim = (float) Math.sqrt(discrim);

		// Compute quadratic _t_ values
		float q;
		if (B < 0)
			q = -.5f * (B - rootDiscrim);
		else
			q = -.5f * (B + rootDiscrim);
		t[0] = q / A;
		t[1] = C / q;
		if (t[0] > t[1]) {
			float tmp = t[0];
			t[0] = t[1];
			t[1] = tmp;
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		Ray ray = new Ray(new Point3f(1,1,1), new Vector3f(1,1,2));
		Sphere s = new Sphere(new Point3f(1,1,1), 5);
		Intersection inttt = new Intersection();
		float []tHit = new float[1];
		if (s.Intersect(ray, tHit, inttt)) {
			inttt.p.print();
			inttt.n.print("");
		}
	}
}
