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
	private boolean verbose;



	// Methods

	/**
	 * Constructor
	 */
	ConsoleIO(){
		scanner = new Scanner(System.in); // allocate the scanner for user input
		verbose = false; // intially set verbose to 'false'
	}



	/**
	 * Toggle the verbosity of the information display to the user for each response.
	 */
	void toggleVerbosity(){
		verbose = !verbose;
	}



	/**
	 * Prints prompt and returns a line of input from the user with leading & trailing whitespace removed.
	 */
	String promptForInput(String prompt){
		System.out.print(prompt + " ");
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
	int promptForNumberInRange(String prompt, int start, int end) throws OutOfRangeException {
		int choice = Integer.parseInt(promptForInput(prompt)); // grab input from command line, convert to int

		if (choice < start || choice > end){
			throw new OutOfRangeException(
				String.format("Please enter a number between %d and %d inclusive.", start, end)
			);
		}

		return choice;
	}


	/**
	 * Prints given string to the console and a new line.
	 * @param text - The string to be printed.
	 */
	void log(String text){
		System.out.println(text);
	}



	/**
	 * Clears the console.
	 */
	void clearConsole(){
		System.out.print("\033\143");
	}



	/**
	 * Prints the main menu of the options that the user can choose from.
	 */
	void printMainMenu(){
		log("\n\n-----------------------------------------------------------");
		log("[0] << Quit");
		log("[1] Get weather in a city");
		log("[2] Get location of the ISS");
		log("[3] Get location of the ISS & weather at that location");
		log("[4] Get current cryptocurrency prices");
		log("[5] Settings");
	}


	/**
	 * Prints the settings menu of the options that the user can choose from.
	 */
	void printSettingsMenu(UnitStandard unitStandard){
		log("\n\n-----------------------------------------------------------");
		log("[0] << Back");

		String optionImperial = (unitStandard == UnitStandard.imperialStandard) ? "(imperial)" : " imperial ";
		String optionMetric = (unitStandard == UnitStandard.metricStandard) ? "(metric)" : " metric";
		log("[1] " + optionImperial + " : " + optionMetric);


		String optionBrief = (!verbose) ? "  (brief) " : "   brief  ";
		String optionVerbose = (verbose) ? "(verbose)" : " verbose";
		log("[2] " + optionBrief + " : " + optionVerbose);
	}


	/**
	 * Prints the weather data of the given weather response.
	 *	@param response - the weather response received from the request to OpenWeather.
	 */
	void printResponse(WeatherResponse weatherResponse, UnitStandard unitStandard){
		String[] params = new String[]{"Weather", "Temperature", "Feels like", "Wind speed", "Humidity"};
		String[] values = new String[]{
			weatherResponse.weather[0].main + " ~ " + weatherResponse.weather[0].description,
			weatherResponse.main.temp + " " + unitStandard.temp,
			weatherResponse.main.feels_like + " " + unitStandard.temp,
			weatherResponse.wind.speed + " " + unitStandard.speed,
			weatherResponse.main.humidity + "%"
		};
		displayTable(params, values);
	}



	/**
	 * Prints the ISS coordinates and the city & country thats below it (if applicable) given weather and space response.
	 *	@param spaceResponse - the ISS location response received from the request to OpenNotify.
	 *	@param weatherResponse - the weather response received from the request to OpenWeather with the ISS coords.
	 */
	void printResponse(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
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
	void printResponse(CryptoResponse cryptoResponse){
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
	 * Gets the width of the widest string in an array.
	 *	@param strings - An array of strings.
	 * @return int representing the width of the widest string in the array.
	 */
	private int getWidth(String[] strings){
		int widest = 0;
		for (String s : strings) widest = Math.max(s.length(), widest);
		return widest;
	}



	/**
	 * Prints key-value pairs of data out to the command line in table format.
	 * @param params - A string array of the first column.
	 * @param values - A string array of the second column.
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


