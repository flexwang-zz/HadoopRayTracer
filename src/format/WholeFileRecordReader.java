package format;

import java.io.IOException;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.RecordReader;

import core.Scene;

class WholeFileRecordReader implements RecordReader<PairWritable, BytesWritable> {

	private FileSplit fileSplit;
	private Configuration conf;

	private int nDivide;
	private int curDivide;
	private byte[] contents;
	private byte[] imagesize;
	private int line;
	private int xres, yres;

	public WholeFileRecordReader(FileSplit fileSplit, Configuration conf)
			throws IOException {
		this.fileSplit = fileSplit;
		this.conf = conf;

		this.nDivide = conf.getInt("nDivide", 0);
		this.curDivide = -1;
		contents = new byte[(int) this.fileSplit.getLength()];

		Path file = fileSplit.getPath();
		FileSystem fs = file.getFileSystem(conf);
		FSDataInputStream in = null;
		try {
			in = fs.open(file);
			IOUtils.readFully(in, contents, 0, contents.length);
		} finally {
			IOUtils.closeStream(in);
		}

		Scene scene = new Scene(contents);
		yres = scene.camera.getYRes();
		xres = scene.camera.getXRes();

		nDivide = (nDivide < 1 || nDivide > yres) ? yres : nDivide;
		line = yres / nDivide;
		// output the bmp image size(width*height) with key being -1
		imagesize = new byte[8];
		int2byte(xres, imagesize, 0);
		int2byte(yres, imagesize, 4);
	}

	@Override
	public boolean next(PairWritable key, BytesWritable value) throws IOException {
		if (curDivide < 0) {
			key.setfirst(-1);
			key.setsecond(-1);
			value.set(imagesize, 0, 8);
			curDivide++;
			return true;
		}
		else if (curDivide < (nDivide-1)){
			key.setfirst(curDivide*line);
			key.setsecond(curDivide * line + line - 1);
			value.set(contents, 0, contents.length);
			curDivide++;
			return true;
		}
		else if (curDivide < nDivide) {
			key.setfirst(curDivide*line);
			key.setsecond(yres - 1);
			value.set(contents, 0, contents.length);
			curDivide++;
			return true;
		}
		
		return false;
	}

	@Override
	public PairWritable createKey() {
		return new PairWritable();
	}

	@Override
	public BytesWritable createValue() {
		return new BytesWritable();
	}

	@Override
	public long getPos() throws IOException {
		float ratio = (float)(curDivide+1)/nDivide;
		return (long)(ratio*fileSplit.getLength());
	}

	@Override
	public float getProgress() throws IOException {
		float ratio = (float)(curDivide+1)/nDivide;
		return ratio;
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}
	
	private  void int2byte(int res, byte[] targets, int shift) {

		targets[0 + shift] = (byte) (res & 0xff);
		targets[1 + shift] = (byte) ((res >> 8) & 0xff);
		targets[2 + shift] = (byte) ((res >> 16) & 0xff);
		targets[3 + shift] = (byte) (res >>> 24);
	}
}