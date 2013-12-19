package core;

public class Color3f {
	public float r, g, b;
	
	public Color3f(Color3f rgb) {
		r = rgb.r;
		g = rgb.g;
		b = rgb.b;
	}
	
	public Color3f(float rgb) {
		r = g =b =rgb;
	}
	
	public Color3f(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color3f(int[] rgb)
	{
		this.r = rgb[0];
		this.g = rgb[1];	
		this.b = rgb[2]	;
	}
	
	public Color3f(String rgb)
	{
		String[] rgbs = rgb.split(" ");
		r = Float.parseFloat(rgbs[0]);
		g = Float.parseFloat(rgbs[1]);
		b = Float.parseFloat(rgbs[2]);
	}
	
	public int getValue()
	{
		int alpha = 255;
		int red = Math.min(255,(int)(r*255.f));
		int green = Math.min(255, (int)(g*255.f));
		int blue = Math.min(255, (int)(b*255.f));
		
		int rgba = (alpha << 24) | (red<< 16) | (green << 8) | blue;
		return rgba;
	}
	
	public byte[] getBytes() {
		int red = Math.min(255,(int)(r*255.f));
		int green = Math.min(255, (int)(g*255.f));
		int blue = Math.min(255, (int)(b*255.f));
		byte[] bytes = new byte[3];
		bytes[0] = (byte)(((red) << 24) >> 24);
		bytes[1] = (byte)(((green) << 24) >> 24);
		bytes[2] = (byte)(((blue) << 24) >> 24);
		return bytes;
		
	}
	
	public Color3f Scale(float rr, float gg, float bb)
	{
		return new Color3f((r*rr), (g*gg), (b*bb));
	}
	
	public Color3f Scale(Color3f rgb)
	{
		return new Color3f(r*rgb.r, g*rgb.g, b*rgb.b);
	}
	
	public Color3f Scale(float s)
	{
		return new Color3f((r*s), (g*s), (b*s));
	}
	
	public Color3f Add(Color3f rgb)
	{
		return new Color3f(r+rgb.r, g+rgb.g, b+rgb.b);
	}

	public void print(String prefix) {
		System.out.println(prefix+"r:"+r+" g:"+g+" b:"+b);
	}
	
	public boolean isBlack()
	{
		return r<Float.MIN_VALUE && g<Float.MIN_VALUE && b<Float.MIN_VALUE;
	}
	
}
