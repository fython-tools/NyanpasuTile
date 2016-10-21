package moe.feng.nyanpasu.tile.ui.fragment.settings;

import android.os.Bundle;

import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.ui.fragment.AbsPrefFragment;
import moe.feng.nyanpasu.tile.util.Settings;
import rikka.materialpreference.ListPreference;

public class SettingScanQr extends AbsPrefFragment {

	ListPreference mPrefMethod;

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.settings_scan_qr);

		getActivity().setTitle(R.string.scan_qr_tile_label);

		mPrefMethod = (ListPreference) findPreference("scan_method");

		try {
			mPrefMethod.setValue(
					Settings.getHelper(getContext(), "scan_qr").get("method", "0")
			);
		} catch (Exception e) {

		}

		mPrefMethod.setOnPreferenceChangeListener((preference, newValue) -> {
			Settings.getHelper(getContext(), "scan_qr").put("method", (String) newValue);
			return true;
		});
	}

}
