package no.ntnu.item.bruteforce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Security;
import java.util.Stack;

import no.ntnu.item.threads.BruteForceThread;
import no.ntnu.item.threads.DictionaryThread;

public class BruteForce {

	public static int THREADS;
	public static int MAX_WORD_LENGTH;
	public static boolean found = false;
	public static FileReader fr;
	public static BufferedReader buf;

	public static long start;
	public static double time;
	public static long words_read;

	public static byte[] salt;
	public static byte[] cipher;
	public static Stack<String> words;

	private char[] current_word;
	private BruteForceThread[] bf_threads;

	public BruteForce(String[] args) throws IOException {
		File tmp = new File(args[0]);
		if (!tmp.exists()) {
			System.out.println("ERROR: Target file does not exist!");
			printHelp();
		}
		FileInputStream in = new FileInputStream(tmp);
		byte[] b = new byte[(int) tmp.length()];
		in.read(b);
		in.close();
		salt = new byte[16];
		cipher = new byte[b.length - salt.length];
		System.arraycopy(b, 0, salt, 0, salt.length);
		System.arraycopy(b, salt.length, cipher, 0, cipher.length);
		words_read = 0;
		start = 0;
	}

	public void bruteForceAttack(String[] input) {
		System.out.println("************************************************");
		System.out.println("\tRunning brute force attack");
		System.out.println("************************************************");
		current_word = new char[1];
		words = new Stack<String>();
		THREADS = Integer.parseInt(input[2]);
		bf_threads = new BruteForceThread[THREADS];
		MAX_WORD_LENGTH = Integer.parseInt(input[1]);
		for (int i = 0; i < bf_threads.length; i++) {
			bf_threads[i] = new BruteForceThread("bf" + i);
		}

		waitForBFThreads();

		while (!found) {
			if (current_word.length > MAX_WORD_LENGTH)
				break;
			pushWord(current_word.length - 1);
			char[] tmp = new char[current_word.length + 1];
			tmp[tmp.length - 1] = ' ';
			System.arraycopy(current_word, 0, tmp, 0, current_word.length);
			current_word = tmp;
		}
	}

	public void dictionaryAttack(String[] input) {
		System.out.println("************************************************");
		System.out.println("\tRunning dictionary attack");
		System.out.println("************************************************");
		File dict = new File(input[1]);
		THREADS = Integer.parseInt(input[2]);
		if (dict.exists()) {
			try {
				fr = new FileReader(dict);
				buf = new BufferedReader(fr);
				start = System.currentTimeMillis();
				for (int i = 0; i < THREADS; i++) {
					new DictionaryThread("dict" + i);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("ERROR: Dictionary file does not exist!");
			printHelp();
			System.exit(0);
		}
	}

	public boolean pushWord(int pos) {
		// Iterating over a chosen space of characters
		for (int i = 32; i < 127; i++) {
			current_word[pos] = ((char) i);
			if (pos == 0) {
				synchronized (words) {
					words.push(new String(current_word));
					if (words.size() == THREADS && start == 0) {
						start = System.currentTimeMillis();
						words.notifyAll();
					}
				}
			}

			if (pos > 0) {
				if (pushWord(pos - 1))
					return true;
			}
		}
		return false;
	}

	private void waitForBFThreads() {
		while (true) {
			int done = 0;
			for (BruteForceThread bft : bf_threads) {
				if (bft.thread.getState() == Thread.State.WAITING) {
					done++;
				}
				if (done == bf_threads.length) {
					return;
				}
			}
		}
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

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
				.println("\nNOTE: Make sure you have installed the Java(TM) \nCryptography Extension (JCE) Jurisdiction Policy \nFiles and that you are using Bouncy Castle as \nprimary JCE provider prior to execution. Check \nout [0] for more information.\n\n[0] http://znjp.com/mcdaniel/BC.html");

	}

	public static void printSuccess(String word) {
		System.out.println("SUCCESS! Password is { " + new String(word) + " }");
		System.out.println("Read " + words_read + " passwords in " + time
				+ " seconds");
		System.out.println("Average speed: " + words_read / time
				+ " passwords/second");
	}

	public static void printFail() {
		time = (System.currentTimeMillis() - start) / 1000.0;
		System.out.println("Did not find password");
		System.out.println("Read " + words_read + " passwords in " + time
				+ " seconds");
		System.out.println("Average speed: " + words_read / time
				+ " passwords/second");
	}

	public static void main(String[] args) throws IOException {
		Security.insertProviderAt(
				new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
		if (args.length == 3 && !isInteger(args[0])) {
			BruteForce bf = new BruteForce(args);
			if (isInteger(args[1])) {
				bf.bruteForceAttack(args);
			} else {
				bf.dictionaryAttack(args);
			}
		} else {
			printHelp();
		}
	}

}
