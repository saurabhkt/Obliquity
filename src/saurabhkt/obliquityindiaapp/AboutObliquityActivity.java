package saurabhkt.obliquityindiaapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AboutObliquityActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.aboutobliquity);
	
	    // TODO Auto-generated method stub
	}

}
