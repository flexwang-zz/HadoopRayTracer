package format;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class SceneSplit implements org.apache.hadoop.mapred.InputSplit{
	
	public int start, end;
	public byte[] contents;
	public int length;
	
	public SceneSplit() {
	}
	
	public SceneSplit(int start, int end, byte[] bytes, int length) {
		this.length = length;
		this.start = start;
		this.end = end;
		contents = new byte[length];
		System.arraycopy(bytes, 0, contents, 0, length);
	}
	

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		start = in.readInt();
		end = in.readInt();
		length = in.readInt();
		contents = new byte[length];
		in.readFully(contents, 0, length);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(start);
		out.writeInt(end);
		out.writeInt(length);
		out.write(contents, 0, length);
	}

	@Override
	public long getLength() throws IOException {
		return length+12;
	}

	@Override
	public String[] getLocations() throws IOException {
		// TODO Auto-generated method stub
		String[] result = new String[1];
		result[0] = "";
		return result;
	}

}
