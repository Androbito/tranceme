package com.gnb.WS;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.util.Log;

import com.gnb.parser.DjsParser;
import com.gnb.parser.HitsParser;
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

		WebThread wt = new WebThread(urlDjs, WebThread.METHOD_GET, manager,
				WebThread.ENCODING_UTF_8, false);
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

	public void gethitsById(int itemId, ConnectivityManager manager,
			final Activity context) {
		// TODO Auto-generated method stub
		String urlhits = Constants.WSbyDJ + itemId;
		WebThread wt = new WebThread(urlhits, WebThread.METHOD_GET, manager,
				WebThread.ENCODING_UTF_8, false);
		wt.setListener(new WebListener() {
			public void onFinish(String url, String resultat) {
				Log.i("WSHelper", resultat);
				onFinishGethits(resultat, context);
			}

			public void onError(WebException error) {
				onErrorGetHits(context);
			}
		});
		wt.start();
	}

	protected void onErrorGetHits(Activity context) {
		// TODO Auto-generated method stub
		Log.i("WSHelper", "onErrorGethits");
	}

	protected void onFinishGethits(String resultat, Activity context) {
		// TODO Auto-generated method stub
		final HitsParser parser = new HitsParser(resultat);

		try {
			if (!parser.parse()) {
				onErrorGetHits(context);
				return;
			}

			for (WSHelperListener listener : wsHelperListeners) {
				listener.onHitsLoaded(parser.getObjects());
			}
			Log.i("WSHelper", "onFinishGethits");
		} catch (Exception e) {
			onErrorGetHits(context);
		}
	}

	protected void onErrorGetDjs(Activity context) {
		// TODO Auto-generated method stub
		Log.i("WSHelper", "onErrorGetDjs");
		for (WSHelperListener listener : wsHelperListeners) {
			listener.onErrorLoadingDj();
		}
	}

	protected void onFinishGetDjs(String resultat, Activity context) {
		// TODO Auto-generated method stub
		Log.i("WSHelper", "onFinishGetDjs");
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