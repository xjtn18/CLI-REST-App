package com.company.nflxcli.io;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A class for managing a 2-column table of strings that can print to console.
 */
class Table {

	// Simple class for storing a pair of objects; used for the display tables.
	private static class KeyValuePair <KeyType, ValueType> {
		private final KeyType key;
		private final ValueType value;

		public KeyValuePair(KeyType key, ValueType value){
			this.key = key;
			this.value = value;
		}

		public KeyType getKey(){ return key; }
		public ValueType getValue(){ return value; }
	}


	// Attributes
	List<KeyValuePair<String,String>> rows;


	// Methods

	/** Constructor */
	Table(){
		rows = new ArrayList<>();
	}



	/**
	 * Removes all elements currently in the 'rows' list.
	 * @param key - The key string.
	 * @param value - The value string.
	 */
	void add(String key, String value){
		rows.add(new KeyValuePair<>(key, value));
	}



	/**
	 * Removes all elements currently in the 'rows' list.
	 */
	void clear(){
		rows.clear();
	}



	/**
	 * Prints the table to the command line
	 */
	void print(){
		int widestKey = 0, widestValue = 0;
		// find the widest string in each column
		for (KeyValuePair<String,String> row : rows){
			widestKey = Math.max(row.getKey().length(), widestKey);
			widestValue = Math.max(row.getValue().length(), widestValue);
		}
		int lineWidth = widestKey + widestValue + 10;

		// char array to store the horizontal lines of the table
		char[] line = new char[lineWidth];
		line[0] = ' ';
		Arrays.fill(line, 1, lineWidth-1, '-');
		line[lineWidth-1] = ' ';

		String format = "| %" + widestKey + "s  |  %-" + widestValue + "s |\n"; // table row format

		System.out.println(line);
		for (KeyValuePair<String,String> row : rows){
			System.out.printf(format, row.getKey(), row.getValue()); // print row
		}
		System.out.println(line);
	}

}
