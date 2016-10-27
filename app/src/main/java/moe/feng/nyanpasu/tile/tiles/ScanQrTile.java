package moe.feng.nyanpasu.tile.tiles;

import android.service.quicksettings.TileService;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;
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
		switch (mSettings.get("method", "0")) {
			case "1":
				AlipayZeroSdk.openAlipayScan(this);
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
