package com.company.isstracker;


public class WeatherResponse {


	public static class Coordinate {
		public String lon, lat;
	}


	public static class Weather {
		public String id, main, description, icon;

	}

	public static class Atmosphere {
		public String temp, feels_like, pressure, humidity, temp_min, temp_max, sea_level, grnd_level;
	}

	public static class Wind {
		public String speed, deg, gust;
	}

	public static class Clouds {
		public String all;
	}

	// public class Rain {
	// 	public String 1h, 3h;
	// }

	// public class Snow {
	// 	public String 1h, 3h;
	// }

	public static class Sys {
		public String type, id, message, country, sunrise, sunset;
	}


	public Coordinate coord;
	public Weather[] weather;
	public String base;
	public Atmosphere main;
	public String visibility;
	public Wind wind;
	public Clouds clouds;
	public String dt;
	public Sys sys;
	public String timezone, id, name, cod;
}