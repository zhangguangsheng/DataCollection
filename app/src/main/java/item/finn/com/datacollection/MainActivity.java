package item.finn.com.datacollection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(item.finn.com.datacollection.R.layout.activity_current_location);
		new CollectionInterface().init(this);
	}

}
