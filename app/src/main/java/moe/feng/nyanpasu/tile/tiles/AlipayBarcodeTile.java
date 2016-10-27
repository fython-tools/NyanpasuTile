package moe.feng.nyanpasu.tile.tiles;

import android.service.quicksettings.TileService;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;

public class AlipayBarcodeTile extends TileService {

	@Override
	public void onClick() {
		AlipayZeroSdk.openAlipayBarcode(this);
	}

}
