package moe.feng.nyanpasu.tile.model;

import java.util.List;

public interface IWeather {

	int STATUS_SUNNY = 0, STATUS_CLOUDY = 1, STATUS_RAINY = 2, STATUS_SNOW = 3;

	String getCity();
	String getCityPinyin();
	String getProvider();
	Temperature getMaxTemperature();
	Temperature getMinTemperature();
	int getStatus();
	float getWindSpeed();
	int getWindDegree();
	int getHumidity();
	float getPressure();
	List<IDay> getDays();

	interface IDay {
		Temperature getMaxTemperature();
		Temperature getMinTemperature();
		int getStatus();
		int getDayInWeek();
	}

}
