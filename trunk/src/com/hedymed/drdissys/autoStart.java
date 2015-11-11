package com.hedymed.drdissys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class autoStart extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
        	Intent mIntent = new Intent(context, MainActivity.class);
    		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
    		context.startActivity(mIntent);//no start at boot complete yet.
            return;
        }
	}
}
