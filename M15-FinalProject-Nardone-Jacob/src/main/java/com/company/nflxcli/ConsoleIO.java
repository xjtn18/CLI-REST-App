package com.company.nflxcli;

import com.company.nflxcli.response.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Handles the program's main console input and output operations.
 */ 
class ConsoleIO {

	// Simple class for storing a pair of objects; used for the display tables.
	private static class TableKeyValuePair <KeyType, ValueType> {
		private final KeyType key;
		private final ValueType value;

		public TableKeyValuePair(KeyType key, ValueType value){
			this.key = key;
			this.value = value;
		}

		public KeyType getKey(){ return key; }
		public ValueType getValue(){ return value; }
	}

	// Custom exception class for number inputs from the user that are outside the range of possible values.
	public static class OutOfRangeException extends Exception {
		public OutOfRangeException(String msg){ super(msg); }
	}


	// Attributes
	private final Scanner scanner;
	private boolean verbose;



	// Methods

	/** Constructor */
	ConsoleIO(){
		scanner = new Scanner(System.in); // allocate the scanner for user input
		verbose = false; // initially set verbose to 'false'
	}



	/**
	 * Toggle the verbosity of the information display to the user for each response.
	 */
	void toggleVerbosity(){
		verbose = !verbose;
	}



	/**
	 * Prints prompt and returns a line of input from the user with leading & trailing whitespace removed.
	 * @param prompt - The prompt message string
	 */
	String promptForInput(String prompt){
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
		//System.out.print("\033\143"); // Doesn't work on window's consoles; prints random symbol instead.
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
	 *	@param weatherResponse - the weather response received from the request to OpenWeather.
	 *	@param unitStandard - the unit standard used for displaying measurements.
	 */
	void printWeatherResponse(WeatherResponse weatherResponse, UnitStandard unitStandard){
		List<TableKeyValuePair<String, String>> pairList = new ArrayList<>();

		if (verbose){
			pairList.add(new TableKeyValuePair<>("Weather:", weatherResponse.weather[0].main + " ~ " + weatherResponse.weather[0].description));
			pairList.add(new TableKeyValuePair<>("Temperature:", weatherResponse.main.temp + " " + unitStandard.temp));
			pairList.add(new TableKeyValuePair<>("Temperature min:", weatherResponse.main.temp_min + " " + unitStandard.temp));
			pairList.add(new TableKeyValuePair<>("Temperature max:", weatherResponse.main.temp_max + " " + unitStandard.temp));
			pairList.add(new TableKeyValuePair<>("Feels like:", weatherResponse.main.feels_like + " " + unitStandard.temp));
			if (weatherResponse.rain != null){
				if (weatherResponse.rain._1h != null) pairList.add(new TableKeyValuePair<>("Rainfall last hour:", weatherResponse.rain._1h + " mm"));
				if (weatherResponse.rain._3h != null) pairList.add(new TableKeyValuePair<>("Rainfall last 3 hours:", weatherResponse.rain._3h + " mm"));
			}
			if (weatherResponse.snow != null){
				if (weatherResponse.snow._1h != null) pairList.add(new TableKeyValuePair<>("Snowfall last hour:", weatherResponse.snow._1h + " mm"));
				if (weatherResponse.snow._3h != null) pairList.add(new TableKeyValuePair<>("Snowfall last 3 hours:", weatherResponse.snow._3h + " mm"));
			}
			pairList.add(new TableKeyValuePair<>("Humidity:", weatherResponse.main.humidity + "%"));
			pairList.add(new TableKeyValuePair<>("Cloudiness:", weatherResponse.clouds.all + "%"));
			pairList.add(new TableKeyValuePair<>("Wind speed:", weatherResponse.wind.speed + " " + unitStandard.speed));
			if (weatherResponse.wind.gust != null){
				pairList.add(new TableKeyValuePair<>("Gust:", weatherResponse.wind.gust + " " + unitStandard.speed));
			}
			pairList.add(new TableKeyValuePair<>("Direction:", weatherResponse.wind.deg + "°"));
			pairList.add(new TableKeyValuePair<>("Pressure:", weatherResponse.main.pressure + " hPa"));
			pairList.add(new TableKeyValuePair<>("Visibility:", weatherResponse.visibility + " meters"));

			pairList.add(new TableKeyValuePair<>("Sunrise:", getMilitaryTimeUTC(Long.parseLong(weatherResponse.sys.sunrise))));
			pairList.add(new TableKeyValuePair<>("Sunrise:", getMilitaryTimeUTC(Long.parseLong(weatherResponse.sys.sunset))));

		} else {
			pairList.add(new TableKeyValuePair<>("Weather:", weatherResponse.weather[0].main + " ~ " + weatherResponse.weather[0].description));
			pairList.add(new TableKeyValuePair<>("Temperature:", weatherResponse.main.temp + " " + unitStandard.temp));
			pairList.add(new TableKeyValuePair<>("Wind speed:", weatherResponse.wind.speed + " " + unitStandard.speed));
			pairList.add(new TableKeyValuePair<>("Humidity:", weatherResponse.main.humidity + "%"));
		}

		displayTable(pairList);
	}



	/**
	 * Prints the ISS coordinates and the city & country that's below it (if applicable) given weather and space response.
	 *	@param spaceResponse - the ISS location response received from the request to OpenNotify.
	 *	@param weatherResponse - the weather response received from the request to OpenWeather with the ISS coordinates.
	 */
	void printSpaceResponse(SpaceResponse spaceResponse, WeatherResponse weatherResponse){
		List<TableKeyValuePair<String, String>> pairList = new ArrayList<>();

		pairList.add(new TableKeyValuePair<>("Latitude:", spaceResponse.iss_position.latitude));
		pairList.add(new TableKeyValuePair<>("Longitude:", spaceResponse.iss_position.longitude));
		if (weatherResponse.sys.country != null){
			pairList.add(new TableKeyValuePair<>("Currently above:", weatherResponse.name + ", " + weatherResponse.sys.country));
		} else {
			pairList.add(new TableKeyValuePair<>("Currently above:", "(not above any country)"));
		}

		displayTable(pairList);
	}



	/**
	 * Prints the coin data of the given cryptocurrency response.
	 *	@param cryptoResponse - the weather response received from the request to CoinAPI.
	 */
	void printCryptoResponse(CryptoResponse cryptoResponse){
		List<TableKeyValuePair<String, String>> pairList = new ArrayList<>();

		pairList.add(new TableKeyValuePair<>("Name:", cryptoResponse.name));
		pairList.add(new TableKeyValuePair<>("ID:", cryptoResponse.asset_id));
		if (cryptoResponse.price_usd != null){
			pairList.add(new TableKeyValuePair<>("Price:", String.format("$%,.2f", Float.parseFloat(cryptoResponse.price_usd))));
		} else {
			pairList.add(new TableKeyValuePair<>("Price:", "(no price data found)"));
		}

		displayTable(pairList);
	}



	/**
	 * Prints key-value pairs of data out to the command line in table format.
	 * @param pairList - List of pairs representing the 2 columns of each row.
	 */
	private void displayTable(List<TableKeyValuePair<String, String>> pairList){
		int widestKey = 0, widestValue = 0;
		// find the widest string in each column
		for (TableKeyValuePair<String,String> entry : pairList){
			widestKey = Math.max(entry.getKey().length(), widestKey);
			widestValue = Math.max(entry.getValue().length(), widestValue);
		}
		int lineWidth = widestKey + widestValue + 10;

		// char array to store the horizontal lines of the table
		char[] line = new char[lineWidth];
		line[0] = ' ';
		Arrays.fill(line, 1, lineWidth-1, '-');
		line[lineWidth-1] = ' ';

		String format = "| %" + widestKey + "s  |  %-" + widestValue + "s |\n"; // table row format

		System.out.println(line);
		for (TableKeyValuePair<String,String> entry : pairList){
			System.out.printf(format, entry.getKey(), entry.getValue()); // print row
		}
		System.out.println(line);
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


