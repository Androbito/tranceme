package com.gnb.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.gnb.coverflow.CoverFlow;
import com.gnb.coverflow.ImageAdapter;
import com.gnb.tranceme.MainActivity;
import com.gnb.tranceme.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	private String url;
	private List<Bitmap> mlistBit;

	public BitmapDownloaderTask(List<Bitmap> listBit) {
		mlistBit = listBit;
	}

	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
		return download_Image(params[0]);
	}

	private Bitmap download_Image(String url) {
		// ---------------------------------------------------
		Bitmap bm = null;
		if (url.length() == 0)
			url = "http://tourismapp.olympe.in/tourismapp/images/Maroc.png";
		Log.i("Url", url);
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {
			Log.e("Hub", "Error getting the image from server : "
					+ e.getMessage().toString());
		}
		return bm;
	}

	@Override
	// Once the image is downloaded, associates it to the imageView
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (bitmap != null) {
			mlistBit.add(bitmap);
		}
	}
}