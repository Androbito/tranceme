package com.gnb.tranceme;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.widget.TextView;
import android.widget.Toast;

import com.coboltforge.slidemenu.SlideMenu;
import com.coboltforge.slidemenu.SlideMenu.SlideMenuItem;
import com.coboltforge.slidemenu.SlideMenuInterface.OnSlideMenuItemClickListener;
import com.gnb.WS.WSHelper;
import com.gnb.WS.WSHelperListener;
import com.gnb.connexions.Networking;
import com.gnb.coverflow.CoverAdapterView;
import com.gnb.coverflow.CoverAdapterView.OnItemSelectedListener;
import com.gnb.coverflow.CoverFlow;
import com.gnb.coverflow.ImageAdapter;
import com.gnb.model.Dj;
import com.gnb.model.Hit;
import com.gnb.receivers.ConnectionChangeReceiver;
import com.gnb.social.ActivityShare;
import com.gnb.sqlite.DatabaseHandler;

public class MainActivity extends Activity implements
		OnSlideMenuItemClickListener, WSHelperListener {

	public Animation upAnimation;
	public Animation downAnimation;
	public LinearLayout networkNotification;
	public FrameLayout networkcanvas;
	ConnectionChangeReceiver receiver;
	private ImageButton playButton, pauseButton, stopButton;
	ImageView favorisButton, shareButton, fbShare;
	public SlideMenu slidemenu;
	private ConnectivityManager manager;
	private List<Dj> listdjs = new ArrayList<Dj>(0);
	private boolean menuIsVisible = false;
	private List<Hit> hits, favHits;
	private AlertDialog alert;
	private CoverFlow coverFlow;
	private Hit hit, currentHit;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	DatabaseHandler db;
	protected ProgressDialog mProgressDialog;

	public Hit getHit() {
		return hit;
	}

	public void setHit(Hit hit) {
		this.hit = hit;
	}

	public Hit getCurrentHit() {
		return currentHit;
	}

	public void setCurrentHit(Hit currentHit) {
		this.currentHit = currentHit;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// hide block notification network
		db = new DatabaseHandler(this);
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
		alert.setMessage("Loading playlist ...!");
		coverFlow = (CoverFlow) findViewById(R.id.coverFlow1);
		slideInit();
	}

	private void slideInit() {
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
	}

	private void initControls() {
		fbShare = (ImageView) findViewById(R.id.fbShare);
		fbShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MainActivity.this.getCurrentHit() != null) {
					Intent i = new Intent(MainActivity.this,
							ActivityShare.class);
					i.putExtra("titleTrack",
							MainActivity.this.getCurrentHit().title);
					i.putExtra("urlImgTrack",
							MainActivity.this.getCurrentHit().img);
					((LinearLayout) findViewById(R.id.shareLay))
							.setVisibility(View.GONE);
					startActivity(i);
				} else {
					Toast.makeText(MainActivity.this, "Play a track please!",
							Toast.LENGTH_LONG).show();
					((LinearLayout) findViewById(R.id.shareLay))
					.setVisibility(View.GONE);
				}

			}
		});
		shareButton = (ImageView) findViewById(R.id.share);
		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (((LinearLayout) findViewById(R.id.shareLay))
						.getVisibility() == View.VISIBLE) {
					((LinearLayout) findViewById(R.id.shareLay))
							.setVisibility(View.GONE);
				} else {
					((LinearLayout) findViewById(R.id.shareLay))
							.setVisibility(View.VISIBLE);
				}

			}
		});
		playButton = (ImageButton) findViewById(R.id.imageButton1);
		playButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				mProgressDialog.setMessage("Track loading...");
				mProgressDialog.show();
				new Thread((new Runnable() {
					Message msg = null;

					public void run() {
						try {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
								mediaPlayer.reset();
							}
							Log.i("ok", MainActivity.this.getHit().url);
							play(new URL(MainActivity.this.getHit().url));
							MainActivity.this.setCurrentHit(MainActivity.this
									.getHit());
							msg = mHandler.obtainMessage(1);
							// sends the message to our handler
							mHandler.sendMessage(msg);
							mediaPlayer.start();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				})).start();
			}
		});

		pauseButton = (ImageButton) findViewById(R.id.imageButton2);
		pauseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				}
			}
		});
		stopButton = (ImageButton) findViewById(R.id.imageButton3);
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.reset();
				}

			}
		});
		favorisButton = (ImageView) findViewById(R.id.favoris);
		favorisButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				db.addHitToFavoris(MainActivity.this.hit);
				favorisButton.setImageResource(R.drawable.etoilefav);
			}
		});
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mProgressDialog.dismiss();
				break;
			}
		}
	};

	protected void play(URL url) {
		// TODO Auto-generated method stub
		try {
			mediaPlayer.setDataSource(url.toString());
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// Toast.makeText(this, "" + itemId, Toast.LENGTH_SHORT).show();

		if (itemId != -1) {
			alert.show();
			WSHelper.getInstance().gethitsById(itemId, manager,
					MainActivity.this);
			menuIsVisible = false;
		} else {
			alert.show();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					favHits = new ArrayList<Hit>(0);
					favHits.addAll(db.getFavHits());
					Log.i("t", "" + favHits.size());
					if (favHits.size() == 0)
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								alert.dismiss();
								Toast.makeText(getApplicationContext(),
										"No saved Favorites ",
										Toast.LENGTH_LONG).show();
							}
						});
					else
						onHitsLoaded(favHits);
				}
			});
			t.start();

			menuIsVisible = false;
		}

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
		SlideMenuItem item = new SlideMenuItem();
		item.id = -1;
		item.icon = getResources().getDrawable(R.drawable.music);
		item.label = "Favoris";
		slidemenu.addMenuItem(item);
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
	public void onHitsLoaded(final List<Hit> hits) {

		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		MainActivity.this.hits = new ArrayList<Hit>(hits.size());
		MainActivity.this.hits.addAll(hits);
		final ImageView[] imgView = new ImageView[hits.size()];
		for (int i = 0; i < hits.size(); i++) {
			ImageView iv = new ImageView(MainActivity.this);
			Hit hit = hits.get(i);
			if (hit.getImg().length() == 0)
				hit.setImg("http://www.djluv.in/music/images/albums/1330236629_its-cover-not-found.jpg");
			Log.i("img", "" + hit.img);
			try {
				URL url = new URL(hit.img);
				URI uri = new URI(url.getProtocol(), url.getHost(),
						url.getPath(), url.getQuery(), null);
				iv.setImageBitmap(createReflectedImages(getBitmapFromInputStream((InputStream) new URL(
						uri.toString()).getContent())));
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
			iv.setLayoutParams(new CoverFlow.LayoutParams(120, 180));
			iv.setScaleType(ImageView.ScaleType.MATRIX);
			imgView[hits.indexOf(hit)] = iv;

		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Log.i("imgView", "" + imgView.length);
				ImageAdapter coverImageAdapter = new ImageAdapter(
						MainActivity.this, imgView);

				coverFlow.setAdapter(coverImageAdapter);

				coverFlow.setSpacing(-15);
				coverFlow.setSelection(0, true);
				((TextView) findViewById(R.id.hititle))
						.setText(MainActivity.this.hits.get(0).title);

				MainActivity.this.setHit(MainActivity.this.hits.get(0));
				coverFlow
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(
									CoverAdapterView<?> parent, View view,
									int position, long id) {
								// TODO Auto-generated method stub
								MainActivity.this.setHit(MainActivity.this.hits
										.get(position));
								((TextView) findViewById(R.id.hititle))
										.setText(MainActivity.this.getHit().title);
								if (db.isFav(MainActivity.this.hit))
									favorisButton
											.setImageResource(R.drawable.etoilefav);
								else
									favorisButton
											.setImageResource(R.drawable.etoile);
							}

							@Override
							public void onNothingSelected(
									CoverAdapterView<?> parent) {
								// TODO Auto-generated method stub
							}
						});
				initControls();
				alert.dismiss();
			}
		});
	}

	@Override
	public void onErrorLoadingHit() {
		// TODO Auto-generated method stub
		Log.i("MainActivity", "onErrorLoadingHit");
	}

	public Bitmap getBitmapFromInputStream(InputStream input) {
		Bitmap myBitmap = BitmapFactory.decodeStream(input);
		return myBitmap;
	}

	public Bitmap createReflectedImages(Bitmap originalImage) {
		// The gap we want between the reflection and the original image
		final int reflectionGap = 4;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// Create a Bitmap with the flip matrix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// Create a shader that is a linear gradient that covers the
		// reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}
}
