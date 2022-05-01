package com.company.nflxcli;


/**
 * Enum for storing the different unit standards and the strings representing
 * 	their names and respective measurement symbols/abbreviations.
 * 	Useful for getting data back from our APIs in a specific unit of measure
 * 	and printing the data to the console.
 */
public enum UnitSystem {

	// Preset standards
	IMPERIAL("imperial", "mph", "°F"),
	METRIC("metric", "m/s", "°C");

	public final String name, speed, temp;

	UnitSystem(String n, String s, String t){
		name = n;
		speed = s;
		temp = t;
	}
}

