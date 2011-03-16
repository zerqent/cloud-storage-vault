package no.ntnu.item.csv.testutils;

public class ArrayComparison {

	public static boolean arraysAreEqual(Object[] first, Object[] second) {
		if (first.length != second.length) {
			return false;
		}

		for (int i = 0; i < second.length; i++) {
			if (first[i] != second[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean arraysAreEqual(byte[] first, byte[] second) {
		if (first.length != second.length) {
			return false;
		}

		for (int i = 0; i < second.length; i++) {
			if (first[i] != second[i]) {
				return false;
			}
		}
		return true;
	}

}
