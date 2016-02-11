package com.vitynskyi.executors;

import com.vitynskyi.loader.DataLoader;
import com.vitynskyi.model.InstanceWithDistances;
import com.vitynskyi.model.Instance;
import com.vitynskyi.utils.Distance;
import com.vitynskyi.utils.Normalize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fastforward on 17/07/15.
 */
public class GRNNExecutor {
    public static void main(String[] args) throws IOException {

        double minMape = 100;
        double minS1 = 0;
        double minS2 = 0;

        List<Instance> train = DataLoader.getTrainData("D:/datascience/procom_train.txt");
        List<Instance> test = DataLoader.getTrainData("D:/datascience/procom_use.txt");

        List<Instance> normTest = Normalize.normalizeTestWithOutput(train, test);
        List<Instance> normTrain = Normalize.normalizeTrainWithOutput(train);

        List<InstanceWithDistances> trainInstancesWithDistances = new ArrayList<InstanceWithDistances>();
        List<InstanceWithDistances> testInstancesWithDistances = new ArrayList<InstanceWithDistances>();


        for(int i = 0; i < normTrain.size(); i++) {
            Instance fromInstance = normTrain.get(i);
            List<Double> euclideanDistances = new ArrayList<Double>();

            for(int j = 0; j < normTrain.size(); j++) {
                    Instance toInstance = normTrain.get(j);
                    euclideanDistances.add(Distance.getEuclideanDistance(fromInstance, toInstance));
            }
            trainInstancesWithDistances.add(new InstanceWithDistances(fromInstance, euclideanDistances));
        }

        for(int i = 0; i < normTest.size(); i++) {
            Instance fromInstance = normTest.get(i);
            List<Double> euclideanDistances = new ArrayList<Double>();

            for(int j = 0; j < normTrain.size(); j++) {
                    Instance toInstance = normTrain.get(j);
                    euclideanDistances.add(Distance.getEuclideanDistance(fromInstance, toInstance));
            }
            testInstancesWithDistances.add(new InstanceWithDistances(fromInstance, euclideanDistances));
        }

        for (double s1 = 0.1; s1 <= 10; s1 += 0.1) {
            List<Double> grnnTrain = new ArrayList<Double>();
            for(int i = 0; i < trainInstancesWithDistances.size(); i++) {

                InstanceWithDistances instance = trainInstancesWithDistances.get(i);

                double gaussianDistanceSum = 0;
                double gaussianDistanceAndOutputSum = 0;

                for(int j = 0; j < instance.getEuclideanDistances().size(); j++) {
                    if (i != j) {
                        double euclideanDistance = instance.getEuclideanDistances().get(j);
                        double gaussianDistance = Distance.getGaussianDistance(euclideanDistance, s1);
                        gaussianDistanceAndOutputSum += gaussianDistance * normTrain.get(j).getOutputElement();
                        gaussianDistanceSum += gaussianDistance;
                    }
                }
                if(gaussianDistanceSum < Math.pow(10, -6)) {
                    gaussianDistanceSum = Math.pow(10, -6);
                }
                grnnTrain.add(gaussianDistanceAndOutputSum / gaussianDistanceSum);
            }

            for (double s2 = 0.1; s2 <= 10; s2 += 0.1) {
                double resultSum = 0;
                for(int i = 0; i < testInstancesWithDistances.size(); i++) {

                    InstanceWithDistances instance = testInstancesWithDistances.get(i);

                    double gaussianDistanceSum1 = 0;
                    double gaussianDistanceAndOutputSum1 = 0;

                    double gaussianDistanceSum2 = 0;
                    double gaussianDistanceAndOutputSum2 = 0;

                    for(int j = 0; j < instance.getEuclideanDistances().size(); j++) {
                        double euclideanDistance = instance.getEuclideanDistances().get(j);
                        double gaussianDistance1 = Distance.getGaussianDistance(euclideanDistance, s1);
                        double gaussianDistance2 = Distance.getGaussianDistance(euclideanDistance, s2);

                        gaussianDistanceAndOutputSum1 += gaussianDistance1 * normTrain.get(j).getOutputElement();
                        gaussianDistanceSum1 += gaussianDistance1;

                        gaussianDistanceAndOutputSum2 += gaussianDistance2 * (normTrain.get(j).getOutputElement() - grnnTrain.get(j));
                        gaussianDistanceSum2 += gaussianDistance2;
                    }
                    if(gaussianDistanceSum1 < Math.pow(10, -6)) {
                        gaussianDistanceSum1 = Math.pow(10, -6);
                    }
                    if(gaussianDistanceSum2 < Math.pow(10, -6)) {
                        gaussianDistanceSum2 = Math.pow(10, -6);
                    }
                    double result = (gaussianDistanceAndOutputSum1 / gaussianDistanceSum1) + (gaussianDistanceAndOutputSum2 / gaussianDistanceSum2);
                    resultSum += Math.abs((testInstancesWithDistances.get(i).getInstance().getOutputElement() - result) / testInstancesWithDistances.get(i).getInstance().getOutputElement());

                }
                System.out.println("S1=" + s1 + " S2=" + s2 +" MAPE=" + resultSum / testInstancesWithDistances.size() * 100);
                if(minMape > (resultSum / testInstancesWithDistances.size() * 100)) {
                    minMape = resultSum / testInstancesWithDistances.size() * 100;
                    minS1 = s1;
                    minS2 = s2;
                }
            }
        }
        System.out.println("S1=" + minS1 + " S2=" + minS2 +" MAPE=" + minMape);

        /*for (double s1 = 0.1; s1 <= 10; s1 += 0.1) {
            double resultSum = 0;
            for(int i = 0; i < testInstancesWithDistances.size(); i++) {

                InstanceWithDistances instance = testInstancesWithDistances.get(i);

                double gaussianDistanceSum1 = 0;
                double gaussianDistanceAndOutputSum1 = 0;

                for(int j = 0; j < instance.getEuclideanDistances().size(); j++) {
                    double euclideanDistance = instance.getEuclideanDistances().get(j);
                    double gaussianDistance1 = Distance.getGaussianDistance(euclideanDistance, s1);

                    gaussianDistanceAndOutputSum1 += gaussianDistance1 * normTrain.get(j).getOutputElement();
                    gaussianDistanceSum1 += gaussianDistance1;

                }
                if(gaussianDistanceSum1 < Math.pow(10, -6)) {
                    gaussianDistanceSum1 = Math.pow(10, -6);
                }

                double result = (gaussianDistanceAndOutputSum1 / gaussianDistanceSum1);
                resultSum += Math.abs((testInstancesWithDistances.get(i).getInstance().getOutputElement() - result) / testInstancesWithDistances.get(i).getInstance().getOutputElement());

            }
            if(minMape > (resultSum / testInstancesWithDistances.size() * 100)) {
                minMape = resultSum / testInstancesWithDistances.size() * 100;
                minS1 = s1;
            }
        }
        System.out.println("S1=" + minS1 + " MAPE=" + minMape);*/
    }
}
