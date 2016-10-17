package moe.feng.nyanpasu.tile.tiles;

import android.service.quicksettings.TileService;
import android.util.Log;

import moe.feng.nyanpasu.tile.api.ScanQrApi;
import moe.feng.nyanpasu.tile.util.Settings;

public class ScanQrTile extends TileService {

	Settings.Helper mSettings;

	@Override
	public void onCreate() {
		super.onCreate();
		mSettings = Settings.getHelper(this, "scan_qr");
	}

	@Override
	public void onClick() {
		super.onClick();
		Log.i("Test", "Value:" + mSettings.get("method", "0"));
		switch (mSettings.get("method", "0")) {
			case "1":
				ScanQrApi.openAlipayScan(this);
				break;
			case "2":
				ScanQrApi.openWeChatScan(this);
				break;
			case "0":
			default:
				ScanQrApi.openScanner(this);
		}
	}

}
