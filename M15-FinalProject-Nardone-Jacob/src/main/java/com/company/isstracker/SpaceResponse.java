package com.company.isstracker;



public class SpaceResponse {

	public class Position {
		public String longitude, latitude;
	}

	public String message;
	public String timestamp;
	public Position iss_position;
}