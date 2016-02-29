package com.tustanovskyy.grnn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.tustanovskyy.parser.FileParser;

public class TestGRNN {

	private final Logger LOGGER = Logger.getLogger(TestGRNN.class);

	public double testMape(FileParser fileParser, double sigma,
			int numberOfOutputs) {

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
		grnn.calculateNewGausianDistances();
		LOGGER.info("new gausian distances:\n" + grnn.getNewGausianDistances());
		double[] gausians = { 0.7, 0.8, 0.9 };
		for (int i = 0; i < 3; i++) {
			try {

				File file = new File("D:\\ai\\result" + (i + 1));

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				for (int k = 0; k < grnn.getLinesTrain().size(); k++) {
					if (grnn.getNewGausianDistances().get(i).get(k) > 0.0001) {
						bw.write(i + ". ");
						for (String vector : grnn.getLinesTrain().get(k)) {
							bw.write(vector);
						}
						bw.write(" " + gausians[i]);
					}
				}
				bw.close();

				System.out.println("Done");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return grnn.getMape();
	}

}
