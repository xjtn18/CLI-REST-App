package com.company.nflxcli;


/**
 * 
 * glorified namespace to hold all the methods that print the main user facing elements of the application.
 *
 */
class ConsoleUI {


	/**
	 * Prints the menu of the options that the user can choose from.
	 */
	public static void printMenu(){
		System.out.println("\n\n-----------------------------------------------------------");
		System.out.println("[1] Get weather in a city");
		System.out.println("[2] Get location of the ISS");
		System.out.println("[3] Get location of the ISS & weather at that location");
		System.out.println("[4] Get current cryptocurrency prices");
		System.out.println("[5] Quit");
	}


	/**
	 * Prints the weather data of the given weather response.
	 *	@param response - the weather response received from the request to OpenWeather.
	 */
	public static void printWeatherData(WeatherResponse weatherResponse){
		System.out.println("Weather: " + weatherResponse.weather[0].main + " ~ "
				+ weatherResponse.weather[0].description);
		System.out.println("Temperature: " + weatherResponse.main.temp + " °F");
		System.out.println("Feels like: " + weatherResponse.main.feels_like + " °F");
		System.out.println("Wind speed: " + weatherResponse.wind.speed + " mph");
		System.out.println("Humidity: " + weatherResponse.main.humidity + " %");
	}


	/**
	 * Prints the ISS location and the weather data at location of ISS given the both repsonses.
	 *	@param spaceResponse - the ISS location response received from the request to OpenNotify.
	 *	@param weatherResponse - the weather response received from the request to OpenWeather with the ISS coords.
	 */

	public static void printISSData(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
		System.out.println("Latitude: " + spaceResponse.iss_position.latitude);
		System.out.println("Longitude: " + spaceResponse.iss_position.longitude);
		if (weatherResponse.sys.country == null)
			System.out.println("(Currently not above any country)");
		else
			System.out.println("(Currently above " + weatherResponse.name + ", " + weatherResponse.sys.country + ")");
	}



	/**
	 * Prints the coin data of the given cryptocurrency response.
	 *	@param cryptoResponse - the weather response received from the request to CoinAPI.
	 */
	public static void printCryptoData(CryptoResponse[] cryptoResponse){
		System.out.println("Name: " + cryptoResponse[0].name);
		System.out.println("ID: " + cryptoResponse[0].asset_id);
		System.out.printf("Price: $%,.2f\n", Float.parseFloat(cryptoResponse[0].price_usd));
	}

}


