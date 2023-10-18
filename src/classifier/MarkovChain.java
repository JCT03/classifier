package classifier;

import classifier.histogram.Histogram;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.math.BigDecimal;

public class MarkovChain<L,S> {
    private LinkedHashMap<L, HashMap<Optional<S>, Histogram<S>>> label2symbol2symbol = new LinkedHashMap<>();

    public Set<L> allLabels() {return label2symbol2symbol.keySet();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (L language: label2symbol2symbol.keySet()) {
            sb.append(language);
            sb.append('\n');
            for (Map.Entry<Optional<S>, Histogram<S>> entry: label2symbol2symbol.get(language).entrySet()) {
                sb.append("    ");
                sb.append(entry.getKey().get());
                sb.append(":");
                sb.append(entry.getValue().getPortions());
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Increase the count for the transition from prev to next.
    // Should pass SimpleMarkovTest.testCreateChains().
    public void count(Optional<S> prev, L label, S next) {
        if (!label2symbol2symbol.containsKey(label)) {
            label2symbol2symbol.put(label, new HashMap<>());
        } 
        if (!label2symbol2symbol.get(label).containsKey(prev)) {
            label2symbol2symbol.get(label).put(prev, new Histogram<>());
        }
        label2symbol2symbol.get(label).get(prev).bump(next);
    }

    // Returns P(sequence | label)
    // Should pass SimpleMarkovTest.testSourceProbabilities() and MajorMarkovTest.phraseTest()
    //
    // HINT: Be sure to add 1 to both the numerator and denominator when finding the probability of a
    // transition. This helps avoid sending the probability to zero.
    public BigDecimal probability(ArrayList<S> sequence, L label) {
        BigDecimal probability = new BigDecimal("1.0");
        HashMap<Optional<S>, Histogram<S>> languageMap = label2symbol2symbol.get(label);
        for (int i = 1; i < sequence.size();i++) {
            BigDecimal count = new BigDecimal("1.0");
            BigDecimal total = new BigDecimal("1.0");
            if (languageMap.containsKey(Optional.of(sequence.get(i-1)))) {
                count = count.add(BigDecimal.valueOf(languageMap.get(Optional.of(sequence.get(i-1))).getCountFor(sequence.get(i))));
                total = total.add(BigDecimal.valueOf(languageMap.get(Optional.of(sequence.get(i-1))).getTotalCounts()));
                probability = probability.multiply(count.divide(total, 10,RoundingMode.HALF_UP), new MathContext(10));
            }
        }
        return probability;
    }

    // Return a map from each label to P(label | sequence).
    // Should pass MajorMarkovTest.testSentenceDistributions()
    public LinkedHashMap<L,Double> labelDistribution(ArrayList<S> sequence) {
        LinkedHashMap<L,Double> ret = new LinkedHashMap<>();
        BigDecimal sum = new BigDecimal("0");
        for (L language: label2symbol2symbol.keySet()) {
            sum = sum.add(probability(sequence, language));
        }
        for (L language: label2symbol2symbol.keySet()) {
            ret.put(language, probability(sequence, language).divide(sum,10,RoundingMode.HALF_UP).doubleValue());
        }
        return ret;
    }

    // Calls labelDistribution(). Returns the label with highest probability.
    // Should pass MajorMarkovTest.bestChainTest()
    public L bestMatchingChain(ArrayList<S> sequence) {
        LinkedHashMap<L,Double> distribution = labelDistribution(sequence);
        L mostLikely = null;
        double max = -1.0;
        for (Map.Entry<L,Double> entry : distribution.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostLikely = entry.getKey();
            }
        }
        return mostLikely;
    }
}
