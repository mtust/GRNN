package com.tustanovskyy.grnn;

import org.apache.log4j.Logger;

import com.tustanovskyy.parser.FileParser;

public class TestGRNN {
	
	private final Logger LOGGER = Logger.getLogger(TestGRNN.class);

	public double testMape(FileParser fileParser, double sigma, int numberOfOutputs) {
		

		GRNN grnn = new GRNN(fileParser, numberOfOutputs);
		grnn.setSigma(sigma);		
		grnn.calculateHauseDistances();
		grnn.calculateG();
		grnn.calculateMAPE();
		grnn.evklidDistance(grnn.getLinesTrain(), grnn.getLinesTest());
		StringBuilder stringBuilder = new StringBuilder();
		for (String element : grnn.getNearestVectorTest()) {
			stringBuilder.append(element + " ");
		}
		LOGGER.info("nearest test vector: " + stringBuilder.toString());
		grnn.setNewSigmas();
		return grnn.getMape();
	}

}
