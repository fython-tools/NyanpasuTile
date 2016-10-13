package moe.feng.nyanpasu.tile.api.weather;

public class OpenWeatherMapApi {

	private static final String APP_KEY = "bad07c5c440867324a1a9d98bc6867b5";

	private static final String API_HOST = "http://api.openweathermap.org";

	private static final String QUERY_URL = API_HOST + "/data/2.5/weather";
	private static final String BY_NAME_URL = QUERY_URL + "?q={name}&appid=" + APP_KEY;
	private static final String BY_ID_URL = QUERY_URL + "?id={id}&appid=" + APP_KEY;
	private static final String BY_GEOLOC_URL = QUERY_URL + "?lat={lat}&lon={lon}&appid=" + APP_KEY;


}
