package item.finn.com.datacollection.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import item.finn.com.datacollection.CollectionInterface;
import item.finn.com.datacollection.service.MainService;

public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (!CollectionInterface.isServiceWorked(context, "item.finn.com.datacollection.service.LocationService")) {
			context.startService(new Intent(context, MainService.class));
		}
	}

}
