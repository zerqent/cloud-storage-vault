package no.ntnu.item.csv;

import no.ntnu.item.cryptoutil.Cryptoutil;
import android.app.Activity;
import android.os.Bundle;

public class CSVActivity extends Activity {
	/** Called when the activity is first created. */

	public String myString = "foobar";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		System.out.println(Cryptoutil.ASYM_CIPHER);
		System.out.println(Cryptoutil.ASYM_CIPHER);

	}
}
