package materials;

import primitives.Vector3f;
import core.Color3f;

public class Material {
	public Color3f BSDF(Vector3f wi, Vector3f wr, Vector3f n) {
		wi = wi.Normalize();
		wr = wr.Normalize();

		return diffuseBSDF(wi, wr, n).Add(specularBSDF(wi, wr, n));
	}

	public Color3f diffuseBSDF(Vector3f wi, Vector3f wr, Vector3f n) {

		if (wi.Dot(n) * wr.Dot(n) < 0.f) { // the wi and wr are not in the same
											// hemisphere of n
			return new Color3f(0.f);
		}
		return diffuse.Scale(Math.abs(n.Cos(wi)));
	}

	public Color3f specularBSDF(Vector3f wi, Vector3f wr, Vector3f n) {
		if (wi.Dot(n) * wr.Dot(n) < 0.f) { // the wi and wr are not in the same
											// hemisphere of n
			return new Color3f(0.f);
		}
		Vector3f H = wi.Add(wr).Normalize();
		float s = (float) Math.pow(Math.abs(H.Cos(n)), shiness);

		return specular.Scale(s);

	}

	public Color3f refractBSDF(Vector3f wi, Vector3f wr, Vector3f n) {
		if (wi.Dot(n) * wr.Dot(n) < 0.f) { // the wi and wr are not in the same
			// hemisphere of n
			return refract;
		}
		return new Color3f(0.f);
	}

	public Vector3f getRefractDir(Vector3f wi, Vector3f n) {
		float cos = wi.Cos(n);
		float sin = (float) Math.sqrt(1 - cos * cos);
		Vector3f x, y, z;
		float sint, cost;
		if (cos > 0.f) { // refract from outside
			// System.out.println("bigger than zero");
			sint = Math.min(1.f, sin / index);
			cost = (float) Math.sqrt(1 - sint * sint);
			z = wi.Cross(n);
			y = n.Scale(-1.f).Normalize();
			x = y.Cross(z).Normalize();

		} else { // refract from inside
			sint = Math.min(1.f, sin * index);
			cost = (float) Math.sqrt(1 - sint * sint);
			z = wi.Cross(n.Scale(-1.f));
			y = n.Normalize();
			x = y.Cross(z).Normalize();
		}
		return x.Scale(sint).Add(y.Scale(cost)).Normalize();

	}

	public Material(String diffuse, String specular, String Shiness,
			String refract, String index) {
		this.diffuse = diffuse.isEmpty() ? new Color3f(0.f) : new Color3f(
				diffuse);
		this.specular = specular.isEmpty() ? new Color3f(0.f) : new Color3f(
				specular);
		this.refract = refract.isEmpty() ? new Color3f(0.f) : new Color3f(
				refract);
		this.shiness = Shiness.isEmpty() ? 5 : Integer.parseInt(Shiness);
		this.index = index.isEmpty() ? 1.f : Float.parseFloat(index);
	}

	public Material(Material material) {
		this.diffuse = new Color3f(material.diffuse);
		this.specular = new Color3f(material.specular);
	}

	public Material() {
		this.diffuse = new Color3f(0.f);
		this.specular = new Color3f(0.f);
	}

	public void print(String prefix) {
		System.out.println(prefix + "Material:");
		System.out.println(prefix + "\tShiness:" + shiness);
		diffuse.print(prefix + "\tdiffuse: ");
		specular.print(prefix + "\tspecular: ");
		refract.print(prefix + "\trefract: ");
		System.out.println(prefix + "\tindex: " + index);
	}

	public Color3f diffuse;
	public Color3f specular;
	public Color3f refract;
	public float index;

	public int shiness;

	public static void main(String[] args) {
		Material m = new Material();
		m.index = 1.2f;
		Vector3f n = new Vector3f(0.f, -1.f, 0.f);
		Vector3f v = m.getRefractDir(new Vector3f(1f, 1.f, 1f), n);
		m.getRefractDir(v, n).print("");
		new Vector3f(1, 1, 1).Normalize().print("");

	}
}
