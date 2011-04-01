package no.ntnu.item.csv;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class CSVActivityTest extends ActivityUnitTestCase<CSVActivity> {

	public CSVActivityTest(Class<CSVActivity> activityClass) {
		super(activityClass);
	}

	// private CSVActivity mActivity;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testFileManagerIsNullWithCleanContext() {
		setActivityContext(getInstrumentation().getContext());
		final CSVActivity ac = startActivity(new Intent(Intent.ACTION_MAIN),
				null, null);
		assertNull(CSVActivity.fm);
	}

}
