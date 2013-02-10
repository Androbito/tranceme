package com.gnb.tranceme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.gnb.tranceme.R;
import com.gnb.WS.WSHelper;
import com.gnb.WS.WSHelperListener;
import com.gnb.connexions.Networking;
import com.gnb.coverflow.CoverFlow;
import com.gnb.coverflow.ImageAdapter;
import com.gnb.media.StreamingMediaPlayer;
import com.gnb.model.Dj;
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
		CoverFlow coverFlow = (CoverFlow) findViewById(R.id.coverFlow1);

		ImageAdapter coverImageAdapter = new ImageAdapter(this);

		coverImageAdapter.createReflectedImages();

		coverFlow.setAdapter(coverImageAdapter);

		coverFlow.setSpacing(-15);
		coverFlow.setSelection(1, true);
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

	private void initControls() {
		streamButton = (ImageButton) findViewById(R.id.imageButton1);
		streamButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!encours)
					startStreamingAudio();
				else {
					if (!isPlaying
							&& !audioStreamer.getMediaPlayer().isPlaying()) {

						audioStreamer.getMediaPlayer().start();
						audioStreamer.startPlayProgressUpdater();
					}
					Toast t = new Toast(MainActivity.this);
					t.setText("error !!!");
					t.show();
					isPlaying = !isPlaying;
				}
			}
		});

		playButton = (ImageButton) findViewById(R.id.imageButton2);
		playButton.setEnabled(false);
		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (audioStreamer.getMediaPlayer().isPlaying()) {
					audioStreamer.getMediaPlayer().pause();
				}
				isPlaying = !isPlaying;
			}
		});
	}

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
		Toast.makeText(this, ""+itemId, Toast.LENGTH_SHORT)
				.show();
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
			slidemenu.addMenuItem(item);
			slidemenu.addMenuItem(item);
			slidemenu.addMenuItem(item);
		}

	}

	@Override
	public void onErrorLoadingDj() {
		// TODO Auto-generated method stub
		Log.i("MainActivity", "onErrorLoadingDj");
	}

}
