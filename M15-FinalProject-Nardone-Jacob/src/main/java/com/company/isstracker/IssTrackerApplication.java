package com.company.isstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.util.Scanner;
import java.util.InputMismatchException;



@SpringBootApplication
public class IssTrackerApplication {

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
	 * @param client - The WebClient object we use to make the API requests
	 * @param responseClass - The '.class' of whatever response object type we want
	 * @return The custom response object if the request was successful, else null.
	 */
	private <ResponseType> ResponseType request(WebClient client, Class<ResponseType> responseClass){
		ResponseType response = null;

		try {
			// get the Mono response
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

		return response;
	}



	private void weatherAtCity() throws BadRequestException {
		String cityLatitude = "";
		String cityLongitude = "";

		client = WebClient.create("https://api.openweathermap.org/data/2.5/weather?" +
			"lat=" + cityLatitude +
			"&lon=" + cityLongitude +
			"&appid=df5cfd59a9d48cfb1c016a0ae7d1ffef");
	}



	private void weatherAtISS() throws BadRequestException {
		// First get a response from the ISS API
		client = WebClient.create("http://api.open-notify.org/iss-now.json");
		SpaceResponse spaceResponse = this.<SpaceResponse>request(client, SpaceResponse.class);
		if (spaceResponse == null){
			throw new BadRequestException("Error: The API request failed.");
		}

		// Request from the weather API using the coordinates of the ISS
		client = WebClient.create("https://api.openweathermap.org/data/2.5/weather?" +
			"lat=" + spaceResponse.iss_position.latitude +
			"&lon=" + spaceResponse.iss_position.longitude +
			"&units=imperial" +
			"&appid=df5cfd59a9d48cfb1c016a0ae7d1ffef");

		// Map the final response into our custom response object
		WeatherResponse weatherResponse = this.<WeatherResponse>request(client, WeatherResponse.class);
		if (weatherResponse == null){
			throw new BadRequestException("Error: The API request failed.");
		}

		System.out.println("");
		System.out.println("Weather: " + weatherResponse.weather[0].main);
		System.out.println("Description: " + weatherResponse.weather[0].description);
		System.out.println("Tempurature: " + weatherResponse.main.temp + "°F");
	}



	private void locationOfISS() throws BadRequestException {
		client = WebClient.create("http://api.open-notify.org/iss-now.json");
		SpaceResponse response = this.<SpaceResponse>request(client, SpaceResponse.class);
		if (response == null){
			throw new BadRequestException("Error: The API request failed.");
		}
		System.out.println("");
		System.out.println("Latitude: " + response.iss_position.latitude);
		System.out.println("Longitude: " + response.iss_position.longitude);
	}



	private void currentCryptoPrices() throws BadRequestException {
	}



	/**
	 * Gets input from user through the command line.
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
		Scanner commandScanner = new Scanner(System.in);
		boolean running = true;

		// Main program command loop
		while (running){
			int i = 0;
			System.out.println("\n\n-----------------------------------------------");
			System.out.println("[" + ++i + "] Get weather in a city");
			System.out.println("[" + ++i + "] Get weather in the location of the ISS");
			System.out.println("[" + ++i + "] Get location of the ISS");
			System.out.println("[" + ++i + "] Get current cryptocurrency prices");
			System.out.println("[" + ++i + "] Quit");

			try {
				System.out.print("\nEnter the corresponding number of your selection: ");
				int choice = promptForNumberInRange(commandScanner, 1, i);

				switch (choice){
					case 1: // Weather in a city
						weatherAtCity();
						break;

					case 2: // Weather in the location of the iss
						weatherAtISS();
						break;

					case 3: // Location of the ISS
						locationOfISS();
						break;

					case 4: // Current crypto prices
						currentCryptoPrices();
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
				commandScanner.nextLine(); // wait for user to press Enter again before reprompting.
			}

		}

	}



	/**
	 * Instatiate the application class and start it.
	 * Call system.exit when the main command loop is done.
	 */
	public static void main(String[] args) throws OutOfRangeException, BadRequestException {
		SpringApplication.run(IssTrackerApplication.class, args);

		new IssTrackerApplication().start(); // run the program command loop
		System.exit(0);
	}

}
