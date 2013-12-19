package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import lights.Light;
import primitives.Intersection;
import primitives.Ray;
import primitives.Vector3f;

public class raytracer {

	public static void main(String args[]) throws IOException {
		InputStream is = new FileInputStream("/home/flex/scene.xml");
		int len = is.available();
		byte[]b = new byte[len];
		is.read(b);
		Scene scene = new Scene(b);
		scene.print("");
		BufferedImage buff = raytrace(scene);

		File file = new File("/home/flex/out.jpg");
		try {
			ImageIO.write(buff, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("finish");
	}

	public static BufferedImage raytrace(Scene scene) throws IOException {
		File file = new File("/home/flex/answer");
		OutputStream os = new FileOutputStream(file);
		Camera camera = scene.camera;
		int xres = camera.getXRes();
		int yres = camera.getYRes();
		BufferedImage result = new BufferedImage(xres, yres,
				BufferedImage.TYPE_3BYTE_BGR);
		String answer = "";
		for (int i = 0; i < xres; i++) {
			for (int j = 0; j < yres; j++) {
				result.setRGB(i, j, getColor3f(scene, i, j)
						.getValue());
			}
		}
		os.write(answer.getBytes());
		os.flush();
		os.close();
		return result;

	}
	
	public static Color3f getColor3f(Scene scene, int x, int y) {
		return raycast(scene.camera.GenerateRay(x, y), scene);
	}

	public static Color3f raycast(Ray ray, Scene scene) {
		Intersection intersect = new Intersection();
		Color3f rgb = new Color3f(0.f);
		if (scene.Intersect(ray, intersect)) {
			// for each light in scene test shadow
			for (int i = 0, size = scene.ls.size(); i < size; i++) {
				Vector3f wi = new Vector3f();
				Light light = scene.ls.get(i);
				Color3f li = light.SampleLight(scene, intersect.p, wi, intersect.rayEpsilon);
				if (li.isBlack()) {
					continue;
				}
				Color3f brdf = intersect.material.BRDF(wi, ray.d.Scale(-1.f), intersect.n);
				rgb = rgb.Add(li.Scale(brdf));
			}
			
			// recursive trace ray
			if (++ray.depth < scene.camera.maxdepth) {
				Vector3f wi = intersect.n.Scale(2.f).Sub(ray.d.Scale(-1.f));
				Color3f brdf = intersect.material.BRDF(wi, ray.d.Scale(-1.f), intersect.n);
				Ray newray = new Ray(intersect.p, wi, intersect.rayEpsilon, Float.MAX_VALUE);
				
				newray.depth = ray.depth;
				rgb = rgb.Add(raycast(newray, scene).Scale(brdf));
			}
			
		}
		return rgb;
	}

}
