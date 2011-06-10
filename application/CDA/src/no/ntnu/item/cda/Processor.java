package no.ntnu.item.cda;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Security;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.NLineInputFormat;

public class Processor {

	public static byte[] ciphertext;
	public static byte[] salt;
	public static String password;
	public static boolean found;

	public static double time;
	public static long start;

	public static int words_read;

	public static void printHelp() {
		System.out.println("************************************************");
		System.out.println("\t\tHELP");
		System.out.println("************************************************");
		System.out.println("Cloud Dictionary attack");
		System.out
				.println("\t [1 arg]: Path to dictionary file in distributed file system. The dictionary file should contain lines with passwords separated by SPACE. The number of passwords per line must be a multiple of the number of threads used per server ([5 arg]).");
		System.out
				.println("\t [2 arg]: Path to output file in distributed file system");
		System.out
				.println("\t [3 arg]: Path to credentials file in distributed file system");
		System.out.println("\t [4 arg]: Number of working server nodes to use");
		System.out
				.println("\t [5 arg]: Number of threads to use for each server node");
		System.out
				.println("\nNOTE: Make sure you have installed the Java(TM) \nCryptography Extension (JCE) Jurisdiction Policy \nFiles and that you are using Bouncy Castle as \nprimary JCE provider prior to execution. Check \nout [0] for more information.\n\n[0] http://znjp.com/mcdaniel/BC.html");
	}

	public static int[] getDictionaryMetadata(String filename, JobConf conf)
			throws IOException {
		FSDataInputStream in = null;
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(filename);
		in = fs.open(path);
		InputStream is = new BufferedInputStream(in);
		try {
			byte[] c = new byte[1024];
			int col_count = 0;
			int row_count = 0;
			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++row_count;
					}
					if (row_count == 0 && c[i] == ' ')
						++col_count;
				}
			}
			int[] metadata = { row_count, col_count };
			return metadata;
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) throws Exception {
		Security.insertProviderAt(
				new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);

		if (args.length < 5 || args.length > 5
				|| args[0].equalsIgnoreCase("-h")
				|| args[0].equalsIgnoreCase("--help")) {
			printHelp();
			System.exit(0);
		}

		JobConf conf = new JobConf(Processor.class);
		conf.setJobName("dictionaryattack");

		int[] dict_metadata = getDictionaryMetadata(args[0], conf);

		int MAPPERS = 0;
		try {
			MAPPERS = Integer.parseInt(args[3]);
		} catch (Exception e) {
			System.out
					.println("ERROR: The number of server nodes given is not a number.");
			System.exit(1);
		}
		int THREADS = 0;
		try {
			THREADS = Integer.parseInt(args[4]);
			if (THREADS > 0) {
				conf.setInt("THREADS", THREADS);
			} else {
				throw new Exception();
			}
			if (dict_metadata[1] >= THREADS) {
				conf.setInt("PASSWORDS_PER_LINE", dict_metadata[1]);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out
					.println("ERROR: The number of threads given is not a valid number. Make sure that the number of threads is greater than or equal to the number of passwords, per line, in the dictionary.\n\n Use the -h or --help option for more information.");
			System.exit(1);
		}

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(LongWritable.class);

		conf.setMapperClass(CDAMapper.class);

		conf.setInputFormat(NLineInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		conf.setNumReduceTasks(0);
		conf.setNumMapTasks(MAPPERS);

		conf.setCompressMapOutput(true);

		int dict_lines = dict_metadata[0];

		double lines_per_mapper = dict_lines / MAPPERS;

		if (Math.floor(lines_per_mapper) == lines_per_mapper) {
			conf.setInt("mapred.line.input.format.linespermap", dict_lines
					/ MAPPERS);
		} else {
			conf.setInt("mapred.line.input.format.linespermap",
					(dict_lines / MAPPERS) + 1);
		}

		DistributedCache.addCacheFile(new URI(args[2]), conf);
		JobClient.runJob(conf);
	}
}
