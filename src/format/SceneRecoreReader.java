package format;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.RecordReader;

public class SceneRecoreReader implements RecordReader<PairWritable, BytesWritable> {

	private boolean processed = false;
	private SceneSplit sceneSplit;

	public SceneRecoreReader(SceneSplit sceneSplit, Configuration conf)
			throws IOException {
		this.sceneSplit = sceneSplit;
	}
	
	@Override
	public void close() throws IOException {		
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
		return 0;
	}

	@Override
	public float getProgress() throws IOException {
		// TODO Auto-generated method stub
		return processed?1.f:0.f;
	}

	@Override
	public boolean next(PairWritable key, BytesWritable value)
			throws IOException {
		
		if (!processed) {
			key.setfirst(sceneSplit.start);
			key.setsecond(sceneSplit.end);
			value.set(sceneSplit.contents, 0, sceneSplit.contents.length);
			processed = true;
			return true;
		}
		return false;
	}

}
