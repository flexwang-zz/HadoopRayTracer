package primitives;

import materials.Material;

public class Intersection {
	public Point3f p;
	public Vector3f n;
	public Material material;
	public float rayEpsilon;

	public Intersection(Point3f p, Vector3f v, Material material) {
		this.p = new Point3f(p);
		this.n = v.Normalize();
		this.material = new Material(material);
	}

	public Intersection(Intersection intersect) {
		this.p = new Point3f(intersect.p);
		this.n = new Vector3f(intersect.n);
		this.material = new Material(intersect.material);
		this.rayEpsilon = intersect.rayEpsilon;

	}

	public Intersection() {
	}
}
