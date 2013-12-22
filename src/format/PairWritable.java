package format;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PairWritable implements org.apache.hadoop.io.WritableComparable<PairWritable>  {
	private int first, second;

	public PairWritable(int first, int second) {
		this.first = first;
		this.second = second;
	}
	
	public PairWritable() {
		this.first = 0;
		this.second = 0;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		first = in.readInt();
		second = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(first);
		out.writeInt(second);
	}

	@Override
	public int compareTo(PairWritable o) {
		if (o instanceof PairWritable) {
			int thisValue = this.first;
			int thatValue = ((PairWritable)o).first;
			return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0
					: 1));
		}
		return 0;
	}
	
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + first;
        result = prime * result + (int) (second ^ (second >>> 32));
        return result;
      }
    
    public int getfirst() {
    	return first;
    }
    
    public int getsecond() {
    	return second;
    }

}
