package moe.feng.nyanpasu.tile.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class WeatherService extends IntentService {

	public static final String TAG = WeatherService.class.getSimpleName();

	public static final String BAIDU_LBS_APPKEY = "6ad732cfea46894f24b89bb31be37e51";

	public WeatherService() {
		super(TAG);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}

}
