package moe.feng.nyanpasu.tile.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.util.CmdUtils;
import moe.feng.nyanpasu.tile.util.Settings;

public class RootCheckActivity extends Activity {

	private TextView mStateView;

	private static final int STATE_FAILED = 0, STATE_FINISH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_root_check);

		mStateView = (TextView) findViewById(R.id.check_state_text);

		new Thread(new RootCheckRunnable()).start();
	}

	private class RootCheckRunnable implements Runnable {

		@Override
		public void run() {
			boolean isRooted = CmdUtils.isRooted();
			if (isRooted) {
				int code = CmdUtils.execRootCmdForExitCode(new String[]{"md /data/.root"});
				Log.i("Code", code + ",");
				if (code != -42 && code != -911 && code != 1) {
					mHandler.sendEmptyMessage(STATE_FINISH);
					return;
				}
			}
			mHandler.sendEmptyMessage(STATE_FAILED);
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case STATE_FAILED:
					mStateView.setText(R.string.root_check_state_failed);
					mStateView.setTextColor(Color.RED);
					break;
				case STATE_FINISH:
					mStateView.setText(R.string.root_check_state_finish);
					Settings.getHelper(RootCheckActivity.this, "root").put("root_tip", true);
					break;
			}
		}
	};

}
