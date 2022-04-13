package com.company.isstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.util.Scanner;
import java.util.InputMismatchException;
import java.lang.Thread;
import java.lang.InterruptedException;



@SpringBootApplication
public class IssTrackerApplication {


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


	private <ResponseType> ResponseType request(WebClient client, Class<ResponseType> responseClass){
		ResponseType response = null;

		try {
			// create custom reponse object
			Mono<ResponseType> responseMono = client
				.get()
				.retrieve()
				.bodyToMono(responseClass);
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



	public void programLoop() {
		WebClient client = null;
		Scanner commandScanner = new Scanner(System.in);
		boolean running = true;
		String input;

		// Main program loop
		while (running){
			int i = 0;
			System.out.println("\n\n-----------------------------------------------");
			System.out.println("[" + ++i + "] Weather");
			System.out.println("[" + ++i + "] ISS Location");
			System.out.println("[" + ++i + "] Crypto Prices");
			System.out.println("[" + ++i + "] Quit");

			try {
				System.out.print("\nEnter the corresponding number of your choice: ");
				input = commandScanner.nextLine(); // grab input from the command line
				int choice = Integer.parseInt(input);

				if (choice < 1 || choice > i){
					throw new OutOfRangeException("Error: Number provided is out of range.");
				}

				switch (choice){
					case 1:
						break;
					case 2:
						{
							client = WebClient.create("http://api.open-notify.org/iss-now.json");
							SpaceResponse response = this.<SpaceResponse>request(client, SpaceResponse.class);
							if (response == null){
								throw new BadRequestException("Error: The API request failed.");
							}
							System.out.println("");
							System.out.println("Latitude: " + response.iss_position.latitude);
							System.out.println("Longitude: " + response.iss_position.longitude);
						}
						break;
					case 3:
						break;
					case 4:
						System.out.println("\nQuiting program; may take a moment...");
						running = false;
						break;
				}

			} catch (NumberFormatException | OutOfRangeException ioe){
				System.out.println("\nError: Please enter a valid number between 1 and " + i + " inclusive.");

			} catch (BadRequestException bre){
				System.out.println("\n" + bre.getMessage());
			}

			try {Thread.sleep(2000);} catch (InterruptedException ie){}; // sleep for 2 seconds
		}

	}



	public static void main(String[] args) throws OutOfRangeException, BadRequestException {
		SpringApplication.run(IssTrackerApplication.class, args);

		new IssTrackerApplication().programLoop(); // main program loop

		System.exit(0);
	}

}
