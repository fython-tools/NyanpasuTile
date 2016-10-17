package moe.feng.nyanpasu.tile.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.util.HashMap;

public class Settings {

	@Expose private HashMap<String, HashMap<String, Object>> sets = new HashMap<>();
	private Context context;

	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	private static Settings INSTANCE;

	private static final String FILE_NAME = "settings.json";

	public static Settings getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new Settings(context);
		}
		INSTANCE.load();
		return INSTANCE;
	}

	public static Helper getHelper(Context context, String key) {
		return new Helper(getInstance(context), key);
	}

	private Settings(Context context) {
		this.context = context;
	}

	public HashMap<String, Object> get(String key) {
		return sets.get(key);
	}

	public boolean containsKey(String key) {
		return sets.containsKey(key);
	}

	public void put(String key, HashMap<String, Object> data) {
		sets.put(key, data);
	}

	public void remove(String key) {
		sets.remove(key);
	}

	public void load() {
		String data = "{}";
		try {
			data = FileUtils.readFile(context, FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Settings s = gson.fromJson(data, Settings.class);
		if (s.sets != null) this.sets = gson.fromJson(data, Settings.class).sets;
	}

	public synchronized void save() {
		try {
			FileUtils.saveFile(context, FILE_NAME, gson.toJson(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveInBackground() {
		new Thread() {
			@Override
			public void run() {
				save();
			}
		}.start();
	}

	public static class Helper {

		private Settings settings;
		private String key;

		Helper(Settings settings, String key) {
			this.settings = settings;
			this.key = key;
			if (!settings.containsKey(key)) {
				settings.sets.put(key, new HashMap<>());
				settings.save();
			}
		}

		public <T> T get(String vk, T defValue) {
			if (settings.get(key).containsKey(vk)) {
				return (T) settings.get(key).get(vk);
			} else {
				return defValue;
			}
		}

		public void put(String vk, String str) {
			settings.get(key).put(vk, str);
			settings.saveInBackground();
		}

		public void put(String vk, Number num) {
			settings.get(key).put(vk, num);
			settings.saveInBackground();
		}

		public void put(String vk, Boolean bool) {
			settings.get(key).put(vk, bool);
			settings.saveInBackground();
		}

		public void put(String vk, Character chr) {
			settings.get(key).put(vk, chr);
			settings.saveInBackground();
		}

	}

}
