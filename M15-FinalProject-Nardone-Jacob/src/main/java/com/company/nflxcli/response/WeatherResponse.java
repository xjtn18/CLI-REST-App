package com.company.nflxcli.response;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
 * The OpenWeather weather info response class.
 * 
 */
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

	public static class Precipitation {
		 // attributes need @JsonProperty since their key starts with a digit
		@JsonProperty("1h") public String _1h;
		@JsonProperty("3h") public String _3h;
	}

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
	public Precipitation rain, snow;
	public String dt;
	public Sys sys;
	public String timezone, id, name, cod;
}