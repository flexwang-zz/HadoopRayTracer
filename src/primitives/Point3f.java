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

	public Point3f Translate3f(Vector3f v) {
		Point3f result = new Point3f(x, y, z);
		result.x += v.x;
		result.y += v.y;
		result.z += v.z;

		return result;
	}

	public Point3f Translate3f(float xt, float yt, float zt) {
		return new Point3f(this.x + xt, this.y + yt, this.z + zt);
	}
}
