package com.company.nflxcli;


class UnitStandard {

	// Attributes
	public static final UnitStandard imperialStandard = new UnitStandard("imperial", "mph", "°F");
	public static final UnitStandard metricStandard = new UnitStandard("metric", "m/s", "°C");

	public String name, speed, temp;


	// Methods

	/**
	 * Constructor
	 */
	UnitStandard(String _name, String _speed, String _temp){
		name = _name;
		speed = _speed;
		temp = _temp;
	}

}
