public void dictionaryAttack(String[] input) {
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
