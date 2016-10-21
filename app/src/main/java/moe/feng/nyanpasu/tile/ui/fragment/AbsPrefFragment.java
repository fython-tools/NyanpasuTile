package moe.feng.nyanpasu.tile.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import rikka.materialpreference.PreferenceFragment;

public abstract class AbsPrefFragment extends PreferenceFragment {

	public boolean openUrl(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		return true;
	}

}
