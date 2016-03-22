package com.tustanovskyy.main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tustanovskyy.grnn.TestGRNN;
import com.tustanovskyy.model.PairResult;
import com.tustanovskyy.parser.FileParser;

public class Main {

	public static void main(String[] args) {

		

		TestGRNN testGRNN = new TestGRNN();
		List<PairResult> pairResults = new ArrayList<PairResult>();
		double sigma = 0.01;
		while(sigma <= 1) {
			FileParser fileParser = new FileParser();
			fileParser.setSeperator('\t');
			try {
				
//				fileParser
//						.initializeCSVReaderTest("D:\\ai\\procom_use00.txt");
//				fileParser.initializeCSVReaderTrain("D:\\ai\\procom_train00.txt");
				fileParser
				.initializeCSVReaderTest("/home/myroslav/programming/GRNN/procom_use00.txt");
		fileParser.initializeCSVReaderTrain("/home/myroslav/programming/GRNN/procom_train00.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sigma += 1.999;			
			PairResult pairResult = new PairResult(sigma, testGRNN.testMape(fileParser, sigma, 1));
			pairResults.add(pairResult);
			System.out.println("sigma = " + sigma + ", MAPE = "
					+ pairResult.getMAPE());
		}	
		
		Collections.sort(pairResults);
		System.out.println("\n ----------------\nbest ten results: \n");		
		for(int i = 0; i < 10 && i < pairResults.size(); i++){
			System.out.println("sigma = " + pairResults.get(i).getSigma() + ", MAPE = "
					+ pairResults.get(i).getMAPE());
		}
		
		

	}

}
