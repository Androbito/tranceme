package com.gnb.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

	ProgressBar p = null;
	ImageView imageView = null;

	public DownloadImagesTask(ProgressBar pbr) {
		this.p = pbr;

	}

	public DownloadImagesTask() {

	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// Toast.makeText(null,"h :"+imageView.getHeight()+imageView.getWidth(),
		// 5000).show();
		imageView.setImageBitmap(result);
		// mcv.pBarChannel.setVisibility(View.GONE);
		if (p != null) {
			p.setVisibility(View.GONE);
		}
	}

	private Bitmap download_Image(String url) {
		// ---------------------------------------------------
		Bitmap bm = null;
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
		// ---------------------------------------------------
	}

	/*
	 * protected void doInBackground(ProgressBar... params) { //this.imageView =
	 * (ImageButton)params[0]; this.p = (ProgressBar)params[0]; return
	 * download_Image((String)imageView.getTag()); }
	 */

	protected void onPreExecute() {
		super.onPreExecute();

	}

	protected void onProgressUpdate(String... progress) {

	}

	@Override
	protected Bitmap doInBackground(ImageView... params) {
		// TODO Auto-generated method stub
		this.imageView = (ImageView) params[0];

		return download_Image((String) imageView.getTag());
	}
}
