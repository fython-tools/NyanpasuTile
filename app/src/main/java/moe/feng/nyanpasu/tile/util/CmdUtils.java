package moe.feng.nyanpasu.tile.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdUtils {

	public static final String[] START_NET_ADB = {
			"setprop service.adb.tcp.port 5555",
			"stop adbd",
			"start adbd"
	}, STOP_NET_ADB = {
			"setprop service.adb.tcp.port -1",
			"stop adbd",
			"start adbd"
	};

	public static String execRootCmd(String[] cmds) {
		String result = "";
		DataOutputStream dos = null;
		DataInputStream dis = null;

		try {
			Process p = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(p.getOutputStream());
			dis = new DataInputStream(p.getInputStream());

			for (String cmd : cmds) {
				Log.i("CmdUtils", cmd);
				dos.writeBytes(cmd + "\n");
				dos.flush();
			}
			dos.writeBytes("exit\n");
			dos.flush();
			String line;
			while ((line = dis.readLine()) != null) {
				Log.d("result", line);
				result += line;
			}
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static String propReader(String filter) {
		Process process = null;
		try {
			process = new ProcessBuilder().command("/system/bin/getprop")
					.redirectErrorStream(true).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringBuilder log = new StringBuilder();
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(filter)) log.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		process.destroy();
		return log.toString();
	}

	public static boolean isNetworkAdbEnabled() {
		String result = propReader("service.adb.tcp.port");
		return !result.contains("-1") && result.contains("service.adb.tcp.port");
	}

}
