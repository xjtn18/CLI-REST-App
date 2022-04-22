package com.company.nflxcli;

import com.company.nflxcli.response.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Scanner;



@SpringBootApplication
public class NflxCliApplication {

	/**
	 * Runs the main program command loop; asks users to select from a set of options.
	 * Loop ends when user selects the 'quit' option.
	 */
	public void start() {
		ConsoleIO consoleIO = new ConsoleIO();
		ApiHandler apiHandler = new ApiHandler();

		boolean running = true;


		// Main program command loop
		while (running){
			// display menu options to the user
			consoleIO.printMenu();

			try {
				// get user's selected option
				System.out.print("\nEnter a number to select an option: ");
				int choice = consoleIO.promptForNumberInRange(1, 5);

				switch (choice){

					case 1: { // Weather in a city
						// Get city name from the user
						System.out.print("Please enter a city name: ");
						WeatherResponse weatherResponse = apiHandler.getWeatherInCity(consoleIO.promptForInput());

						System.out.printf("\n\n--- Current weather in %s, %s ---\n",
							weatherResponse.name,
							weatherResponse.sys.country);
						consoleIO.printResponse(weatherResponse);
						break;


					} case 2: { // Location of the ISS
						SpaceResponse spaceResponse = apiHandler.getLocationISS();
						// Get weather response data to check for a city & country that's below the ISS
						WeatherResponse weatherResponse = apiHandler.getWeatherAtCoordinates(
							spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude
							);

						System.out.println("\n\n--- Current ISS location ---");
						consoleIO.printResponse(spaceResponse, weatherResponse);
						break;


					} case 3: { // Location and weather at the ISS
						SpaceResponse spaceResponse = apiHandler.getLocationISS();
						WeatherResponse weatherResponse = apiHandler.getWeatherAtCoordinates(
							spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude
							);

						// display ISS location information first, and then the weather at that location
						System.out.println("\n\n--- Current ISS location and weather ---");
						consoleIO.printResponse(spaceResponse, weatherResponse);
						consoleIO.printResponse(weatherResponse);
						break;


					} case 4: { // Current crypto prices
						// Get a crypto asset name from the user
						System.out.print("Please enter a crypto asset ID: ");
						CryptoResponse cryptoResponse = apiHandler.getCryptoData(consoleIO.promptForInput());

						// display that crypto's price info
						System.out.println("\n\n--- Current data on " + cryptoResponse.asset_id + " ---");
						consoleIO.printResponse(cryptoResponse);
						break;


					} case 5: { // Quit
						System.out.println("Exiting program; may take a moment...");
						running = false;
						break;
					}
				}


			} catch (NumberFormatException | ConsoleIO.OutOfRangeException ioe){
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
				System.out.print("\n[press 'Enter' to continue] ");
				consoleIO.promptForInput(); // wait for user to press Enter again before re-prompting.
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


