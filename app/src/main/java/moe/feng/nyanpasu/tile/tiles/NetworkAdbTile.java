package moe.feng.nyanpasu.tile.tiles;

import android.graphics.drawable.Icon;
import android.service.quicksettings.TileService;
import android.util.Log;

import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.util.CmdUtils;

public class NetworkAdbTile extends TileService {

	@Override
	public void onClick() {
		Log.i("NetworkAdb", CmdUtils.isNetworkAdbEnabled() + ".");
		if (CmdUtils.isNetworkAdbEnabled()) {
			CmdUtils.execRootCmd(CmdUtils.STOP_NET_ADB);
			getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_phonelink_off_white_24dp));
			getQsTile().updateTile();
		} else {
			CmdUtils.execRootCmd(CmdUtils.START_NET_ADB);
			getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_phonelink_white_24dp));
			getQsTile().updateTile();
		}
	}

}
