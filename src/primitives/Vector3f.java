package primitives;

public class Vector3f {
	public float x, y, z;

	public Vector3f(float X, float Y, float Z) {
		x = X;
		y = Y;
		z = Z;
	}

	public Vector3f() {
		x = 0.f;
		y = 0.f;
		z = 0.f;
	}

	public Vector3f(String str) {
		String vals[] = str.split(" ");

		x = Float.parseFloat(vals[0]);
		y = Float.parseFloat(vals[1]);
		z = Float.parseFloat(vals[2]);
	}

	public Vector3f(Vector3f v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3f(Point3f from, Point3f to) {
		x = to.x - from.x;
		y = to.y - from.y;
		z = to.z - from.z;
	}

	public Vector3f Normalize() {
		float len = this.length();
		return new Vector3f(x / len, y / len, z / len);
	}

	public Vector3f Cross(Vector3f v2) {
		return new Vector3f(y * v2.z - z * v2.y, z * v2.x - x * v2.z, x * v2.y
				- y * v2.x);
	}

	public float Dot(Vector3f v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3f Add(Vector3f v) {
		return new Vector3f(x + v.x, y + v.y, z + v.z);
	}
	
	public Vector3f Sub(Vector3f v) {
		return new Vector3f(x-v.x, y-v.y, z-v.z);
	}
	
	public Vector3f Scale(float t) {
		return new Vector3f(x * t, y * t, z * t);
	}

	public void print(String prefix) {
		System.out.println(prefix + "x:" + x + " y:" + y + " z:" + z);
	}

	public float Cos(Vector3f v) {	
		return Dot(v) / v.length() / length();
	}

	public float Angle(Vector3f v) {
		return (float) Math.acos(Cos(v));
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float lengthSquared() {
		return x * x + y * y + z * z;
	}
}
