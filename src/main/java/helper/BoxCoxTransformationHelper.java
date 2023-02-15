package helper;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.univariate.BrentOptimizer;
import org.apache.commons.math3.optimization.univariate.UnivariateOptimizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoxCoxTransformationHelper {
    public static List<Double> transform(List<Double> data) {
        return transform(data, lambdaSearch(data));
    }

    public static List<Double> transform(List<Double> data, double lam) {
        List<Double> transform = new ArrayList<>();

        if (lam == 0) {
            for (Double datum : data) {
                transform.add(Math.log(datum));
            }
        } else {
            for (Double datum : data) {
                transform.add((Math.pow(datum, lam) - 1.0) / lam);
            }
        }
        return transform;
    }

    public static double lambdaSearch(final List<Double> data) {
        return lambdaSearch(data, -1, 2);
    }

    public static double lambdaSearch(final List<Double> data, double lower, double upper) {
        UnivariateOptimizer solver = new BrentOptimizer(1e-10, 1e-14);
        return solver.optimize(100, x -> lambdaCV(data, x), GoalType.MINIMIZE, lower, upper).getPoint();
    }

    private static double lambdaCV(List<Double> data, double lam) {
        Iterator<Double> iter = data.iterator();
        List<Double> avg = new ArrayList<>();
        List<Double> result = new ArrayList<>();
        while (iter.hasNext()) {
            List<Double> l = new ArrayList<>();
            l.add(iter.next());
            if (iter.hasNext()) {
                l.add(iter.next());
            }
            avg.add(StatsHelper.average(l));
            result.add(StatsHelper.standardDeviation(l));
        }
        for (int i = 0; i < result.size(); i += 1) {
            result.set(i, result.get(i) / Math.pow(avg.get(i), 1 - lam));
        }
        return StatsHelper.standardDeviation(result) / StatsHelper.average(result);
    }
}