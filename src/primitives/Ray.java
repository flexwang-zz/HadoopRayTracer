package primitives;

import core.Color3f;

public class Ray {
	public Color3f rgb;
	public Vector3f d;
	public Point3f o;
	public float maxt, mint;
	public int depth;
	
	public Ray()
	{
		depth = 0;
	}
	
	public Ray(Point3f origin, Vector3f dir)
	{
		o = origin;
		d = dir.Normalize();
		mint = 0.f;
		maxt = Float.MAX_VALUE;
		depth = 0;
	}
	
	public Ray(Point3f from, Point3f to) {
		o = new Point3f(from);
		d = new Vector3f(from, to).Normalize();
		mint = 0.f;
		maxt = new Vector3f(from, to).length();
		depth = 0;
	}
	
	public Ray(Point3f from, Vector3f dir, float mint, float maxt) {
		o = new Point3f(from);
		d = dir.Normalize();
		this.mint = mint;
		this.maxt = maxt;
	}
	
	public Ray(Ray r)
	{
		o = new Point3f(r.o);
		d = new Vector3f(r.d);
		maxt = r.maxt;
		mint = r.mint;
		depth = 0;
	}
	
	public Point3f getPoint(float t)
	{
		return new Point3f(o.x+d.x*t, o.y+d.y*t, o.z+d.z*t);
	}
	
	public void print(String prefix)
	{
		System.out.println(prefix+"Ray: ");
		o.print("\to: "+prefix);
		d.print("\td: "+prefix);
	}
}
