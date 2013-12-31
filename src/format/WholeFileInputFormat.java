package format;

import java.io.IOException;
 
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
 
public class WholeFileInputFormat extends FileInputFormat<PairWritable, BytesWritable> {
    @Override
    protected boolean isSplitable(FileSystem fs, Path filename) {
        return false;
    }
 
    @Override
    public RecordReader<PairWritable, BytesWritable> getRecordReader(
      InputSplit split, JobConf job, Reporter reporter) throws IOException {
        return new WholeFileRecordReader((FileSplit) split, job);
    }
}