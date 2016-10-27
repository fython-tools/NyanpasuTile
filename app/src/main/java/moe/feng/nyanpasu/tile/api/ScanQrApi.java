package moe.feng.nyanpasu.tile.api;

import android.content.Intent;
import android.net.Uri;
import android.service.quicksettings.TileService;

import moe.feng.nyanpasu.tile.ui.ScannerActivity;

public class ScanQrApi {

	public static void openScanner(TileService context) {
		Intent intent = new Intent(context, ScannerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivityAndCollapse(intent);
	}

	public static boolean openWeChatScan(TileService context) {
		try {
			Uri uri = Uri.parse("weixin://");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivityAndCollapse(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
