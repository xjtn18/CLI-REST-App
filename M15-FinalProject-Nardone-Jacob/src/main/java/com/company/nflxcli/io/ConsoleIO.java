package com.company.nflxcli.io;

import com.company.nflxcli.response.*;
import com.company.nflxcli.UnitStandard;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Scanner;


/**
 * Handles the program's main console input and output operations.
 */ 
public class ConsoleIO {


	// Custom exception class for number inputs from the user that are outside the range of possible values.
	public static class OutOfRangeException extends Exception {
		public OutOfRangeException(String msg){ super(msg); }
	}


	// Attributes
	private final Scanner scanner;
	private final Table table;
	private boolean verbose;



	// Methods

	/** Constructor */
	public ConsoleIO(){
		scanner = new Scanner(System.in); // allocate the scanner for user input
		table = new Table();
		verbose = false; // initially set verbose to 'false'
	}



	/**
	 * Toggle the verbosity of the information display to the user for each response.
	 */
	public void toggleVerbosity(){
		verbose = !verbose;
	}



	/**
	 * Prints prompt and returns a line of input from the user with leading & trailing whitespace removed.
	 * @param prompt - The prompt message string
	 */
	public String promptForInput(String prompt){
		System.out.print(prompt + " ");
		return scanner.nextLine().trim();
	}



	/**
	 * Prompts for integer in the specified range as input from user through the command line.
	 * 
	 * @param prompt - The prompt message string
	 * @param start - Start of the valid range
	 * @param end - End of the valid range
	 * @return int representing the user's choice
	 * @throws OutOfRangeException - if the user doesn't provide a number within the given range
	 */
	public int promptForNumberInRange(String prompt, int start, int end) throws OutOfRangeException {
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
	public void log(String text){
		System.out.println(text);
	}



	/**
	 * Prints the main menu of the options that the user can choose from.
	 */
	public void printMainMenu(){
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
	public void printSettingsMenu(UnitStandard unitStandard){
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
	 *	@param weatherResponse - the weather response received from the request to OpenWeather.
	 *	@param unitStandard - the unit standard used for displaying measurements.
	 */
	public void printWeatherResponse(WeatherResponse weatherResponse, UnitStandard unitStandard){
		table.clear();

		if (verbose){
			table.add("Weather:", weatherResponse.weather[0].main + " ~ " + weatherResponse.weather[0].description);
			table.add("Temperature:", weatherResponse.main.temp + " " + unitStandard.temp);
			table.add("Temperature min:", weatherResponse.main.temp_min + " " + unitStandard.temp);
			table.add("Temperature max:", weatherResponse.main.temp_max + " " + unitStandard.temp);
			table.add("Feels like:", weatherResponse.main.feels_like + " " + unitStandard.temp);
			if (weatherResponse.rain != null){
				if (weatherResponse.rain._1h != null) table.add("Rainfall last hour:", weatherResponse.rain._1h + " mm");
				if (weatherResponse.rain._3h != null) table.add("Rainfall last 3 hours:", weatherResponse.rain._3h + " mm");
			}
			if (weatherResponse.snow != null){
				if (weatherResponse.snow._1h != null) table.add("Snowfall last hour:", weatherResponse.snow._1h + " mm");
				if (weatherResponse.snow._3h != null) table.add("Snowfall last 3 hours:", weatherResponse.snow._3h + " mm");
			}
			table.add("Humidity:", weatherResponse.main.humidity + "%");
			table.add("Cloudiness:", weatherResponse.clouds.all + "%");
			table.add("Wind speed:", weatherResponse.wind.speed + " " + unitStandard.speed);
			if (weatherResponse.wind.gust != null){
				table.add("Gust:", weatherResponse.wind.gust + " " + unitStandard.speed);
			}
			table.add("Direction:", weatherResponse.wind.deg + "°");
			table.add("Pressure:", weatherResponse.main.pressure + " hPa");
			table.add("Visibility:", weatherResponse.visibility + " meters");

			table.add("Sunrise:", getMilitaryTimeUTC(Long.parseLong(weatherResponse.sys.sunrise)));
			table.add("Sunset:", getMilitaryTimeUTC(Long.parseLong(weatherResponse.sys.sunset)));

		} else {
			table.add("Weather:", weatherResponse.weather[0].main + " ~ " + weatherResponse.weather[0].description);
			table.add("Temperature:", weatherResponse.main.temp + " " + unitStandard.temp);
			table.add("Wind speed:", weatherResponse.wind.speed + " " + unitStandard.speed);
			table.add("Humidity:", weatherResponse.main.humidity + "%");
		}

		table.print();
	}



	/**
	 * Prints the ISS coordinates and the city & country that's below it (if applicable) given weather and space response.
	 *	@param spaceResponse - the ISS location response received from the request to OpenNotify.
	 *	@param weatherResponse - the weather response received from the request to OpenWeather with the ISS coordinates.
	 */
	public void printSpaceResponse(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
		table.clear();

		table.add("Latitude:", spaceResponse.iss_position.latitude);
		table.add("Longitude:", spaceResponse.iss_position.longitude);
		if (weatherResponse.sys.country != null){
			table.add("Currently above:", weatherResponse.name + ", " + weatherResponse.sys.country);
		} else {
			table.add("Currently above:", "(not above any country)");
		}

		table.print();
	}



	/**
	 * Prints the coin data of the given cryptocurrency response.
	 *	@param cryptoResponse - the weather response received from the request to CoinAPI.
	 */
	public void printCryptoResponse(CryptoResponse cryptoResponse){
		table.clear();

		table.add("Name:", cryptoResponse.name);
		table.add("ID:", cryptoResponse.asset_id);
		if (cryptoResponse.price_usd != null){
			table.add("Price:", String.format("$%,.2f", Float.parseFloat(cryptoResponse.price_usd)));
		} else {
			table.add("Price:", "(no price data found)");
		}

		table.print();
	}



	/**
	 * Returns the formatted military time (UTC) from a given unix timestamp.
	 * @param unixTime - the unix timestamp to format.
	 * @return A string of the formatted time in UTC.
	 */
	private String getMilitaryTimeUTC(long unixTime){
		final String timezone = "UTC";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return Instant.ofEpochSecond(unixTime)
				.atZone(ZoneId.of(timezone))
				.format(formatter) + " " + timezone;
	}

}


