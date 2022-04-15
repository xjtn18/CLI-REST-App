package com.company.nflxcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Scanner;



@SpringBootApplication
public class NflxCliApplication {

	private final String weatherApiKey = "df5cfd59a9d48cfb1c016a0ae7d1ffef";
	private final String coinApiKey = "33D50D5A-4308-456E-9060-45F05797217B";


	// Custom exception classes
	private class OutOfRangeException extends Exception {
		public OutOfRangeException(String msg){ super(msg); }
	}

	private class BadRequestException extends Exception {
		public BadRequestException(String msg){ super(msg); }
	}



	/**
	 * Gets the response object of the type specified by the 'ResponseType'.
	 * 
	 * @param uri - URI of the API request
	 * @param responseClass - The '.class' of whatever response object type we want
	 * @return The custom response object if the request was successful
	 */
	private <ResponseType> ResponseType request(String uri, Class<ResponseType> responseClass){
		// get the Mono response
		WebClient client = WebClient.create(uri);

		Mono<ResponseType> responseMono = client
			.get()
			.retrieve()
			.bodyToMono(responseClass);

		// map the response into our custom response object and return it
		return responseMono.share().block();

	}



	/**
	 * Returns the weather response given a city
	 * 
	 */
	private WeatherResponse getWeatherInCity(String cityName){
		// Request from the weather API passing in the given city name
		return this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"q=" + cityName +
			"&units=imperial" +
			"&appid=" + weatherApiKey,
			WeatherResponse.class);
	}



	/**
	 * Returns the weather response given coordinates
	 * 
	 */
	private WeatherResponse getWeatherAtCoordinates(String latitude, String longitude){
		// Request from the weather API using the coordinates of the ISS to grab the city & country data, if it exists
		return this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"lat=" + latitude +
			"&lon=" + longitude +
			"&units=imperial" +
			"&appid=" + weatherApiKey,
			WeatherResponse.class);
	}



	/**
	 * Returns the space response of the ISS
	 * 
	 */
	private SpaceResponse getLocationISS(){
		return this.<SpaceResponse>request("http://api.open-notify.org/iss-now.json", SpaceResponse.class);
	}



	/**
	 * Returns the crypto response of a given asset
	 * 
	 */
	private CryptoResponse[] getCryptoData(String assetName) throws BadRequestException {
		if (assetName == "")
			throw new BadRequestException("Empty asset ID was queried.");

		CryptoResponse[] cryptoResponse = this.<CryptoResponse[]>request("https://rest.coinapi.io/v1/assets/" + assetName +
			"?apikey=" + coinApiKey,
			CryptoResponse[].class); // @NOTE: CoinApi returns json wrapped entirely in a single array

		if (cryptoResponse.length == 0)
			throw new BadRequestException("The API request failed; unknown asset ID was queried.");
		else
			return cryptoResponse;
	}



	/**
	 * Prompts for integer in the specified range as input from user through the command line.
	 * 
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
	 * Runs the main program command loop; asks users to select from a set of options.
	 * Loop ends when user selects the 'quit' option.
	 */
	public void start() {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		// Main program command loop
		while (running){
			// display menu options to the user
			Display.printMenu();

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
						Display.printWeatherData(weatherResponse);
						break;


					} case 2: { // Location of the ISS
						SpaceResponse spaceResponse = getLocationISS();
						// Get weather response data to check city + country that's below the ISS
						WeatherResponse weatherResponse = getWeatherAtCoordinates(spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude);

						System.out.println("\n--- Current ISS location ---");
						Display.printISSData(spaceResponse, weatherResponse);
						break;


					} case 3: { // Location and weather at the ISS
						SpaceResponse spaceResponse = getLocationISS();
						WeatherResponse weatherResponse = getWeatherAtCoordinates(spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude);

						System.out.println("\n--- Current ISS location and weather ---");
						// display ISS location information first
						Display.printISSData(spaceResponse, weatherResponse);
						// display weather at the ISS location
						Display.printWeatherData(weatherResponse);
						break;


					} case 4: { // Current crypto prices
						// Get a crypto asset name from the user
						System.out.print("\nPlease enter a crypto asset ID: ");
						CryptoResponse[] cryptoResponse = getCryptoData(scanner.nextLine().trim());

						System.out.println("\n--- Current data on " + cryptoResponse[0].asset_id + " ---");
						// display that crypto's price info
						Display.printCryptoData(cryptoResponse);
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
					System.out.println("\nClient error occured. Invalid or unknown query. Please try again.");
				} else if (statusCode >= 500 && statusCode < 600){
					System.out.println("\nServer error occured.");
				}

			} catch (BadRequestException bre){
				System.out.println("\nError: " + bre.getMessage());

			} catch (Exception e) { // all other general exceptions
				System.out.println("\nError: " + e.getMessage());
			}	

			if (running){
				System.out.println("\n[press 'Enter' to continue]");
				scanner.nextLine(); // wait for user to press Enter again before reprompting.
			}

		}
	}



	/**
	 * Instatiate the application class and start it.
	 * Call system.exit when the main command loop is done.
	 */
	public static void main(String[] args) throws OutOfRangeException, BadRequestException {
		SpringApplication.run(NflxCliApplication.class, args);

		new NflxCliApplication().start(); // run the program command loop
		System.exit(0);
	}

}
