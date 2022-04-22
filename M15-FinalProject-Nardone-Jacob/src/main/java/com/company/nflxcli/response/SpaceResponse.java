package com.company.nflxcli.response;


/**
 * 
 * The OpenNotify ISS info response class.
 * 
 */
public class SpaceResponse {

	public static class Coordinate {
		public String longitude, latitude;
	}

	public String message;
	public String timestamp;
	public Coordinate iss_position;
}