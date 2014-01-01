package format;

import java.io.IOException;

import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import core.Scene;

public class SceneFileInputFormat extends
		FileInputFormat<PairWritable, BytesWritable> {
	@Override
	protected boolean isSplitable(FileSystem fs, Path filename) {
		return true;
	}

	@Override
	public RecordReader<PairWritable, BytesWritable> getRecordReader(
			InputSplit split, JobConf job, Reporter reporter)
			throws IOException {
		return new SceneRecoreReader((SceneSplit) split, job);
	}

	@Override
	public InputSplit[] getSplits(JobConf job, int useless) throws IOException {
		FileStatus status = listStatus(job)[0];
		Path fileName = status.getPath();
		FileSystem fs = fileName.getFileSystem(job);
		FSDataInputStream in = fs.open(fileName);
		int length = in.available();
		byte[] contents = new byte[length];
		try {
			IOUtils.readFully(in, contents, 0, contents.length);
		} finally {
			IOUtils.closeStream(in);
		}

		Scene scene = new Scene(contents);
		int yres = scene.camera.getYRes();
		int xres = scene.camera.getXRes();
		int nDivide = job.getInt("nDivide", 0);
		nDivide = (nDivide < 1 || nDivide > yres) ? yres : nDivide;
		int line = yres / nDivide;

		SceneSplit[] result = new SceneSplit[nDivide];

		for (int i = 0; i < nDivide; i++) {
			if (i == (nDivide - 1)) {
				result[i] = new SceneSplit(i * line, yres - 1, contents, length);
			} else {
				result[i] = new SceneSplit(i * line, i * line + line - 1,
						contents, length);
			}
		}
		return result;
	}
}