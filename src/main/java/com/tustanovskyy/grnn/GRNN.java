package com.tustanovskyy.grnn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.tustanovskyy.parser.FileParser;

public class GRNN {

	final static double MIN_CH = 0.000001;

	private List<List<Double>> gausianDistance;
	private List<Double> g;

	private Double minEuklidDistance;

	private String[] minTestVector;

	private List<Double> newSigmas;

	private List<String[]> linesTestInputMinToTrain;
	private List<String[]> linesTrainInputMinToTrain;

	private List<String[]> linesTestOutput;
	private List<String[]> linesTrainOutput;

	private double sigma;

	private double mape;

	private List<List<Double>> evklidDistance = new ArrayList<List<Double>>();

	private final Logger LOGGER = Logger.getLogger(GRNN.class);

	public GRNN() {

	}

	public GRNN(FileParser fileParser, int numberOfOutput) {
		this();

		try {
			// linesTestInput = fileParser.getCsvReaderTest().readAll();
			//
			// linesTrainInput = fileParser.getCsvReaderTrain().readAll();
			this.initializeGRNNFromParser(fileParser, numberOfOutput);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GRNN(FileParser fileParser) {
		try {
			this.initializeGRNNFromParser(fileParser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initializeGRNNFromParser(FileParser fileParser)
			throws IOException {
		this.initializeGRNNFromParser(fileParser, 1);
	}

	public void initializeGRNNFromParser(FileParser fileParser,
			int numberOfOutput) throws IOException {

		linesTestInputMinToTrain = new ArrayList<String[]>();
		linesTrainInputMinToTrain = new ArrayList<String[]>();
		linesTestOutput = new ArrayList<String[]>();
		linesTrainOutput = new ArrayList<String[]>();
		String[] line;
		while ((line = fileParser.getCsvReaderTest().readNext()) != null) {
			String[] inputLine = new String[line.length - numberOfOutput];
			System.arraycopy(line, 0, inputLine, 0, line.length
					- numberOfOutput);
			linesTestInputMinToTrain.add(inputLine);
			String[] outputLine = new String[numberOfOutput];
			System.arraycopy(line, line.length - numberOfOutput, outputLine, 0,
					numberOfOutput);
			linesTestOutput.add(outputLine);
		}

		while ((line = fileParser.getCsvReaderTrain().readNext()) != null) {
			String[] inputLine = new String[line.length - numberOfOutput];
			System.arraycopy(line, 0, inputLine, 0, line.length
					- numberOfOutput);
			linesTrainInputMinToTrain.add(inputLine);
			String[] outputLine = new String[numberOfOutput];
			System.arraycopy(line, line.length - numberOfOutput, outputLine, 0,
					numberOfOutput);
			linesTrainOutput.add(outputLine);
		}
		// for (String[] strings : linesTestInput) {
		// for (String string : strings) {
		// System.out.print(Double.parseDouble(string.replace(',', '.')) + " ");
		// }
		// System.out.println();
		// }
		// for (String[] strings : linesTestOutput) {
		// for (String string : strings) {
		// System.out.print(Double.parseDouble(string.replace(',', '.')) + " ");
		// }
		// System.out.println();
		// }
		// for (String[] strings : linesTrainInput) {
		// for (String string : strings) {
		// System.out.print(Double.parseDouble(string.replace(',', '.')) + " ");
		// }
		// System.out.println();
		// }
		// for (String[] strings : linesTrainOutput) {
		// for (String string : strings) {
		// System.out.print(Double.parseDouble(string.replace(',', '.')) + " ");
		// }
		// System.out.println();
		// }
		// System.out.println(linesTestInput.size() + " - "
		// + linesTestOutput.size() + " - " + linesTestInput.get(0).length
		// + " - " + linesTestOutput.get(0).length + "|"
		// + linesTrainInput.size() + " - " + linesTrainOutput.size());

	}

	public void calculateMAPE() {
		double sum = 0;
		for (int t = 0; t < linesTestInputMinToTrain.size(); t++) {
			sum += Math.abs((Double.parseDouble(linesTestOutput.get(t)[0]
					.replace(',', '.')) - g.get(t))
					/ Double.parseDouble(linesTestOutput.get(t)[0].replace(',',
							'.')));
		}
		mape = (sum / linesTestInputMinToTrain.size()) * 100;
	}

	public void calculateHauseDistances() {

		gausianDistance = new ArrayList<List<Double>>();
		for (int t = 0; t < linesTestInputMinToTrain.size(); t++) {
			List<Double> line = new ArrayList<Double>();
			for (int i = 0; i < linesTrainInputMinToTrain.size(); i++) {
				line.add(Math.exp((-calculateR(linesTestInputMinToTrain.get(t),
						linesTrainInputMinToTrain.get(i)) / (sigma * sigma))));
			}

			gausianDistance.add(line);
		}

	}

	private double calculateR(String[] lineTest, String[] lineTrain) {
		double r = 0.0;
		for (int j = 0; j < lineTest.length; j++) {
			r += Math.pow(
					(Double.parseDouble(lineTest[j].replace(',', '.')) - Double
							.parseDouble(lineTrain[j].replace(',', '.'))), 2);

		}
		return r;
	}

	public void calculateG() {

		g = new ArrayList<Double>();

		for (int t = 0; t < linesTestInputMinToTrain.size(); t++) {
			double sumZn = 0;
			double sumCh = 0;
			for (int i = 0; i < linesTrainInputMinToTrain.size(); i++) {
				sumZn += (gausianDistance.get(t).get(i))
						* Double.parseDouble((linesTrainOutput.get(i)[0])
								.replace(',', '.'));
				sumCh += gausianDistance.get(t).get(i);
			}
			if (sumCh > MIN_CH) {
				g.add(sumZn / sumCh);
			} else {
				g.add(MIN_CH);
			}
		}
	}

	public List<List<Double>> evklidDistance(List<String[]> inputTrain,
			List<String[]> inputTest) {
		// List<List<Double>> evklidDistance = new ArrayList();
		LOGGER.info("inputTest.size() = " + inputTest.size());
		LOGGER.info("length = " + inputTest.get(0).length);
		LOGGER.info("euklid distances");
		for (int t = 0; t < inputTest.size(); t++) {
			List<Double> evklidArray = new ArrayList<Double>();
			for (int i = 0; i < inputTrain.size(); i++) {
				double sum = 0.0;
				for (int j = 0; j < inputTest.get(0).length; j++) {
					sum += Math.pow(
							Double.parseDouble((inputTrain.get(i)[j]).replace(
									',', '.'))
									- Double.parseDouble((inputTest.get(t)[j])
											.replace(',', '.')), 2);
				}
				evklidArray.add(Math.sqrt(sum));
			}
			LOGGER.info(evklidArray);
			evklidDistance.add(evklidArray);
		}
		return evklidDistance;
	}

	public List<Double> testWithLowDistance() {
		List<Double> testWithLowDistance = new ArrayList<Double>();
		for (List<Double> testVector : this.evklidDistance) {
			Collections.sort(testVector);
			testWithLowDistance.add(testVector.get(0));
		}
		return testWithLowDistance;
	}

	// public String[] getNearestTestVector(){
	// List<Double> testWithLowDistance = this.testWithLowDistance();
	//
	// }

	public String[] getNearestVectorTest() {
		int listListMinIndex = 0;
		int listMinIndex = 0;
		double minDistance = Double.MAX_VALUE;
		List<List<Double>> eDistance = evklidDistance;
		for (int i = 0; i < this.evklidDistance.size(); i++) {

			for (int j = 0; j < this.evklidDistance.get(i).size(); j++) {
				if (this.evklidDistance.get(i).get(j) < minDistance) {
					minDistance = this.evklidDistance.get(i).get(j);
					listListMinIndex = i;
					listMinIndex = j;
				}
			}
		}

		this.setMinEuklidDistance(minDistance);
		this.minTestVector = this.linesTestInputMinToTrain
				.get(listListMinIndex);
		LOGGER.info("minElement: " + minDistance);
		LOGGER.info("listListminIndex: " + listListMinIndex);
		return this.minTestVector;

	}

	public void setNewSigmas() {
		double[] gausians = { 0.7, 0.8, 0.9 };
		List<Double> newSigmas = new ArrayList<Double>();
		for (double gausian : gausians) {
			double sigma = this.getMinEuklidDistance()
			/ Math.sqrt(-Math.log(gausian));
			if(sigma < 0.01){
				newSigmas.add(0.01);
			} else {
				newSigmas.add(sigma);
			}
			
		}
		LOGGER.info("new sigmas: " + newSigmas);
		this.setNewSigmas(newSigmas);
	}

	public String[] getNearestVectorTrain() {
		int listListMinIndex = 0;
		int listMinIndex = 0;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < this.evklidDistance.size(); i++) {

			for (int j = 0; j < this.evklidDistance.get(i).size(); j++) {
				if (this.evklidDistance.get(i).get(j) < minDistance) {
					minDistance = this.evklidDistance.get(i).get(j);
					listListMinIndex = i;
					listMinIndex = j;
				}
			}
		}
		return this.linesTrainInputMinToTrain.get(listListMinIndex);
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public List<List<Double>> getGausianDistance() {

		return gausianDistance;
	}

	public void setGausianDistance(List<List<Double>> gausianDistance) {
		this.gausianDistance = gausianDistance;
	}

	public List<String[]> getLinesTest() {
		return linesTestInputMinToTrain;
	}

	public void setLinesTest(List<String[]> linesTest) {
		this.linesTestInputMinToTrain = linesTest;
	}

	public List<String[]> getLinesTrain() {
		return linesTrainInputMinToTrain;
	}

	public void setLinesTrain(List<String[]> linesTrain) {
		this.linesTrainInputMinToTrain = linesTrain;
	}

	public List<Double> getG() {
		return g;
	}

	public void setG(List<Double> g) {
		this.g = g;
	}

	public List<String[]> getLinesTestOutput() {
		return linesTestOutput;
	}

	public void setLinesTestOutput(List<String[]> linesTestOutput) {
		this.linesTestOutput = linesTestOutput;
	}

	public List<String[]> getLinesTrainOutput() {
		return linesTrainOutput;
	}

	public void setLinesTrainOutput(List<String[]> linesTrainOutput) {
		this.linesTrainOutput = linesTrainOutput;
	}

	public double getMape() {
		return mape;
	}

	public void setMape(double mape) {
		this.mape = mape;
	}

	public List<List<Double>> getEvklidDistance() {
		return evklidDistance;
	}

	public void setEvklidDistance(List<List<Double>> evklidDistance) {
		this.evklidDistance = evklidDistance;
	}

	public Double getMinEuklidDistance() {
		return minEuklidDistance;
	}

	public void setMinEuklidDistance(Double minEuklidDistance) {
		this.minEuklidDistance = minEuklidDistance;
	}

	public List<Double> getNewSigmas() {
		return newSigmas;
	}

	public void setNewSigmas(List<Double> newSigmas) {
		this.newSigmas = newSigmas;
	}

	public String[] getMinTestVector() {
		return minTestVector;
	}

	public void setMinTestVector(String[] minTestVector) {
		this.minTestVector = minTestVector;
	}

	public List<String[]> getLinesTestInputMinToTrain() {
		return linesTestInputMinToTrain;
	}

	public void setLinesTestInputMinToTrain(
			List<String[]> linesTestInputMinToTrain) {
		this.linesTestInputMinToTrain = linesTestInputMinToTrain;
	}

	public List<String[]> getLinesTrainInputMinToTrain() {
		return linesTrainInputMinToTrain;
	}

	public void setLinesTrainInputMinToTrain(
			List<String[]> linesTrainInputMinToTrain) {
		this.linesTrainInputMinToTrain = linesTrainInputMinToTrain;
	}

}
