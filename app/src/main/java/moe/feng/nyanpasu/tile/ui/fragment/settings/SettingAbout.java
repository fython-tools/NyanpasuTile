package moe.feng.nyanpasu.tile.ui.fragment.settings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import moe.feng.nyanpasu.tile.R;
import moe.feng.nyanpasu.tile.ui.fragment.AbsPrefFragment;
import rikka.materialpreference.Preference;

public class SettingAbout extends AbsPrefFragment {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.settings_about);

		Preference mPrefVersion = findPreference("app_version");

		String versionName = null;
		int versionCode = 0;
		try {
			versionName = getActivity().getPackageManager()
					.getPackageInfo(getActivity().getPackageName(), 0).versionName;
			versionCode = getActivity().getPackageManager()
					.getPackageInfo(getActivity().getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		mPrefVersion.setSummary(
				String.format(getString(R.string.app_version_format), versionName, versionCode)
		);

		findPreference("github").setOnPreferenceClickListener(
				preference -> openUrl(getActivity(), getString(R.string.pref_github_url))
		);
		findPreference("weibo").setOnPreferenceClickListener(
				preference -> openUrl(getActivity(), getString(R.string.pref_weibo_url))
		);
		findPreference("email").setOnPreferenceClickListener(this::sendEmailTo);
	}

	private boolean sendEmailTo(Preference preference) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:" + getString(R.string.pref_email_address)));
		intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.pref_email_address));
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
		}
		return true;
	}

}
