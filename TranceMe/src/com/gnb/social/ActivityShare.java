package com.gnb.social;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gnb.tranceme.R;

public class ActivityShare extends Activity {

	private EditText editShare;
	private Button buttonClose, buttonShare;
	private static final String FACEBOOK_APPID = "109744575882985";
	private static final String FACEBOOK_PERMISSION = "publish_stream";
	private static final String TAG = "FacebookSample";

	private final Handler mFacebookHandler = new Handler();
	private FacebookConnector facebookConnector;

	final Runnable mUpdateFacebookNotification = new Runnable() {
		public void run() {
			finish();
			Toast.makeText(getBaseContext(), "Facebook updated !",
					Toast.LENGTH_LONG).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_share_fb);
		this.facebookConnector = new FacebookConnector(FACEBOOK_APPID, this,
				getApplicationContext(), new String[] { FACEBOOK_PERMISSION });
		editShare = (EditText) findViewById(R.id.txtRespondFb);
			editShare.setText("I'm listening to "
					+ getIntent().getStringExtra("titleTrack") + " on TranceMe");

		buttonShare = (Button) findViewById(R.id.btnShareFb);
		buttonShare.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				postMessage(editShare.getText().toString(), getIntent()
						.getStringExtra("urlImgTrack"));
			}
		});
		buttonClose = (Button) findViewById(R.id.btnCloseDialog);
		buttonClose.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
	}

	public void postMessage(final String s, final String u) {

		if (facebookConnector.getFacebook().isSessionValid()) {
			postMessageInThread(s, u);
		} else {
			SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {

				@Override
				public void onAuthSucceed() {
					postMessageInThread(s, u);
				}

				@Override
				public void onAuthFail(String error) {

				}
			};
			SessionEvents.addAuthListener(listener);
			facebookConnector.login();
		}

	}

	private void postMessageInThread(final String s, final String u) {
		Thread t = new Thread() {
			public void run() {

				try {
					facebookConnector.postMessageOnWall(s, u);
					mFacebookHandler.post(mUpdateFacebookNotification);
				} catch (Exception ex) {
					Log.e(TAG, "Error sending msg", ex);
				}
			}
		};
		t.start();
	}

}
