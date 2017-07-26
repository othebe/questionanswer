import common.CouchDBClient;
import neuralnet.activationfunction.IdentityActivationFunction;
import neuralnet.activationfunction.SigmoidActivationFunction;
import neuralnet.layer.Layer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import ranknet.NeuralRankNet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnswerNet {
    private static final int INPUT_COUNT = FeatureGenerator.NUM_FEATURES;
    private static final int HIDDEN_COUNT = INPUT_COUNT * 2;

    public static void main(String[] args) {
        FeatureGenerator featureGenerator = new FeatureGenerator();
        Set<List<List<Double>>> featureSet = featureGenerator.getScoredFeaturesByThread();

        NeuralRankNet net = NeuralRankNet.Builder()
                .setLearningRate(0.1D)
                .addLayer(Layer.Builder().setInCount(INPUT_COUNT).setOutCount(HIDDEN_COUNT).setActivationFunction(SigmoidActivationFunction.INSTANCE).build())
                .addLayer(Layer.Builder().setInCount(HIDDEN_COUNT).setOutCount(HIDDEN_COUNT).setActivationFunction(SigmoidActivationFunction.INSTANCE).build())
                .addLayer(Layer.Builder().setInCount(HIDDEN_COUNT).setOutCount(1).setActivationFunction(IdentityActivationFunction.INSTANCE).build())
                .build();

        train(net, featureSet);
    }

    private static void train(NeuralRankNet net, Set<List<List<Double>>> featureSet) {
        Iterator<List<List<Double>>> iterator = featureSet.iterator();
        while (iterator.hasNext()) {
            List<List<Double>> threadFeatures = iterator.next();

            Iterator<List<Double>> iteratorI = threadFeatures.iterator();
            while (iteratorI.hasNext()) {
                List<Double> rowI = iteratorI.next();

                Iterator<List<Double>> iteratorJ = threadFeatures.iterator();
                while (iteratorJ.hasNext()) {
                    List<Double> rowJ = iteratorJ.next();

                    if (rowJ.size() == 7) {
                        int x  = 1;
                    }

                    double relevanceI = rowI.get(INPUT_COUNT);
                    double relevanceJ = rowJ.get(INPUT_COUNT);

                    if (relevanceI != relevanceJ) {
                        net.train(
                                getFeatureArray(rowI, INPUT_COUNT),
                                getFeatureArray(rowJ, INPUT_COUNT),
                                Nd4j.scalar(1));
                    }
                }
            }
        }
    }

    private static INDArray getFeatureArray(List<Double> row, int numFeatures) {
        double[] featureScores = new double[numFeatures];
        for (int i = 0; i < numFeatures; i++) {
            featureScores[i] = row.get(i);
        }

        return Nd4j.create(featureScores, new int[] { numFeatures, 1 });
    }
}