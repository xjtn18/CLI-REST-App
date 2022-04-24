package com.company.nflxcli;

import com.company.nflxcli.response.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Scanner;



@SpringBootApplication
public class NflxCliApplication {

	// Attributes
	private ConsoleIO consoleIO;
	private ApiHandler apiHandler;
	private UnitStandard unitStandard;


	// Methods

	/**
	 * Constructor
	 */
	NflxCliApplication(){
		consoleIO = new ConsoleIO();
		apiHandler = new ApiHandler();
		unitStandard = UnitStandard.imperialStandard; // intialize to 'imperial' units
	}



	/**
	 * Runs the settings menu loop.
	 * Loop ends when the user selects the 'back' option.
	 */
	void settingsMenuLoop(){
		boolean looping = true;

		while (looping){
			consoleIO.clearConsole();
			consoleIO.printSettingsMenu(unitStandard); 


			try {
				int choice = consoleIO.promptForNumberInRange("\nEnter a number to select an option:", 0, 2);

				switch (choice){
					case 0: {
						looping = false;
						break;

					} case 1: { // Change unit standard
						unitStandard = (unitStandard == UnitStandard.imperialStandard)
							? UnitStandard.metricStandard : UnitStandard.imperialStandard;
						break;

					} case 2: { // Change verbosity
						consoleIO.toggleVerbosity();
						break;
					}
				}

			} catch (NumberFormatException nfe){
				consoleIO.log("\nError: Please enter a number.");
				consoleIO.promptForInput("\n[press 'Enter' to continue]"); // wait for user to press Enter again before re-prompting.

			} catch (ConsoleIO.OutOfRangeException ore){
				consoleIO.log("\nError: " + ore.getMessage());	
				consoleIO.promptForInput("\n[press 'Enter' to continue]"); // wait for user to press Enter again before re-prompting.
			}

		}

	}



	/**
	 * Runs the main menu loop.
	 * Loop ends when user selects the 'quit' option.
	 */
	void mainMenuLoop() {
		boolean looping = true;

		// Main program command loop
		while (looping){
			boolean backedOutSubMenu = false;

			// display menu options to the user
			consoleIO.clearConsole();
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
							unitStandard.name
						);

						System.out.printf("\n\n--- Current weather in %s, %s ---\n",
							weatherResponse.name,
							weatherResponse.sys.country
						);
						consoleIO.printResponse(weatherResponse, unitStandard);
						break;


					} case 2: { // Location of the ISS
						SpaceResponse spaceResponse = apiHandler.getLocationISS();
						// Get weather response data to check for a city & country that's below the ISS
						WeatherResponse weatherResponse = apiHandler.getWeatherAtCoordinates(
							spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude,
							unitStandard.name
						);

						consoleIO.log("\n\n--- Current ISS location ---");
						consoleIO.printResponse(spaceResponse, weatherResponse);
						break;


					} case 3: { // Location and weather at the ISS
						SpaceResponse spaceResponse = apiHandler.getLocationISS();
						WeatherResponse weatherResponse = apiHandler.getWeatherAtCoordinates(
							spaceResponse.iss_position.latitude,
							spaceResponse.iss_position.longitude,
							unitStandard.name
						);

						// display ISS location information first, and then the weather at that location
						consoleIO.log("\n\n--- Current ISS location and weather ---");
						consoleIO.printResponse(spaceResponse, weatherResponse);
						consoleIO.printResponse(weatherResponse, unitStandard);
						break;


					} case 4: { // Current crypto prices
						// Get a crypto asset name from the user
						CryptoResponse cryptoResponse = apiHandler.getCryptoData(
							consoleIO.promptForInput("Please enter a crypto asset ID:")
						);

						// display that crypto's price info
						consoleIO.log("\n\n--- Current data on " + cryptoResponse.asset_id + " ---");
						consoleIO.printResponse(cryptoResponse);
						break;

					} case 5: { // Settings
						settingsMenuLoop();
						backedOutSubMenu = true;
						break;
					}
				}

			} catch (NumberFormatException nfe){
				consoleIO.log("\nError: Please enter a number.");

			} catch (ConsoleIO.OutOfRangeException ore){
				consoleIO.log("\nError: " + ore.getMessage());

			} catch (WebClientResponseException wre){
				int statusCode = wre.getRawStatusCode();
				if (statusCode >= 400 && statusCode < 500){
					consoleIO.log("\nClient error occurred. Invalid or unknown query.");
				} else if (statusCode >= 500 && statusCode < 600){
					consoleIO.log("\nServer error occurred.");
				}

			} catch (Exception e){ // will catch any BadRequestExceptions
				consoleIO.log("\nError: " + e.getMessage());
			}

			if (looping && !backedOutSubMenu){
				consoleIO.promptForInput("\n[press 'Enter' to continue]"); // wait for user to press Enter again before re-prompting.
			}

		}
	}



	/**
	 * Instantiate the application class and start it.
	 * Call system.exit when the main command loop is done.
	 */
	public static void main(String[] args) {
		SpringApplication.run(NflxCliApplication.class, args);

		new NflxCliApplication().mainMenuLoop(); // start the program
		System.exit(0);
	}

}

