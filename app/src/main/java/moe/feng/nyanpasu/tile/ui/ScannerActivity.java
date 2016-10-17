package moe.feng.nyanpasu.tile.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.Result;
import com.melnykov.fab.FloatingActionButton;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.util.ClipboardUtils;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

	private FloatingActionButton mFAB;
	private ImageButton mGalleryBtn;
	private ZXingScannerView mScannerView;

	private static final int REQUEST_PERMISSION = 20001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);

		mScannerView = (ZXingScannerView) findViewById(R.id.scanner_view);
		mFAB = (FloatingActionButton) findViewById(R.id.btn_exit);
		mGalleryBtn = (ImageButton) findViewById(R.id.btn_gallery);

		mFAB.setOnClickListener((view) -> {
			onBackPressed();
		});

		if (!isCameraPermissionGranted()) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.dialog_explanation_permission_title)
						.setMessage(R.string.dialog_explanation_permission_message)
						.setPositiveButton(R.string.dialog_explanation_permission_pos_btn, (dialogInterface, i) -> jumpToSettings())
						.setNegativeButton(android.R.string.cancel, null)
						.show();
			} else {
				ActivityCompat.requestPermissions(
						this,
						new String[]{Manifest.permission.CAMERA},
						REQUEST_PERMISSION
				);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isCameraPermissionGranted()) {
			startScan();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startScan();
			}
		}
	}

	private void startScan() {
		mScannerView.setResultHandler(this);
		mScannerView.startCamera();
	}

	private boolean isCameraPermissionGranted() {
		return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	private void jumpToSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getApplication().getPackageName()));
		startActivity(intent);
	}


	@Override
	public void handleResult(Result result) {
		String text = result.getText();
		if (text.contains("://")) {
			try {
				Uri uri = Uri.parse(text);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				finish();
			} catch (Exception e) {

			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_result)
					.setMessage(text)
					.setPositiveButton(R.string.dialog_result_copy_btn, (dialogInterface, i) -> {
						ClipboardUtils.putString(this, text);
						Toast.makeText(
								this,
								R.string.pref_donate_alipay_address_copied,
								Toast.LENGTH_LONG
						).show();
					})
					.setNegativeButton(android.R.string.ok, null)
					.show();
		}
	}

}
