package com.gnb.tranceme;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coboltforge.slidemenu.SlideMenu;
import com.coboltforge.slidemenu.SlideMenu.SlideMenuItem;
import com.coboltforge.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;
import com.gnb.WS.WSHelper;
import com.gnb.WS.WSHelperListener;
import com.gnb.connexions.Networking;
import com.gnb.coverflow.CoverFlow;
import com.gnb.coverflow.ImageAdapter;
import com.gnb.media.StreamingMediaPlayer;
import com.gnb.model.Dj;
import com.gnb.model.Hit;
import com.gnb.receivers.ConnectionChangeReceiver;

public class MainActivity extends Activity implements
		OnSlideMenuItemClickListener, WSHelperListener {

	public Animation upAnimation;
	public Animation downAnimation;
	public LinearLayout networkNotification;
	public FrameLayout networkcanvas;
	ConnectionChangeReceiver receiver;

	private ImageButton streamButton;
	private ImageButton playButton;
	private boolean isPlaying;
	private boolean encours = false;
	private StreamingMediaPlayer audioStreamer;
	public SlideMenu slidemenu;
	private ConnectivityManager manager;
	private List<Dj> listdjs = new ArrayList<Dj>(0);
	private boolean menuIsVisible = false;
	private List<Hit> hits;
	private AlertDialog alert;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// hide block notification network
		networkNotification = (LinearLayout) findViewById(R.id.network_layout);
		networkcanvas = (FrameLayout) networkNotification
				.findViewById(R.id.network_notification);
		networkNotification.setVisibility(View.INVISIBLE);

		// register receiver connection
		receiver = new ConnectionChangeReceiver(MainActivity.this);
		registerReceiver(receiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		manager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (Networking.isNetworkAvailable(getApplicationContext())) {
			Log.i("cc", "network!");
			WSHelper.getInstance().addWSHelperListener(MainActivity.this);
			WSHelper.getInstance().getDjs(manager, MainActivity.this);
			viewInit();

		}
	}

	private void viewInit() {
		slidemenu = (SlideMenu) findViewById(R.id.slideMenu);
		slidemenu.init(this, R.menu.slide, this, 333);
		slidemenu.setHeaderImage(getResources().getDrawable(
				R.drawable.playliste));
		alert = new AlertDialog.Builder(this).create();
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setMessage("loading tracks ...!");
		ctrlInit();
	}

	private void ctrlInit() {
		// TODO Auto-generated method stub
		((ImageView) findViewById(R.id.playList))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (menuIsVisible) {
							slidemenu.hide();
							menuIsVisible = false;
						} else {
							slidemenu.show();
							menuIsVisible = true;
						}
					}
				});
		streamButton = (ImageButton) findViewById(R.id.imageButton1);
		streamButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Play", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	// private void initControls() {
	// streamButton = (ImageButton) findViewById(R.id.imageButton1);
	// streamButton.setOnClickListener(new View.OnClickListener() {
	// public void onClick(View view) {
	// if (!encours)
	// startStreamingAudio();
	// else {
	// if (!isPlaying
	// && !audioStreamer.getMediaPlayer().isPlaying()) {
	//
	// audioStreamer.getMediaPlayer().start();
	// audioStreamer.startPlayProgressUpdater();
	// }
	// Toast t = new Toast(MainActivity.this);
	// t.setText("error !!!");
	// t.show();
	// isPlaying = !isPlaying;
	// }
	// }
	// });
	//
	// playButton = (ImageButton) findViewById(R.id.imageButton2);
	// playButton.setEnabled(false);
	// playButton.setOnClickListener(new View.OnClickListener() {
	// public void onClick(View view) {
	// if (audioStreamer.getMediaPlayer().isPlaying()) {
	// audioStreamer.getMediaPlayer().pause();
	// }
	// isPlaying = !isPlaying;
	// }
	// });
	// }

	private void startStreamingAudio() {
		try {
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
			if (audioStreamer != null) {
				audioStreamer.interrupt();
			}
			audioStreamer = new StreamingMediaPlayer(this, null, playButton,
					streamButton, progressBar);
			audioStreamer.startStreaming(
					"http://a.tumblr.com/tumblr_lvqijtNrYm1r2apjao1.mp3",
					200000, 8000);
			streamButton.setEnabled(false);
			encours = true;
		} catch (IOException e) {
			Log.e(getClass().getName(), "Error starting to stream audio.", e);
		}

	}

	public void connectionListener(Object object) {

		if (object instanceof ConnectionChangeReceiver) {

			showConnectionLost();
		}

	}

	@Override
	public void onBackPressed() {

		overridePendingTransition(0, 0);
		super.onBackPressed();
	}

	@Override
	protected void onResume() {

		super.onResume();
		registerReceiver(receiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

	}

	@Override
	protected void onPause() {

		super.onPause();

		// unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(receiver);
		super.onDestroy();

	}

	private void showConnectionLost() {

		final AnimationListener makeAppears = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {

				networkNotification
						.setLayoutAnimation(new LayoutAnimationController(
								upAnimation));
				networkNotification.startLayoutAnimation();

			}
		};

		final AnimationListener makeDisappears = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				networkNotification.setVisibility(View.GONE);

			}
		};

		upAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_top_up);
		downAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_top_down);

		upAnimation.setAnimationListener(makeDisappears);
		downAnimation.setAnimationListener(makeAppears);

		if (!Networking.isNetworkAvailable(getApplicationContext())) {

			networkNotification.setVisibility(View.VISIBLE);
			networkNotification
					.setLayoutAnimation(new LayoutAnimationController(
							downAnimation));
			networkNotification.startLayoutAnimation();

		}

	}

	@Override
	public void onSlideMenuItemClick(int itemId) {
		// TODO Auto-generated method stub
//		Toast.makeText(this, "" + itemId, Toast.LENGTH_SHORT).show();
		alert.show();
		WSHelper.getInstance().gethitsById(itemId, manager, MainActivity.this);
		menuIsVisible = false;
	}

	@Override
	public void onDjsLoaded(List<Dj> djs) {
		// TODO Auto-generated method stub
		listdjs.addAll(djs);
		for (Dj dj : djs) {
			SlideMenuItem item = new SlideMenuItem();
			item.id = dj.getId();
			item.icon = getResources().getDrawable(R.drawable.music);
			item.label = dj.getName();
			slidemenu.addMenuItem(item);
		}

		((ProgressBar) findViewById(R.id.progressLoding))
				.setVisibility(View.GONE);
		((ImageView) findViewById(R.id.playList)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.playList)).setClickable(true);

	}

	@Override
	public void onErrorLoadingDj() {
		// TODO Auto-generated method stub
		Log.i("MainActivity", "onErrorLoadingDj");
	}

	@Override
	public void onHitsLoaded(List<Hit> hits) {
		// TODO Auto-generated method stub
		this.hits = new ArrayList<Hit>(hits.size());
		this.hits.addAll(hits);
		ImageView[] imgView = new ImageView[hits.size()];
		for (int i = 0; i < hits.size(); i++) {
			ImageView iv = new ImageView(this);
			Hit hit = hits.get(i);
			if(hit.getImg().length()==0)
				hit.setImg("http://www.djluv.in/music/images/albums/1330236629_its-cover-not-found.jpg");
			Log.i("img", "" + hit.img);
			try {
				URL url = new URL(hit.img);
				URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
				iv.setImageDrawable(Drawable.createFromStream(
						(InputStream) new URL(uri.toString()).getContent(),
						""));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iv.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
			iv.setScaleType(ImageView.ScaleType.MATRIX);
			imgView[hits.indexOf(hit)] = iv;
			
		}
		Log.i("imgView", "" + imgView.length);
		 CoverFlow coverFlow = (CoverFlow) findViewById(R.id.coverFlow1);
		
		 ImageAdapter coverImageAdapter = new ImageAdapter(this,imgView);
		
//		 coverImageAdapter.createReflectedImages();
		
		 coverFlow.setAdapter(coverImageAdapter);
		
		 coverFlow.setSpacing(-15);
		 coverFlow.setSelection(0, true);
		 alert.dismiss();
	}

	@Override
	public void onErrorLoadingHit() {
		// TODO Auto-generated method stub

	}
//	public static Bitmap getBitmapFromURL(String src) {
//	    try {
//	        URL url = new URL(src);
//	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//	        connection.setDoInput(true);
//	        connection.connect();
//	        InputStream input = connection.getInputStream();
//	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
//	        return myBitmap;
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	        return null;
//	    }
//	}
}
