package com.company.nflxcli;

import com.company.nflxcli.response.*;

import java.util.Scanner;
import java.util.Arrays;


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

	/**
	 * Constructor
	 */
	public ConsoleIO(){
		scanner = new Scanner(System.in); // allocate the scanner for user input
	}



	/**
	 * Returns a line of input from the user with leading & trailing whitespace removed.
	 */
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
		String[] params = new String[]{"Weather", "Temperature", "Feels like", "Wind speed", "Humidity"};
		String[] values = new String[]{
			weatherResponse.weather[0].main + " ~ " + weatherResponse.weather[0].description,
			weatherResponse.main.temp + " °F",
			weatherResponse.main.feels_like + " °F",
			weatherResponse.wind.speed + " mph",
			weatherResponse.main.humidity + "%"
		};
		displayTable(params, values);
	}



	/**
	 * Prints the ISS location and the weather data at location of ISS given the both repsonses.
	 *	@param spaceResponse - the ISS location response received from the request to OpenNotify.
	 *	@param weatherResponse - the weather response received from the request to OpenWeather with the ISS coords.
	 */
	public void printResponse(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
		String[] params = new String[]{"Latitude", "Longitude", "Currently above"};
		String[] values = new String[]{
			spaceResponse.iss_position.latitude,
			spaceResponse.iss_position.longitude,
			(weatherResponse.sys.country == null)
			? "(not above any country)" : weatherResponse.name + ", " + weatherResponse.sys.country
		};
		displayTable(params, values);
	}



	/**
	 * Prints the coin data of the given cryptocurrency response.
	 *	@param cryptoResponse - the weather response received from the request to CoinAPI.
	 */
	public void printResponse(CryptoResponse cryptoResponse){
		String[] params = new String[]{"Name", "ID", "Price"};
		String[] values = new String[]{
			cryptoResponse.name,
			cryptoResponse.asset_id,
			(cryptoResponse.price_usd == null)
			? "(no price data found)" : String.format("$%,.2f", Float.parseFloat(cryptoResponse.price_usd))
		};
		displayTable(params, values);
	}



	/**
	 * Prints the coin data of the given cryptocurrency response.
	 *	@param cryptoResponse - the weather response received from the request to CoinAPI.
	 */
	private int getWidth(String[] strings){
		int widest = 0;
		for (String s : strings) widest = Math.max(s.length(), widest);
		return widest;
	}



	/**
	 * 
	 * Prints key-value pairs of data out to the command line in table format.
	 * @param params - The keys as strings.
	 * @param values - The values as strings.
	 */
	private void displayTable(String[] params, String[] values){
		int widestParam = getWidth(params);
		int widestValue = getWidth(values);
		int lineWidth = widestParam + widestValue + 10;
		char[] line = new char[lineWidth]; Arrays.fill(line, 1, lineWidth-1, '-'); line[0] = ' '; // horizontal line
		String format = "| %" + widestParam + "s:  |  %-" + widestValue + "s |\n"; // table row format

		System.out.println(line);
		for (int i = 0; i < params.length; ++i){
			System.out.printf(format, params[i], values[i]); // print row
		}
		System.out.println(line);
	}

}


