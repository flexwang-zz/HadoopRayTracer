package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import format.ByteOutputFormat;
import format.PairWritable;
import format.SceneFileInputFormat;

public class hadoopRaytracer {
	private static int divide;

	public static class Map extends MapReduceBase implements
			Mapper<PairWritable, BytesWritable, IntWritable, BytesWritable> {

		public void map(PairWritable keyrow, BytesWritable scenebytes,
				OutputCollector<IntWritable, BytesWritable> output,
				Reporter reporter) throws IOException {
			// get the row id
			int start = keyrow.getfirst();
			int end = keyrow.getsecond();

			// parse the scene
			InputStream is = new ByteArrayInputStream(scenebytes.getBytes());
			byte[] b = new byte[scenebytes.getLength()];
			is.read(b);

			Scene scene = new Scene(b);
			int xRes = scene.camera.getXRes();
			int yRes = scene.camera.getYRes();

			byte[] rgbs = new byte[3 * xRes * (end - start + 1)];
			for (int line = start; line <= end; line++) {
				int rowid = yRes - line - 1; // because bmp stores pixel value
												// in a
												// bottom-up way
				for (int i = 0; i < xRes; i++) {
					System.arraycopy(raytracer.getColor3f(scene, i, rowid)
							.getBytes(), 0, rgbs, i * 3 + (line - start) * 3
							* xRes, 3);
				}
			}
			
			if (start == 0) {
				byte[] bmphead_plus_rgbs = new byte[3 * xRes * (end - start + 1)+58];
				System.arraycopy(BmpWrite24.bmphead(xRes, yRes), 0, bmphead_plus_rgbs, 0, 58);
				System.arraycopy(rgbs, 0, bmphead_plus_rgbs, 58, 3 * xRes * (end - start + 1));
				output.collect(new IntWritable(start), new BytesWritable(bmphead_plus_rgbs));
			}
			else {
				output.collect(new IntWritable(start), new BytesWritable(rgbs));
			}
		}
	}

	// public static class Combine extends MapReduceBase implements
	public static class Reduce extends MapReduceBase implements
			Reducer<IntWritable, BytesWritable, NullWritable, BytesWritable> {

		public void reduce(IntWritable keyrow, Iterator<BytesWritable> values,
				OutputCollector<NullWritable, BytesWritable> output,
				Reporter reporter) throws IOException {

			output.collect(null, values.next());
		}
	}

	public static void int2byte(int res, byte[] targets, int shift) {

		targets[0 + shift] = (byte) (res & 0xff);
		targets[1 + shift] = (byte) ((res >> 8) & 0xff);
		targets[2 + shift] = (byte) ((res >> 16) & 0xff);
		targets[3 + shift] = (byte) (res >>> 24);
	}

	public static int byte2int(byte[] res, int shift) {

		int targets = (res[0 + shift] & 0xff)
				| ((res[1 + shift] << 8) & 0xff00)
				| ((res[2 + shift] << 24) >>> 8) | (res[3 + shift] << 24);
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
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(SceneFileInputFormat.class);
		conf.setOutputFormat(ByteOutputFormat.class);

		conf.setNumMapTasks(2000);
		conf.setNumReduceTasks(1);

		// interpreter
		String inpath, outpath;
		if (args[0].equals("-d") || args[0].equals("--divide")) {
			try {
				divide = Integer.parseInt(args[1]);
				if (divide < 0) {
					divide = 0;
				}
			} catch (Exception e) {
				divide = 0;
			}

			inpath = args[2];
			outpath = args[3];
		} else if (args[0].startsWith("-d")) {
			int startindex = args[0].indexOf('d') + 1;
			divide = Integer.parseInt(args[0].substring(startindex));
			if (divide < 0) {
				divide = 0;
			}
			inpath = args[1];
			outpath = args[2];
		} else if (args[0].startsWith("--divide")) {
			int startindex = args[0].indexOf('e') + 1;
			divide = Integer.parseInt(args[0].substring(startindex));
			if (divide < 0) {
				divide = 0;
			}
			inpath = args[1];
			outpath = args[2];
		} else {
			inpath = args[0];
			outpath = args[1];
		}

		conf.setInt("nDivide", divide);

		FileInputFormat.setInputPaths(conf, new Path(inpath));
		FileOutputFormat.setOutputPath(conf, new Path(outpath));
		JobClient.runJob(conf);
	}
}
