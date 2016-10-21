package moe.feng.nyanpasu.tile.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.ui.fragment.settings.SettingAbout;
import moe.feng.nyanpasu.tile.ui.fragment.settings.SettingMain;
import moe.feng.nyanpasu.tile.ui.fragment.settings.SettingScanQr;

public class SettingsActivity extends Activity {

	private int mFlag = 0;

	public static final int FLAG_MAIN = 0, FLAG_ABOUT = 1, FLAG_SCAN_QR = 2;

	public static final String EXTRA_FLAG = "flag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mFlag = intent.getIntExtra(EXTRA_FLAG, FLAG_MAIN);

		setContentView(R.layout.activity_settings);

		getActionBar().setDisplayHomeAsUpEnabled(mFlag != FLAG_MAIN);

		setupFragment();
	}

	private void setupFragment() {
		Fragment fragment = null;
		switch (mFlag) {
			case FLAG_MAIN:
				fragment = new SettingMain();
				break;
			case FLAG_ABOUT:
				fragment = new SettingAbout();
				break;
			case FLAG_SCAN_QR:
				fragment = new SettingScanQr();
				break;
		}

		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	public static void launch(Activity parentActivity, int flag) {
		Intent intent = new Intent(parentActivity, SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(EXTRA_FLAG, flag);
		parentActivity.startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return false;
	}

}
