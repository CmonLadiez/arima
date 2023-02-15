package helper;

import java.util.List;

public class StatsHelper {
    public static double average(List<Double> data) {
        double total = 0;
        for (double item : data) {
            total += item;
        }
        return total / data.size();
    }

    public static double variance(List<Double> data) {
        double avg = average(data);
        double total = 0;
        if (data.size() == 1) {
            return 0.0;
        }
        for (double item : data) {
            total += (item - avg) * (item - avg);
        }
        return total / (data.size() - 1);
    }

    public static double standardDeviation(List<Double> data) {
        return Math.sqrt(variance(data));
    }

    public static boolean hipoTest(double pValue) {
        return pValue > 0.1;
    }
}