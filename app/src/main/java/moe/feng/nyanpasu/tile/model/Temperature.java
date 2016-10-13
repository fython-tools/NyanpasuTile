package moe.feng.nyanpasu.tile.model;

public class Temperature {

	private float fd;

	public Temperature(float fd) {
		this.fd = fd;
	}

	public Temperature(float d, boolean isCelsius) {
		this(isCelsius ? convertCtoF(d) : d);
	}

	public void setDegree(float d, boolean isCelsius) {
		this.fd = isCelsius ? convertCtoF(d) : d;
	}

	public float getFahrenheitDegree(){
		return fd;
	}

	public float getCelsiusDegree(){
		return convertFtoC(fd);
	}

	public static Temperature F(float fd) {
		return new Temperature(fd);
	}

	public static Temperature C(float cd) {
		return new Temperature(cd, true);
	}

	public static float convertFtoC(float fd) {
		return (fd - 32) / 1.8f;
	}

	public static float convertCtoF(float cd) {
		return 32 + cd * 1.8f;
	}

}
