package com.company.nflxcli;

import com.company.nflxcli.api.*;
import com.company.nflxcli.response.*;
import com.company.nflxcli.io.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClientResponseException;



class App {

	// Attributes
	private final ConsoleIO consoleIO;
	private final ApiHandler apiHandler;
	private UnitSystem unitSystem;


	// Methods

	/** Constructor */
	public App(){
		consoleIO = new ConsoleIO();
		apiHandler = new ApiHandler();
		unitSystem = UnitSystem.IMPERIAL; // initialize to 'imperial' units
	}



	/**
	 * Runs the settings menu loop.
	 * Loop ends when the user selects the 'back' option.
	 */
	public void settingsMenuLoop(){
		boolean looping = true;

		while (looping){
			consoleIO.printSettingsMenu(unitSystem); 

			try {
				int choice = consoleIO.promptForNumberInRange("\nEnter a number to toggle a setting:", 0, 2);

				switch (choice){
					case 0: {
						looping = false;
						break;

					} case 1: { // Swap unit standard
						unitSystem = (unitSystem == UnitSystem.IMPERIAL)
							? UnitSystem.METRIC : UnitSystem.IMPERIAL;
						break;

					} case 2: { // Change verbosity
						consoleIO.toggleVerbosity();
						break;
					}
				}

			} catch (NumberFormatException nfe){
				consoleIO.log("\nError: Please enter a number.");
				 // wait for user to press Enter again before re-prompting.
				consoleIO.promptForInput("\n[press 'Enter' to continue]");

			} catch (InputOutOfRangeException ore){
				consoleIO.log("\nError: " + ore.getMessage());	
				 // wait for user to press Enter again before re-prompting.
				consoleIO.promptForInput("\n[press 'Enter' to continue]");
			}

		}

	}



	/**
	 * Runs the main menu loop.
	 * Loop ends when user selects the 'quit' option.
	 */
	public void mainMenuLoop() {
		boolean looping = true;

		// Main program command loop
		while (looping){
			boolean backedOutSubMenu = false;

			// display menu options to the user
			consoleIO.printMainMenu();

			try {
				// get user's selected option
				int choice = consoleIO.promptForNumberInRange("\nEnter a number to select an option:", 0, 5);

				switch (choice){
					case 0: { // Quit
						looping = false;
						consoleIO.log("Exiting program; may take a moment...");
						break;

					} case 1: { // Weather in a city
						// Get city name from the user
						WeatherResponse weatherResponse = apiHandler.getWeatherInCity(
							consoleIO.promptForInput("Please enter a city name:"),
							unitSystem.name
						);

						System.out.printf("\n\n--- Current weather in %s, %s ---\n",
							weatherResponse.name,
							weatherResponse.sys.country
						);
						consoleIO.printWeatherResponse(weatherResponse, unitSystem);
						break;


					} case 2: { // Location of the ISS
						SpaceResponse spaceResponse = apiHandler.getLocationISS();
						// Get weather response data to check for a city & country that's below the ISS
						WeatherResponse weatherResponse = apiHandler.getWeatherAtCoordinates(
							spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude,
							unitSystem.name
						);

						consoleIO.log("\n\n--- Current ISS location ---");
						consoleIO.printSpaceResponse(spaceResponse, weatherResponse);
						break;


					} case 3: { // Location and weather at the ISS
						SpaceResponse spaceResponse = apiHandler.getLocationISS();
						WeatherResponse weatherResponse = apiHandler.getWeatherAtCoordinates(
							spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude,
							unitSystem.name
						);

						// display ISS location information first, and then the weather at that location
						consoleIO.log("\n\n--- Current ISS location and weather ---");
						consoleIO.printSpaceResponse(spaceResponse, weatherResponse);
						consoleIO.printWeatherResponse(weatherResponse, unitSystem);
						break;


					} case 4: { // Current crypto prices
						// Get a crypto asset name from the user
						CryptoResponse cryptoResponse = apiHandler.getCryptoData(
							consoleIO.promptForInput("Please enter a crypto asset ID:")
						);

						// display that crypto's price info
						consoleIO.log("\n\n--- Current data on " + cryptoResponse.asset_id + " ---");
						consoleIO.printCryptoResponse(cryptoResponse);
						break;

					} case 5: { // Settings
						settingsMenuLoop();
						backedOutSubMenu = true;
						break;
					}
				}

			} catch (NumberFormatException e){
				consoleIO.log("\nError: Please enter a number.");

			} catch (InputOutOfRangeException | BadRequestException e){
				consoleIO.log("\nError: " + e.getMessage());

			} catch (WebClientResponseException e){
				int statusCode = e.getRawStatusCode();
				if (statusCode >= 400 && statusCode < 500){
					consoleIO.log("\nError: Invalid or unknown query.");
				} else if (statusCode >= 500 && statusCode < 600){
					consoleIO.log("\nServer error occurred. Please try again later.");
				} else {
					consoleIO.log("\nError: Unknown network error.");
				}

			} catch (Exception e){
				consoleIO.log("\nError: Unknown problem occurred.");
				consoleIO.log(e.getMessage());
			}

			if (looping && !backedOutSubMenu){
				 // wait for user to press Enter again before re-prompting.
				consoleIO.promptForInput("\n[press 'Enter' to continue]");
			}

		}
	}

}


@SpringBootApplication
public class NflxCliApplication {

	/**
	 * Instantiate the application class and start it.
	 * Call system.exit when the main command loop is done.
	 */
	public static void main(String[] args) {
		SpringApplication.run(NflxCliApplication.class, args);

		new App().mainMenuLoop(); // start the program
		System.exit(0);
	}

}




