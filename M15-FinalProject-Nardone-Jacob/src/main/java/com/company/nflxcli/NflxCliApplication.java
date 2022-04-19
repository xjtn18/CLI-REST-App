package com.company.nflxcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Scanner;



@SpringBootApplication
public class NflxCliApplication {

	private static final String weatherApiKey = "df5cfd59a9d48cfb1c016a0ae7d1ffef";
	private static final String coinApiKey = "33D50D5A-4308-456E-9060-45F05797217B";


	// Custom exception classes
	private static class OutOfRangeException extends Exception {
		public OutOfRangeException(String msg){ super(msg); }
	}

	private static class BadRequestException extends Exception {
		public BadRequestException(String msg){ super(msg); }
	}



	/**
	 * Gets the response object of the type specified by the 'ResponseType'.
	 * 
	 * @param uri - URI of the API request.
	 * @param responseClass - The '.class' of whatever response object type we want.
	 * @return The custom response object.
	 */
	private <ResponseType> ResponseType request(String uri, Class<ResponseType> responseClass){
		// create the connection to the resource
		WebClient client = WebClient.create(uri);

		// get the Mono response
		Mono<ResponseType> responseMono = client
			.get()
			.retrieve()
			.bodyToMono(responseClass);

		// map the response into our custom response object and return it
		return responseMono.share().block();

	}



	/**
	 * Returns the weather response of a given a city.
	 * @param cityName - The city where we want weather data from.
	 * @return A weather response object.
	 */
	private WeatherResponse getWeatherInCity(String cityName){
		// Request from the weather API passing in the given city name
		return this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"q=" + cityName +
			"&units=imperial" +
			"&appid=" + NflxCliApplication.weatherApiKey,
			WeatherResponse.class);
	}



	/**
	 * Returns the weather response at the given coordinates.
	 * @param latitude - That latitude of our desired location.
	 * @param longitude - The longitude of our desired location.
	 * @return A weather response object.
	 */
	private WeatherResponse getWeatherAtCoordinates(String latitude, String longitude){
		// Request from the weather API using the coordinates of the ISS to grab the city & country data, if it exists
		return this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"lat=" + latitude +
			"&lon=" + longitude +
			"&units=imperial" +
			"&appid=" + NflxCliApplication.weatherApiKey,
			WeatherResponse.class);
	}



	/**
	 * Returns the space response of the ISS.
	 */
	private SpaceResponse getLocationISS(){
		return this.<SpaceResponse>request("http://api.open-notify.org/iss-now.json", SpaceResponse.class);
	}



	/**
	 * Returns the crypto response of a given asset.
	 * @param assetID - The ID of the cryptocurrency we want info for.
	 * @return A crypto response object.
	 * @throws BadRequestException - if an unknown asset ID was queried through the coin API.
	 */
	private CryptoResponse[] getCryptoData(String assetID) throws BadRequestException {
		if (assetID == "")
			throw new BadRequestException("Empty asset ID was queried.");

		CryptoResponse[] cryptoResponse = this.<CryptoResponse[]>request("https://rest.coinapi.io/v1/assets/" + assetID +
			"?apikey=" + NflxCliApplication.coinApiKey,
			CryptoResponse[].class); // @NOTE: CoinApi returns json wrapped entirely in a single array

		if (cryptoResponse.length == 0)
			throw new BadRequestException("The API request failed; unknown asset ID was queried.");
		else
			return cryptoResponse;
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
	private int promptForNumberInRange(Scanner scanner, int start, int end) throws OutOfRangeException {
		int choice = Integer.parseInt(scanner.nextLine().trim()); // grab input from command line, convert to int

		if (choice < start || choice > end){
			throw new OutOfRangeException("Number provided is out of range.");
		}

		return choice;
	}



	/**
	 * Makes a random API call to skip the cold start before the user's first request.
	 */
	private void skipColdStart(){
		try { getWeatherInCity("New York"); }
		catch (WebClientResponseException wre) { };
	}



	/**
	 * Runs the main program command loop; asks users to select from a set of options.
	 * Loop ends when user selects the 'quit' option.
	 */
	public void start() {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		skipColdStart();

		// Main program command loop
		while (running){
			// display menu options to the user
			ConsoleUI.printMenu();

			try {
				// get user's selected option
				System.out.print("\nEnter the number of your selection: ");
				int choice = promptForNumberInRange(scanner, 1, 5);

				switch (choice){

					case 1: { // Weather in a city
						System.out.print("\nPlease enter a city name: ");
						// Get city name from the user
						WeatherResponse weatherResponse = getWeatherInCity(scanner.nextLine().trim());

						System.out.println("\n--- Current weather in " + weatherResponse.name + " ---");
						ConsoleUI.printWeatherData(weatherResponse);
						break;


					} case 2: { // Location of the ISS
						SpaceResponse spaceResponse = getLocationISS();
						// Get weather response data to check city + country that's below the ISS
						WeatherResponse weatherResponse = getWeatherAtCoordinates(spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude);

						System.out.println("\n--- Current ISS location ---");
						ConsoleUI.printISSData(spaceResponse, weatherResponse);
						break;


					} case 3: { // Location and weather at the ISS
						SpaceResponse spaceResponse = getLocationISS();
						WeatherResponse weatherResponse = getWeatherAtCoordinates(spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude);

						System.out.println("\n--- Current ISS location and weather ---");
						// display ISS location information first
						ConsoleUI.printISSData(spaceResponse, weatherResponse);
						// display weather at the ISS location
						ConsoleUI.printWeatherData(weatherResponse);
						break;


					} case 4: { // Current crypto prices
						// Get a crypto asset name from the user
						System.out.print("\nPlease enter a crypto asset ID: ");
						CryptoResponse[] cryptoResponse = getCryptoData(scanner.nextLine().trim());

						System.out.println("\n--- Current data on " + cryptoResponse[0].asset_id + " ---");
						// display that crypto's price info
						ConsoleUI.printCryptoData(cryptoResponse);
						break;


					} case 5: { // Quit
						System.out.println("\nExiting program; may take a moment...");
						running = false;
						break;
				}}

			} catch (NumberFormatException | OutOfRangeException ioe){
				System.out.println("\nError: Please enter a valid number between 1 and 5 inclusive.");

			} catch (WebClientResponseException wre){
				int statusCode = wre.getRawStatusCode();
				if (statusCode >= 400 && statusCode < 500){
					System.out.println("\nClient error occurred. Invalid or unknown query.");
				} else if (statusCode >= 500 && statusCode < 600){
					System.out.println("\nServer error occurred.");
				}

			} catch (Exception e){
				System.out.println("\nError: " + e.getMessage());

			}

			if (running){
				System.out.println("\n[press 'Enter' to continue]");
				scanner.nextLine(); // wait for user to press Enter again before re-prompting.
			}

		}
	}



	/**
	 * Instantiate the application class and start it.
	 * Call system.exit when the main command loop is done.
	 */
	public static void main(String[] args) {
		SpringApplication.run(NflxCliApplication.class, args);

		new NflxCliApplication().start(); // start the program
		System.exit(0);
	}

}


