package materials;

import primitives.Vector3f;
import core.Color3f;

public class Material {
	public Color3f BRDF(Vector3f wi, Vector3f wr, Vector3f n)
	{
		wi = wi.Normalize();
		wr = wr.Normalize();
		
		Color3f rgb = new Color3f(0.f);
		//diffuse term
		rgb = rgb.Add(diffuse.Scale(Math.max(0.f, n.Cos(wi))));

		//specular term
		if (n.Dot(wi) > 0.f || true) {
			Vector3f H = wi.Add(wr).Normalize();
			float s = (float) Math.pow(Math.max(0.f, H.Cos(n)), Shiness);
			
			rgb = rgb.Add(specular.Scale(s));
		}
		return rgb;
	}
	
	public Material(String diffuse, String specular, String Shiness)
	{
		this.diffuse = diffuse.isEmpty()?new Color3f(0.f):new Color3f(diffuse);
		this.specular = specular.isEmpty()?new Color3f(0.f):new Color3f(specular);
		this.Shiness = Shiness.isEmpty()?5:Integer.parseInt(Shiness);
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
		System.out.println(prefix+"Material:");
		System.out.println(prefix+"\tShiness:"+Shiness);
		diffuse.print(prefix+"\tdiffuse: ");
		specular.print(prefix+"\tspecular: ");
	}
	
	public Color3f diffuse;
	public Color3f specular;
	public int Shiness;
}
