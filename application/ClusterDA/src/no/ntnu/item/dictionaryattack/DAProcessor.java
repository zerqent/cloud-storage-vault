package no.ntnu.item.dictionaryattack;

import java.net.URI;
import java.security.Security;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.NLineInputFormat;

public class DAProcessor {

	public static byte[] ciphertext;
	public static byte[] salt;
	public static String password;
	public static boolean found;

	public static double time;
	public static long start;

	public static int words_read;

	// private static void init() throws IOException {
	// File tmp = new File("/home/melvold/Desktop/my_credentials.csv");
	// FileInputStream in = new FileInputStream(tmp);
	// byte[] b = new byte[(int) tmp.length()];
	// in.read(b);
	// in.close();
	// salt = new byte[8];
	// ciphertext = new byte[b.length - salt.length];
	// System.arraycopy(b, 0, salt, 0, salt.length);
	// System.arraycopy(b, salt.length, ciphertext, 0, ciphertext.length);
	// found = false;
	// words_read = 0;
	// }

	public static void printHelp() {
		System.out.println("************************************************");
		System.out.println("\t\tHELP");
		System.out.println("************************************************");
		System.out.println("o Manual brute force attack");
		System.out.println("\t [1 arg]: Path to target file");
		System.out.println("\t [2 arg]: Maximum word length");
		System.out.println("\t [3 arg]: Number of threads");
		System.out.println("o Dictionary attack");
		System.out.println("\t [1 arg]: Path to target file");
		System.out.println("\t [2 arg]: Path to dictionary file");
		System.out.println("\t [3 arg]: Number of threads");
		System.out
				.println("NOTE: Make sure you have installed the Java(TM) \nCryptography Extension (JCE) Jurisdiction Policy \nFiles prior to execution");

	}

	public static void printSuccess(String word) {
		System.out.println("SUCCESS! Password is { " + new String(word) + " }");
		System.out.println("Read " + DAProcessor.words_read + " passwords in "
				+ DAProcessor.time + " seconds");
		System.out.println("Average speed: " + DAProcessor.words_read
				/ DAProcessor.time + " passwords/second");
	}

	public static void printFail() {
		DAProcessor.time = (System.currentTimeMillis() - DAProcessor.start) / 1000.0;
		System.out.println("Did not find password");
		System.out.println("Read " + DAProcessor.words_read + " passwords in "
				+ DAProcessor.time + " seconds");
		System.out.println("Average speed: " + DAProcessor.words_read
				/ DAProcessor.time + " passwords/second");
	}

	public static void main(String[] args) throws Exception {

		Security.insertProviderAt(
				new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);

		JobConf conf = new JobConf(DAProcessor.class);
		conf.setJobName("dictionaryattack");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(LongWritable.class);

		conf.setMapperClass(DAMapper.class);
		// conf.setCombinerClass(DACombiner.class);
		conf.setReducerClass(DAReducer.class);

		conf.setInputFormat(NLineInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		conf.setNumReduceTasks(1);
		conf.setLong("TIME_START", 0);
		conf.setInt("mapred.line.input.format.linespermap", 959661);

		DistributedCache.addCacheFile(new URI(
				"/user/melvold/my_credentials.csv"), conf);

		System.out.println("RUNNING JOB: " + System.currentTimeMillis());
		JobClient.runJob(conf);
	}

}
