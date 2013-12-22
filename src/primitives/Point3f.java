package primitives;


public class Point3f {
	public float x, y, z;

	public Point3f(float X, float Y, float Z) {
		x = X;
		y = Y;
		z = Z;
	}

	public Point3f() {
		x = 0.f;
		y = 0.f;
		z = 0.f;
	}

	public Point3f(String str) {
		String vals[] = str.split(" ");

		x = Float.parseFloat(vals[0]);
		y = Float.parseFloat(vals[1]);
		z = Float.parseFloat(vals[2]);
	}

	public Point3f(Point3f p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}

	public void print() {
		print("");
	}

	public void print(String prefix) {
		System.out.println(prefix + "x:" + x + " y:" + y + " z:" + z);
	}

	public Point3f Translate(Vector3f v) {
		return new Point3f(x+v.x, y+v.y, z+v.z);
	}

	public Point3f Translate(float xt, float yt, float zt) {
		return new Point3f(this.x + xt, this.y + yt, this.z + zt);
	}
	
	public Point3f Add(Point3f p) {
		return new Point3f(x+p.x, y+p.y, z+p.z);
	}
	
	public Point3f Scale(float s) {
		return new Point3f(x*s, y*s, z*s);
	}
}
