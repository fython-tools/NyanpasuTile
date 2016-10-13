package moe.feng.nyanpasu.tile.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BDWeather implements IWeather {

	private int error;
	private String status, date;
	@SerializedName("results") private Result result;

	@Override
	public String getCity() {
		return result.currentCity;
	}

	@Override
	public String getCityPinyin() {
		return null;
	}

	@Override
	public String getProvider() {
		return "Baidu";
	}

	@Override
	public Temperature getMaxTemperature() {
		return result.weatherData.get(0).getMaxTemperature();
	}

	@Override
	public Temperature getMinTemperature() {
		return result.weatherData.get(0).getMinTemperature();
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public float getWindSpeed() {
		return 0;
	}

	@Override
	public int getWindDegree() {
		return 0;
	}

	@Override
	public int getHumidity() {
		return 0;
	}

	@Override
	public float getPressure() {
		return 0;
	}

	@Override
	public List<IDay> getDays() {
		return null;
	}

	class Result {

		public String currentCity, pm25;
		public List<Tip> index;
		@SerializedName("weather_data") public List<Data> weatherData;

		class Tip {
			public String title, zs, tipt, des;
		}

		class Data implements IDay {
			public String date, dayPictureUrl, nightPictureUrl, weather, wind, tempeature;

			@Override
			public Temperature getMaxTemperature() {
				if (tempeature.contains("～")) {
					return Temperature.C(Float.parseFloat(tempeature.substring(0, tempeature.indexOf("～") - 1)));
				} else {
					return Temperature.C(Float.parseFloat(tempeature.replace("℃", "")));
				}
			}

			@Override
			public Temperature getMinTemperature() {
				if (tempeature.contains("～")) {
					return Temperature.C(Float.parseFloat(tempeature.substring(tempeature.indexOf("～") + 1, tempeature.indexOf("℃"))));
				} else {
					return Temperature.C(Float.parseFloat(tempeature.replace("℃", "")));
				}
			}

			@Override
			public int getStatus() {
				return 0;
			}

			@Override
			public int getDayInWeek() {
				return 0;
			}
		}

	}

}
