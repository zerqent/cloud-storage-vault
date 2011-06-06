public void bruteForceAttack(String[] input) {
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
		System.arraycopy(current_word, 0, tmp, 0, 
                current_word.length);
		current_word = tmp;
	}
}
