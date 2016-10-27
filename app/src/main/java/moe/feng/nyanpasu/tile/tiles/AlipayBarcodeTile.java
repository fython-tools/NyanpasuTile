package moe.feng.nyanpasu.tile.tiles;

import android.service.quicksettings.TileService;

import moe.feng.nyanpasu.tile.api.ScanQrApi;

public class AlipayBarcodeTile extends TileService {

	@Override
	public void onClick() {
		ScanQrApi.openAlipayBarcode(this);
	}

}
