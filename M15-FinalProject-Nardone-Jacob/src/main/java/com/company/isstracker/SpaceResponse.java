package com.company.nflxcli;


public class SpaceResponse {

	public static class Coordinate {
		public String longitude, latitude;
	}

	public String message;
	public String timestamp;
	public Coordinate iss_position;
}