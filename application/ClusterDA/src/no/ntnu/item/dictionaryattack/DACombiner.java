package no.ntnu.item.dictionaryattack;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class DACombiner extends MapReduceBase implements
		Reducer<Text, BooleanWritable, Text, BooleanWritable> {

	@Override
	public void reduce(Text key, Iterator<BooleanWritable> values,
			OutputCollector<Text, BooleanWritable> output, Reporter reporter)
			throws IOException {
		System.out.println("PWD: " + key.toString());
		if (values.next().get()) {
			DAProcessor.printSuccess(key.toString());
			output.collect(key, new BooleanWritable(true));
		}
	}
}
