package com.gnb.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gnb.tranceme.MainActivity;



public class ConnectionChangeReceiver extends BroadcastReceiver {

	MainActivity mainActivity;


	public ConnectionChangeReceiver(Object object) {

		if (object instanceof MainActivity)
			this.mainActivity = (MainActivity) object;

	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		if (mainActivity != null)
			mainActivity.connectionListener(ConnectionChangeReceiver.this);

	}

}
