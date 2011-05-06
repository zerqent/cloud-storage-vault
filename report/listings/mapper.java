@Override
public void map(LongWritable key, Text value,
		OutputCollector<Text, LongWritable> output, Reporter reporter)
		throws IOException {

	String[] line = value.toString().split(" ");
	String[] line_chunk = new String[WORDS_PER_THREAD];

	// Create threads to check words in line
	for (int i = 0, c = 0; i < (THREADS * WORDS_PER_THREAD)
			&& i < line.length; i += WORDS_PER_THREAD, c++) {
		if (line.length - i >= WORDS_PER_THREAD) {
			System.arraycopy(line, i, line_chunk, 0, WORDS_PER_THREAD);
		} else {
			System.arraycopy(line, i, line_chunk, 0, line.length - i);
		}
		dictionary_threads[c] = new DictionaryThread("dict" + i, line_chunk);
	}

	// Wait for all threads to finish
	for (DictionaryThread dt : dictionary_threads) {
		try {
			dt.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	if (!password.equals("")) {
		output.collect(new Text("Password is [ " + password
				+ " ]. Found at"),
				new LongWritable(System.currentTimeMillis()));
	}
}
