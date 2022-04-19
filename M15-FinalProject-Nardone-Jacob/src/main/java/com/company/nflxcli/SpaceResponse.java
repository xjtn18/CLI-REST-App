package com.company.nflxcli;


/**
 * 
 * The OpenNotify ISS info response class.
 * 
 */
class SpaceResponse {

	public static class Coordinate {
		public String longitude, latitude;
	}

	public String message;
	public String timestamp;
	public Coordinate iss_position;
}