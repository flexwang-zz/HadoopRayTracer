package primitives;

import core.Color3f;
import materials.Material;

public class Intersection {
	public Point3f p;
	public Vector3f n;
	public Material material;
	public Color3f arealight = null;
	public float rayEpsilon;
	public int id;

	public Intersection(Point3f p, Vector3f v, Material material, int id) {
		this.p = new Point3f(p);
		this.n = v.Normalize();
		this.material = new Material(material);
		this.id = id;
	}

	public Intersection(Intersection intersect) {
		this.p = new Point3f(intersect.p);
		this.n = new Vector3f(intersect.n);
		this.material = new Material(intersect.material);
		this.rayEpsilon = intersect.rayEpsilon;
		this.id = intersect.id;

	}

	public Intersection() {
	}
}
