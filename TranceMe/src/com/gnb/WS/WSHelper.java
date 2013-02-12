package com.gnb.WS;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.util.Log;

import com.gnb.parser.DjsParser;
import com.gnb.utils.Constants;
import com.gnb.web.WebException;
import com.gnb.web.WebListener;
import com.gnb.web.WebThread;

@SuppressLint("SimpleDateFormat")
public class WSHelper {
	private static WSHelper instance;

	public static WSHelper getInstance() {
		if (instance == null) {
			instance = new WSHelper();
		}
		return instance;
	}

	private Set<WSHelperListener> wsHelperListeners;

	private WSHelper() {
		wsHelperListeners = new HashSet<WSHelperListener>();
	}
	public void getDjs(ConnectivityManager manager, final Activity context) {
		String urlDjs = Constants.WSallDJ;

		WebThread wt = new WebThread(urlDjs, WebThread.METHOD_GET,
				manager, WebThread.ENCODING_UTF_8, false);
		wt.setListener(new WebListener() {
			public void onFinish(String url, String resultat) {
				onFinishGetDjs(resultat, context);
			}

			public void onError(WebException error) {
				onErrorGetDjs(context);
			}
		});
		wt.start();
	}
	protected void onErrorGetDjs(Activity context) {
		// TODO Auto-generated method stub
		Log.i("WSHelper","onErrorGetDjs");
		for (WSHelperListener listener : wsHelperListeners) {
			listener.onErrorLoadingDj();
		}
	}
	protected void onFinishGetDjs(String resultat, Activity context) {
		// TODO Auto-generated method stub
		Log.i("WSHelper","onFinishGetDjs");
		final DjsParser parser = new DjsParser(resultat);

		try {
			if (!parser.parse()) {
				onErrorGetDjs(context);
				return;
			}

			context.runOnUiThread(new Runnable() {
				public void run() {
					for (WSHelperListener listener : wsHelperListeners) {
						listener.onDjsLoaded(parser.getObjects());
					}
				}
			});
		} catch (Exception e) {
			onErrorGetDjs(context);
		}
	}
	public void addWSHelperListener(WSHelperListener listener) {
		wsHelperListeners.add(listener);
	}

	public void removeWSHelperListener(WSHelperListener listener) {
		wsHelperListeners.remove(listener);
	}
}