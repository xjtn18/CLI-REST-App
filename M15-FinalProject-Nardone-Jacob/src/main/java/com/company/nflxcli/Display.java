package com.company.nflxcli;


class Display {

	public static void printMenu(){
		System.out.println("\n\n-----------------------------------------------------------");
		System.out.println("[1] Get weather in a city");
		System.out.println("[2] Get location of the ISS");
		System.out.println("[3] Get location of the ISS & weather at that location");
		System.out.println("[4] Get current cryptocurrency prices");
		System.out.println("[5] Quit");
	}


	public static void printWeatherData(WeatherResponse response){
		System.out.println("Weather: " + response.weather[0].main + " ~ " + response.weather[0].description);
		System.out.println("Temperature: " + response.main.temp + " °F");
		System.out.println("Feels like: " + response.main.feels_like + " °F");
		System.out.println("Wind speed: " + response.wind.speed + " mph");
		System.out.println("Humidity: " + response.main.humidity + " %");
	}



	public static void printISSData(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
		System.out.println("Latitude: " + spaceResponse.iss_position.latitude);
		System.out.println("Longitude: " + spaceResponse.iss_position.longitude);
		if (weatherResponse.sys.country == null)
			System.out.println("(Currently not above any country)");
		else
			System.out.println("(Currently above " + weatherResponse.name + ", " + weatherResponse.sys.country + ")");
	}



	public static void printCryptoData(CryptoResponse[] cryptoResponse){
		System.out.println("Name: " + cryptoResponse[0].name);
		System.out.println("ID: " + cryptoResponse[0].asset_id);
		System.out.printf("Price: $%,.2f\n", Float.parseFloat(cryptoResponse[0].price_usd));
	}

}


