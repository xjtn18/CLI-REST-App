package com.company.nflxcli;

import com.company.nflxcli.response.*;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Objects;


/**
 * Handles the program's API requests.
 */ 
class ApiHandler {

	// Custom exception for throwing in special scenarios of bad API requests.
	public static class BadRequestException extends Exception {
		public BadRequestException(String msg){ super(msg); }
	}


	// Attributes
	private static final String weatherApiKey = "df5cfd59a9d48cfb1c016a0ae7d1ffef";
	private static final String coinApiKey = "33D50D5A-4308-456E-9060-45F05797217B";



	// Methods

	/** Constructor */
	public ApiHandler(){
		warmup();
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
	 * @param units - The unit standard we want our response to use.
	 * @return A weather response object.
	 */
	WeatherResponse getWeatherInCity(String cityName, String units){
		String uri = getWeatherUriBuilder(units)
			.queryParam("q", cityName)
			.build().toUriString();

		return request(uri, WeatherResponse.class);
	}



	/**
	 * Returns the weather response at the given coordinates.
	 * @param latitude - That latitude of our desired location.
	 * @param longitude - The longitude of our desired location.
	 * @param units - The unit standard we want our response to use.
	 * @return A weather response object.
	 */
	WeatherResponse getWeatherAtCoordinates(String latitude, String longitude, String units){
		String uri = getWeatherUriBuilder(units)
			.queryParam("lat", latitude)
			.queryParam("lon", longitude)
			.build().toUriString();

		return request(uri, WeatherResponse.class);
	}



	/**
	 * Returns the space response of the ISS.
	 */
	SpaceResponse getLocationISS(){
		return request("http://api.open-notify.org/iss-now.json", SpaceResponse.class);
	}



	/**
	 * Returns the crypto response of a given asset.
	 * @param assetID - The ID of the cryptocurrency we want info for.
	 * @return A crypto response object.
	 * @throws BadRequestException - if an unknown asset ID was queried through the coin API.
	 */
	CryptoResponse getCryptoData(String assetID) throws BadRequestException {
		// Empty asset path causes a WebClientException but with HTTP error code 200... catching it early here.
		if (Objects.equals(assetID, "")) throw new BadRequestException("Empty asset ID was queried.");

		String uri = getCryptoUriBuilder()
			.path(assetID)
			.build().toUriString();

		// @NOTE: CoinApi returns json wrapped entirely in a single array
		CryptoResponse[] cryptoResponse = request(uri, CryptoResponse[].class);

		if (cryptoResponse.length == 0){
			throw new BadRequestException("The API request failed; unknown asset ID was queried.");
		} else {
			return cryptoResponse[0];
		}
	}



	/**
	 * Gives us a weather URI component builder with the unchanging components already set.
	 * @param units - The unit standard we want our response to use.
	 * @return A preset UriComponentsBuilder instance for weather requests.
	 */
	private UriComponentsBuilder getWeatherUriBuilder(String units){
		return UriComponentsBuilder.newInstance()
			.scheme("https").host("api.openweathermap.org")
			.path("data/2.5/weather")
			.queryParam("units", units)
			.queryParam("appid", weatherApiKey);
	}


	/**
	 * Gives us a crypto URI component builder with the unchanging components already set.
	 * @return A preset UriComponentsBuilder instance for crypto requests.
	 */
	private UriComponentsBuilder getCryptoUriBuilder(){
		return UriComponentsBuilder.newInstance()
			.scheme("https").host("rest.coinapi.io")
			.path("v1/assets/")
			.queryParam("apikey", coinApiKey);
	}


	/**
	 * Makes random API calls to skip the cold start before the user's first request.
	 */
	private void warmup(){
		try {
			getLocationISS();
			getWeatherInCity("London", UnitStandard.imperialStandard.name);
			getCryptoData("BTC");
		} catch (WebClientResponseException | BadRequestException ignored) { }
	}



}
