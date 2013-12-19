package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import wholefile.ByteOutputFormat;
import wholefile.WholeFileInputFormat;

public class hadoopRaytracer {
	public static class Map extends MapReduceBase implements
			Mapper<Text, BytesWritable, IntWritable, BytesWritable> {

		public void map(Text filename, BytesWritable scenebytes,
				OutputCollector<IntWritable, BytesWritable> output,
				Reporter reporter) throws IOException {

			InputStream is = new ByteArrayInputStream(scenebytes.getBytes());
			byte[] b = new byte[scenebytes.getLength()];
			is.read(b);
			Scene scene = new Scene(b);

			int yres = scene.camera.getYRes();
			int xres = scene.camera.getXRes();

			// output the bmp image size(width*height) with key being -1
			byte[] imagesize = new byte[8];
			int2byte(xres, imagesize, 0);
			int2byte(yres, imagesize, 4);
			output.collect(new IntWritable(-1), new BytesWritable(imagesize));

			for (int i = 0; i < yres; i++) {
				output.collect(new IntWritable(i), scenebytes);
			}
		}
	}

	// public static class Combine extends MapReduceBase implements
	public static class Reduce extends MapReduceBase implements
			Reducer<IntWritable, BytesWritable, NullWritable, BytesWritable> {

		public void reduce(IntWritable keyrow, Iterator<BytesWritable> values,
				OutputCollector<NullWritable, BytesWritable> output,
				Reporter reporter) throws IOException {

			// get the row id
			int rowid = keyrow.get();
			
			// parse the scene
			BytesWritable scenebytes = values.next();
			InputStream is = new ByteArrayInputStream(scenebytes.getBytes());
			byte[] b = new byte[scenebytes.getLength()];
			is.read(b);
			
			if (rowid < 0) {
				int xRes = byte2int(b, 0);
				int yRes = byte2int(b, 4);
				output.collect(null, new BytesWritable(BmpWrite24.bmphead(xRes, yRes)));
				return;
			}
			Scene scene = new Scene(b);
			int xRes = scene.camera.getXRes();
			int yRes = scene.camera.getYRes();
			
			rowid = yRes - rowid -1;	//because bmp stores pixel value in a bottom-up way
			byte[] rgbs = new byte[3*xRes];
			
			for (int i=0; i<xRes; i++) {
				System.arraycopy(raytracer.getColor3f(scene, i, rowid).getBytes(), 0, rgbs, i*3, 3);
			}
			output.collect(null, new BytesWritable(rgbs));
		}
	}

	public static void int2byte(int res, byte[] targets, int shift) {

		targets[0 + shift] = (byte) (res & 0xff);// 最低位
		targets[1 + shift] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2 + shift] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3 + shift] = (byte) (res >>> 24);// 最高位,无符号右移。
	}

	public static int byte2int(byte[] res, int shift) {
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

		int targets = (res[0+shift] & 0xff) | ((res[1+shift] << 8) & 0xff00) // | 表示安位或
				| ((res[2+shift] << 24) >>> 8) | (res[3+shift] << 24);
		return targets;
	}

	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(raytracer.class);
		conf.setJobName("raytracer");

		conf.setOutputKeyClass(NullWritable.class);
		conf.setOutputValueClass(BytesWritable.class);

		conf.setMapperClass(Map.class);
		conf.setMapOutputKeyClass(IntWritable.class);
		conf.setMapOutputValueClass(BytesWritable.class);
		// conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(WholeFileInputFormat.class);
		// conf.setOutputFormat(TextOutputFormat.class);
		conf.setOutputFormat(ByteOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		JobClient.runJob(conf);
	}
}
