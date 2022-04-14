package com.company.nflxcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.util.Scanner;
import java.util.InputMismatchException;



@SpringBootApplication
public class NflxCliApplication {

	//Attributes
	private WebClient client = null;



	// Custom exception classes
	private class OutOfRangeException extends Exception {
		public OutOfRangeException(String msg){
			super(msg);
		}
	}

	private class BadRequestException extends Exception {
		public BadRequestException(String msg){
			super(msg);
		}
	}



	/**
	 * Gets the response object of the type specified by the 'ResponseType'.
	 * 
	 * @param uri - URI of the API request
	 * @param responseClass - The '.class' of whatever response object type we want
	 * @return The custom response object if the request was successful
	 */
	private <ResponseType> ResponseType request(String uri, Class<ResponseType> responseClass) throws BadRequestException {
		ResponseType response = null;

		try {
			// get the Mono response
			WebClient client = WebClient.create(uri);
			Mono<ResponseType> responseMono = client
				.get()
				.retrieve()
				.bodyToMono(responseClass);

			// map the response into our custom response object
			response = responseMono.share().block();

		} catch (WebClientResponseException we){
			int statusCode = we.getRawStatusCode();
			if (statusCode >= 400 && statusCode < 500){
				System.out.println("\nClient error occured.");
			} else if (statusCode >= 500 && statusCode < 600){
				System.out.println("\nServer error occured.");
			}
			System.out.println(we.getMessage());

		} catch (Exception e) { // all other general exceptions
			System.out.println("\nError: " + e.getMessage());
		}

		if (response == null) throw new BadRequestException("Error: The API request failed.");
		else return response;
	}



	private void printWeather(WeatherResponse response){
		System.out.println("Weather: " + response.weather[0].main + " ~ " + response.weather[0].description);
		System.out.println("Temperature: " + response.main.temp + " °F");
		System.out.println("Feels like: " + response.main.feels_like + " °F");
		System.out.println("Wind speed: " + response.wind.speed + " mph");
		System.out.println("Humidity: " + response.main.humidity + " %");
	}



	private void printWeatherAtCity(String cityName) throws BadRequestException {
		// Request from the weather API passing in the given city name
		WeatherResponse response = this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"q=" + cityName +
			"&units=imperial" +
			"&appid=df5cfd59a9d48cfb1c016a0ae7d1ffef",
			WeatherResponse.class);

		System.out.println("\n--- Current weather in " + cityName + " ---");
		printWeather(response);
	}



	private void printLocationOfISS() throws BadRequestException {
		// Request from the ISS API to get its position
		SpaceResponse spaceResponse = this.<SpaceResponse>request("http://api.open-notify.org/iss-now.json", SpaceResponse.class);
		// Request from the weather API using the coordinates of the ISS to grab the city & country data, if it exists
		WeatherResponse weatherResponse = this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"lat=" + spaceResponse.iss_position.latitude +
			"&lon=" + spaceResponse.iss_position.longitude +
			"&appid=df5cfd59a9d48cfb1c016a0ae7d1ffef",
			WeatherResponse.class);

		System.out.println("\n--- Current ISS location ---");
		System.out.println("Latitude: " + spaceResponse.iss_position.latitude);
		System.out.println("Longitude: " + spaceResponse.iss_position.longitude);
		if (weatherResponse.sys.country == null)
			System.out.println("(Currently not above any country)");
		else
			System.out.println("(Currently above " + weatherResponse.name + ", " + weatherResponse.sys.country + ")");
	}



	private void printWeatherAtISS() throws BadRequestException {
		// Request from the ISS API to get its position
		SpaceResponse spaceResponse = this.<SpaceResponse>request("http://api.open-notify.org/iss-now.json", SpaceResponse.class);

		// display ISS location information first
		printLocationOfISS();

		// Request from the weather API using the coordinates of the ISS to grab the weather data
		WeatherResponse weatherResponse = this.<WeatherResponse>request("https://api.openweathermap.org/data/2.5/weather?" +
			"lat=" + spaceResponse.iss_position.latitude +
			"&lon=" + spaceResponse.iss_position.longitude +
			"&units=imperial" +
			"&appid=df5cfd59a9d48cfb1c016a0ae7d1ffef",
			WeatherResponse.class);

		// display weather at the ISS coordinates
		System.out.println("\n--- Current ISS weather ---");
		printWeather(weatherResponse);
	}



	private void printCurrentCryptoPrices() throws BadRequestException {
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
		int choice = Integer.parseInt(scanner.nextLine()); // grab input from command line, convert to int

		if (choice < start || choice > end){
			throw new OutOfRangeException("Error: Number provided is out of range.");
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
			int i = 0;
			System.out.println("\n\n-----------------------------------------------------------");
			System.out.println("[" + ++i + "] Get weather in a city");
			System.out.println("[" + ++i + "] Get location of the ISS");
			System.out.println("[" + ++i + "] Get weather in the location of the ISS");
			System.out.println("[" + ++i + "] Get current cryptocurrency prices");
			System.out.println("[" + ++i + "] Quit");

			try {
				System.out.print("\nEnter the corresponding number of your selection: ");
				int choice = promptForNumberInRange(scanner, 1, i);

				switch (choice){
					case 1: // Weather in a city
						System.out.print("\nPlease enter a city name: ");
						printWeatherAtCity(scanner.nextLine());
						break;

					case 2: // Weather in the location of the iss
						printLocationOfISS();
						break;

					case 3: // Location of the ISS
						printWeatherAtISS();
						break;

					case 4: // Current crypto prices
						printCurrentCryptoPrices();
						break;

					case 5: // Quit
						System.out.println("\nQuiting program; may take a moment...");
						running = false;
						break;
				}

			} catch (NumberFormatException | OutOfRangeException ioe){
				System.out.println("\nError: Please enter a valid number between 1 and " + i + " inclusive.");

			} catch (BadRequestException bre){
				System.out.println("\n" + bre.getMessage());
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
