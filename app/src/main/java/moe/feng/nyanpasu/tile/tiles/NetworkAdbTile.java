package moe.feng.nyanpasu.tile.tiles;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.TileService;

import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.ui.RootCheckActivity;
import moe.feng.nyanpasu.tile.util.CmdUtils;
import moe.feng.nyanpasu.tile.util.Settings;

public class NetworkAdbTile extends TileService {

	@Override
	public void onClick() {
		if (!Settings.getHelper(this, "root").get("root_tip", false)) {
			Intent intent = new Intent(this, RootCheckActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivityAndCollapse(intent);
		} else {
			if (CmdUtils.isNetworkAdbEnabled()) {
				CmdUtils.execRootCmd(CmdUtils.STOP_NET_ADB);
				getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_phonelink_off_white_24dp));
				getQsTile().setLabel(getString(R.string.network_adb_tile_label));
				getQsTile().updateTile();
			} else {
				CmdUtils.execRootCmd(CmdUtils.START_NET_ADB);
				getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_phonelink_white_24dp));
				getQsTile().updateTile();
			}
		}
	}

}
