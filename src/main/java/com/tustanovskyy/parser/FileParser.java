package com.tustanovskyy.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import au.com.bytecode.opencsv.CSVReader;

public class FileParser {

	private CSVReader csvReaderTrain;
	private CSVReader csvReaderTest;
	
	private char seperator = '\t';
		

	public void initializeCSVReaderTest(String filePathTest)
			throws FileNotFoundException {		
		csvReaderTest = new CSVReader(new FileReader(filePathTest), seperator);
	}

	public void initializeCSVReaderTrain(String filePathTrain)
			throws FileNotFoundException {
		csvReaderTrain = new CSVReader(new FileReader(filePathTrain), seperator);
	}

	public void initializeCSVReaderTest(File fileTest)
			throws FileNotFoundException {
		csvReaderTest = new CSVReader(new FileReader(fileTest), seperator);
	}

	public void initializeCSVReaderTrain(File fileTrain)
			throws FileNotFoundException {
		csvReaderTrain = new CSVReader(new FileReader(fileTrain), seperator);
	}

	public CSVReader getCsvReaderTrain() {
		return csvReaderTrain;
	}

	public void setCsvReaderTrain(CSVReader csvReaderTrain) {
		this.csvReaderTrain = csvReaderTrain;
	}

	public CSVReader getCsvReaderTest() {
		return csvReaderTest;
	}

	public void setCsvReaderTest(CSVReader csvReaderTest) {
		this.csvReaderTest = csvReaderTest;
	}

	public char getSeperator() {
		return seperator;
	}

	public void setSeperator(char seperator) {
		this.seperator = seperator;
	}

}
