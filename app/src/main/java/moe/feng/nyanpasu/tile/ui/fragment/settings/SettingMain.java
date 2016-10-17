package moe.feng.nyanpasu.tile.ui.fragment.settings;

import android.os.Bundle;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.ui.SettingsActivity;
import moe.feng.nyanpasu.tile.ui.fragment.AbsPrefFragment;
import moe.feng.nyanpasu.tile.util.ClipboardUtils;
import rikka.materialpreference.Preference;

public class SettingMain extends AbsPrefFragment {

	Preference mPrefAbout, mPrefDonate;

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.settings_main);

		mPrefAbout = findPreference("about");
		mPrefDonate = findPreference("donate");

		setupCallbacks();
	}

	private void setupCallbacks() {
		findPreference("scan_qr").setOnPreferenceClickListener(preference -> {
			SettingsActivity.launch(getActivity(), SettingsActivity.FLAG_SCAN_QR);
			return true;
		});

		mPrefAbout.setOnPreferenceClickListener(preference -> {
			SettingsActivity.launch(getActivity(), SettingsActivity.FLAG_ABOUT);
			return true;
		});
		mPrefDonate.setOnPreferenceClickListener(preference -> {
			if (AlipayZeroSdk.hasInstalledAlipayClient(getContext())) {
				AlipayZeroSdk.startAlipayClient(getActivity(), "aehvyvf4taua18zo6e");
			} else {
				ClipboardUtils.putString(getContext(), getString(R.string.pref_donate_alipay_address));
			}
			return true;
		});
	}

}
