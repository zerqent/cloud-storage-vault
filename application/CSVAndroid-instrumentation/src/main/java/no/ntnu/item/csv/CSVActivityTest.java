package no.ntnu.item.csv;

import android.test.ActivityInstrumentationTestCase2;

public class CSVActivityTest extends
		ActivityInstrumentationTestCase2<CSVActivity> {

	private CSVActivity mActivity;

	public CSVActivityTest() {
		super("no.ntnu.item.csv", CSVActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		this.mActivity = getActivity();
	}

	public void testPreConditions() throws Exception {
		assertTrue(mActivity.myString.equals("foobar"));
		assertTrue(mActivity.myString.equals("foobar"));
	}

}
