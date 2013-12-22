package core;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lights.Light;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import primitives.Intersection;
import primitives.Ray;
import shapes.*;

public class Scene {

	//
	public static final String SCENE_CAMERA = "camera";
	public static final String SCENE_SHAPES = "shapes";
	public static final String SCENE_LIGHTS = "lights";

	public Camera camera;
	public ArrayList<Shape> objs;
	public ArrayList<Light> ls;
	
	public Scene(byte[] scenebytes)
	{
		Document document = parse(scenebytes);
		parseScene(document);
	}
	
	public Scene(InputStream is)
	{
		Document document = parse(is);
		parseScene(document);
	}
	
	public Scene(File file) {
		
		Document document = parse(file);
		parseScene(document);
		
	}
	
	private void parseScene(Document document)
	{
		objs = new ArrayList<Shape>();
		ls = new ArrayList<Light>();
		// get root element
		Element rootElement = document.getDocumentElement();
		// traverse child elements
		NodeList nodes = rootElement.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String nodename = node.getNodeName();
			if (nodename.equals(Scene.SCENE_CAMERA)) {
				camera = new Camera(node);
			} else if (nodename.equals(Scene.SCENE_SHAPES)) {
				parseShapes(node);
			} else if (nodename.equals(Scene.SCENE_LIGHTS)) {
				parseLights(node);
			}
		}		
	}

	private void parseShapes(Node shapenode) {
		NodeList nodes = shapenode.getChildNodes();
		for (int i = 0, size = nodes.getLength(); i < size; i++) {
			if (nodes.item(i) instanceof Element)
				objs.add(Shape.ShapeFactory(nodes.item(i), objs.size()+1));
		}

	}

	private void parseLights(Node lightnode) {
		NodeList nodes = lightnode.getChildNodes();
		for (int i = 0, size = nodes.getLength(); i < size; i++) {
			if (nodes.item(i) instanceof Element)
				ls.add(Light.LightFactory(nodes.item(i), objs));
		}
	}

	private Document parse(File file) {
		Document document = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			// DOM parser instance
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			// parse an XML file into a DOM tree
			document = builder.parse(file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	private Document parse(byte[] scenebytes) {
		Document document = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			// DOM parser instance
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			// parse an XML file into a DOM tree
			InputStream is = new ByteArrayInputStream(scenebytes);
			document = builder.parse(is);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	private Document parse(InputStream is) {
		Document document = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			// DOM parser instance
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			// parse an XML file into a DOM tree
			document = builder.parse(is);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	public void print(String prefix) {
		System.out.println(prefix + "Scene:");
		// camera
		camera.print(prefix + "\t");

		System.out.println(prefix + "Shapes: " + objs.size());
		for (int i = 0, size = objs.size(); i < size; i++) {
			objs.get(i).print(prefix + "\t");
		}

		System.out.println(prefix + "Lights: " + ls.size());
		for (int i = 0, size = ls.size(); i < size; i++) {
			ls.get(i).print(prefix + "\t");
		}
	}

	public boolean Intersect(Ray r) {
		for (int i = 0, size = objs.size(); i < size; i++) {
			if (objs.get(i).Intersect(r)) {
				return true;
			}
		}
		return false;
	}

	public boolean Intersect(Ray r, Intersection intersect) {

		float tHit_min = Float.MAX_VALUE;
		float[] thist = new float[1];
		boolean hitted = false;
		Intersection thisinter = new Intersection();
		
		for (int i = 0, size = objs.size(); i < size; i++) {
			if (objs.get(i).Intersect(r, thist, thisinter)) {
				hitted = true;
				if (thist[0] < tHit_min) {
					tHit_min = thist[0];

					intersect.p = thisinter.p;
					intersect.n = thisinter.n;
					intersect.material = thisinter.material;
					intersect.rayEpsilon = thisinter.rayEpsilon;
					intersect.id = thisinter.id;
					intersect.arealight = thisinter.arealight;

				}
			}
		}
		return hitted;

	}
}
