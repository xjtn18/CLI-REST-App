package com.company.nflxcli;

import com.company.nflxcli.response.*;

import java.util.Scanner;


/**
 * Handles the program's main console input and output operations.
 */ 
class ConsoleIO {

	// Custom exception classes
	public static class OutOfRangeException extends Exception {
		public OutOfRangeException(String msg){ super(msg); }
	}



	// Attributes
	private Scanner scanner;


	// Methods

	public ConsoleIO(){
		scanner = new Scanner(System.in);
	}



	public String promptForInput(){
		return scanner.nextLine().trim();
	}



	/**
	 * Prompts for integer in the specified range as input from user through the command line.
	 * 
	 * @param scanner - the Scanner object used to take input
	 * @param start - Start of the valid range
	 * @param end - End of the valid range
	 * @return int representing the user's choice
	 * @throws OutOfRangeException - if the user doesn't provide a number within the given range
	 */
	public int promptForNumberInRange(int start, int end) throws OutOfRangeException {
		int choice = Integer.parseInt(scanner.nextLine().trim()); // grab input from command line, convert to int

		if (choice < start || choice > end){
			throw new OutOfRangeException("Number provided is out of range.");
		}

		return choice;
	}



	/**
	 * Prints the menu of the options that the user can choose from.
	 */
	public void printMenu(){
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
	public void printResponse(WeatherResponse weatherResponse){
		System.out.println("Weather: " + weatherResponse.weather[0].main + " ~ "
				+ weatherResponse.weather[0].description);
		System.out.println("Temperature: " + weatherResponse.main.temp + " °F");
		System.out.println("Feels like: " + weatherResponse.main.feels_like + " °F");
		System.out.println("Wind speed: " + weatherResponse.wind.speed + " mph");
		System.out.println("Humidity: " + weatherResponse.main.humidity + "%");
	}



	/**
	 * Prints the ISS location and the weather data at location of ISS given the both repsonses.
	 *	@param spaceResponse - the ISS location response received from the request to OpenNotify.
	 *	@param weatherResponse - the weather response received from the request to OpenWeather with the ISS coords.
	 */
	public void printResponse(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
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
	public void printResponse(CryptoResponse cryptoResponse){
		System.out.println("Name: " + cryptoResponse.name);
		System.out.println("ID: " + cryptoResponse.asset_id);
		if (cryptoResponse.price_usd != null) {
			System.out.printf("Price: $%,.2f\n", Float.parseFloat(cryptoResponse.price_usd));
		} else {
			System.out.println("Price: No price data found");
		}
	}

}


