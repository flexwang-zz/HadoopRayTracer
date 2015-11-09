HadoopRayTracer
===============
 ![image](https://github.com/flexwang/HadoopRayTracer/raw/master/scenes/images/balls.jpg)
 
## Introduction
Hadoop Raytracer is a ray tracer based on Hadoop MapReduce, which takes advantage of the parallelism of clusters. I will explain how to work on it and how I implement it after a brief introduction to Apache Hadoop and Ray Tracing Algorithm.

### About Apache Hadoop
Hadoop MapReduce is a software framework for easily writing applications in-parallel on large clusters (thousands of nodes) of commodity hardware in a reliable, fault-tolerant manner.

Hadoop is so easy to use that people only need to define two methods to write a simplest Hadoop application.

### About Ray Tracing
Ray Tracing is a basic algorithm for rendering realistic image, which takes a description of a 3D scene and how a camera is placed as input and outputs an image captured by the camera of the scene.

Giving a detailed description of Ray Tracing is far beyond the scope of this article. However, the main idea is pretty simple and straightforward. In order to create a picture from a certain viewpoint, we cast rays from the view-point to the scene. If the rays intersect with the scene, the color of the intersection is exactly what we will see. We cast a ray from the intersection position, which will intersect with the scene, to get the color of the previous intersection. Repeating the ray casting and intersecting process will provide us with the final color of each image pixel in the camera screen.

### Combine

As we can see, since ray tracing takes a long time to do ray intersection, the algorithm is rather time consuming. Also, we can see that, ray tracing algorithm is a highly parallel process, where calculation of each pixel value is independent from others. 

To take advantage of the parallelism of this algorithm, I build Hadoop Raytracer to amortize the expensive computation across clusters. It takes a scene description xml as input, runs the raytracing in-parallel across clusters and eventually generates a bmp image as it generates on a single pc (why bmp will be explained later).

## Usage
### Define a scene xml
To generate an image of what the scene should look like, we first need the scene of course. Here, we adopt .xml file, which is both readable and easy to parse. 

The scene xml should has a root element <scene/> to hold the entire scene. And there should have three types of elements: a camera to define the point of view and the resolution of the output image, shapes to define the objects in the scene and lights to define the illumination of the scene. More scene description files can be seen in /scenes/ directory.

#### Camera
A typical camera should look like below. The eye, look and up attributes tell where to put, where to point and how to hold the camera respectively. And the maxdepth attribute tells the ray tracer the maximum number of light scattering bounces to follow when tracing rays through the scene. And I think the rest of the attributes just speak for themselves.
```xml
<camera
eye="278 273 -800"
look="278 273 0.f"
up="0.f 1.f 0.f"
xres="1200"
yres="1200"
znear="0.1f"
zfar="100.f"
angle="37.5f"
maxdepth="5"/>
```
### Shapes
Hadoop Raytracer supports two kinds of shape so far: sphere and triangle. And inside each shape, the material should be defined to tell the reflectance and refractiveness of the surface. If none of these is defined, the object will be set to be black by default as an occluder. 

Below is a sample. Since we use Phong reflection model here, a shiness attribute is needed.

```xml
<shapes>
	<triangle
		p1="556.0f 548.8f 0.0f"
		p2="552.8f 0.0f 0.0f"
		p3="556.0f 548.8f 559.2f"
		diffuse="0.4 0.1 0.1"
		specular="0 0 0"
		shiness="5"/>
		
	<sphere
		radius="100"
		pos="178 80 169"
		refract="0.7 0.7 0.7"
		specular="0.2 0.2 0.2"
		index="1.02f"
		shiness="20"
	/>		
</shapes>

```
### Light
Point light, direct light and area light are supported in Hadoop Ray Tracer. For a point light, the position and the illumination should be defined. For a direct light, the direction and the illumination should be defined. 

Area light is a bit more complicated: a shape element should be defined as the child node standing for the area which emits the light. When Hadoop Raytracer encounters an area light, it will add the light to the light list and the shape emitting the light to the object list.

## Start rendering
By default, we render a line of pixels in each mapper, which means there will be a total number of yres(the y resolution defined in scene .xml) mappers to be initialized(which is not a good idea, too many mappers will be initialized). Users can define another split strategy by setting the –d(--divied) argument.

Assuming Hadoop is configured and running. HadoopRaytracer.jar is in current directory and a scene file named cornellbox.xml is in /scene/ directory of hdfs. We can do a default rendering by:
```sh
hadoop jar ./HadoopRaytracer.jar core.hadoopRaytracer /scene/cornellbox.xml /output/
```
Or, we can force Hadoop Raytracer to do the rendering within 4 mappers in either of the below commands:
```sh
hadoop jar ./HadoopRaytracer.jar core.hadoopRaytracer –d 4 /scene/cornellbox.xml /output/
hadoop jar ./HadoopRaytracer.jar core.hadoopRaytracer –-divide 4 /scene/cornellbox.xml /output/

```

After rendering, we can fetch the result by a Hadoop fs –get command.

Always remember that the number of mappers should not exceed the number of nodes by too many, which will lead to an expensive overhead to initialize many mappers in one node.

## Implementation

### Ray tracing

Hadoop Raytracer project contains a standalone ray tracer and a function to calculate one pixel of the final rendered image, both of which use the ray tracing algorithm.

I don’t think this part of implementation should be discussed here, since it is an aged and well-known algorithm. There are three features I’d like to indicate:
  - Hadoop Raytracer uses Phong reflection model to simulate the surface illumination;
  - Hadoop Raytracer supports the light attenuation across a transparent surface;
  - Hadoop Raytracer supports soft shadow caused by partially blocked area light.

### MapReduce

#### Pipeline

I’d like to brief introduce the pipeline before mentioning any detail. Hadoop Raytracer first parses the input scene file and gives the scene to mappers. Then the mappers will do the rendering task that they’re responsible for. After mappers finish rendering, a single reducer receiving the rendering result will output it to a single bmp file. The graph below demonstrates the pipeline.

#### Parse the xml file as a whole

By default, Hadoop breaks an input file into lines to give mappers a <linenumber, linecontent> pair one by one. It is obviously not suitable for parsing a whole xml file. So I define a new class SceneFileInputFormat (in format. SceneFileInputFormat.java) extends FileInputFormat to read a file as a whole and give it to mappers to render.

#### Distribute rendering tasks

Actually, SceneFileInputFormat class is not only a parser but also a task distributor. After parsing the scene, we will get the y resolution of the target image. We divide it by --divide argument (if not set then it is exactly y resolution), so that each mapper responds for yres/ndivide rows of pixels and of course the last reducer will do a little less as for the remainer of yres/ndivide. I define a new class PairWritable(in format.PairWritable.java) to store the start and end line number of each task.

Additionally, the mapper responsible for the first part of the rendering task is also responsible to write the bmp file head.

#### Output format

In Hadoop framework, every mapper will output to a reducer and the reducer will output after process the data. As a result, there seems to be no way to combine them together and do some compression before outputting to hdfs. So to get a valid image file, we have to use bmp format. A bmp image stores the image information in the first 58 bytes, and then the raw rgb value. This format allows us to write the image information first and append the rgb value bit by bit afterwards.

Hadoop by default output as a plain text, we redefine the output format as binary value of rgb. The binary output format is done in format.ByteOutputFormat.java.

#### Render and write back

So, now each mapper gets what they need to do. That is, the mapper who got the <PairWritable(0,end0), BytesWritable(scene)> is responsible for writing the file head of bmp image besides rendering. And the rest of mappers are just responsible for rendering.

Writing the image head is done in core.BmpWrite24.bmphead(int width, int height), which returns the necessary information for a width x height bmp image in byte.

For each of the mappers, raytracer.getColor3f(scene, x, y)(in core.raytracer.java) will return a rgb value for a given pixel (x, y). They just call it to get the pixel rgb value time after time until they get every pixel they need. Then they will output the raw rgb value. A little trick here is that a bmp image stores pixel value in a bottom-up order, so we need to do a reverse to get a correctly ordered result.





