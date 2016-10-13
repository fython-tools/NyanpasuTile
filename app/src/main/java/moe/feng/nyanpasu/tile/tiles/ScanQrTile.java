package moe.feng.nyanpasu.tile.tiles;

import android.service.quicksettings.TileService;

import moe.feng.nyanpasu.tile.api.ScannQrApi;

public class ScanQrTile extends TileService {

	@Override
	public void onClick() {
		super.onClick();
		ScannQrApi.openAlipayScan(this);
	}

}
