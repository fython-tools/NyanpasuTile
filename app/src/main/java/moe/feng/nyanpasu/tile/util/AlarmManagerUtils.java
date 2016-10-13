package moe.feng.nyanpasu.tile.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerUtils {

	public static void startServiceAlarm(Context context, Class<?> service, long interval) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, service);
		PendingIntent p = PendingIntent.getService(context, 10000, i, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, interval, p);
	}

	public static void stopServiceAlarm(Context context, Class<?> service) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, service);
		PendingIntent p = PendingIntent.getService(context, 10000, i, PendingIntent.FLAG_CANCEL_CURRENT);
		am.cancel(p);
	}

}
