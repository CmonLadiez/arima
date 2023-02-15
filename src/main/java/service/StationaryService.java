package service;

import com.github.signaflo.timeseries.TimeSeries;
import domain.CurrencyRate;
import domain.StationarySeries;
import domain.StationingType;
import helper.BoxCoxTransformationHelper;
import org.hawkular.datamining.forecast.stats.AugmentedDickeyFullerTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.lang.Math.pow;

public class StationaryService {
    public static AugmentedDickeyFullerTest adfTest(double[] doublesArr) {
        return new AugmentedDickeyFullerTest(doublesArr, (int) pow(doublesArr.length - 1, 1. / 3.),
                AugmentedDickeyFullerTest.Type.NoInterceptNoTimeTrend);
    }

    public StationarySeries stationingSeries(List<CurrencyRate> rates) {
        double[] doublesArrInit = rates.stream()
                .mapToDouble(CurrencyRate::getValue)
                .toArray();
        double[] doublesArr = doublesArrInit.clone();
        int d = 0;
        AugmentedDickeyFullerTest adf = adfTest(doublesArrInit);

        while (adf.pValue() > 0.1 && d <= 2) {
            TimeSeries timeSeries = TimeSeries.from(doublesArr);
            doublesArr = timeSeries.difference().asArray();
            adf = adfTest(doublesArr);
            d++;
            if (d == 3) {
                List<Double> doubleList = BoxCoxTransformationHelper.transform(DoubleStream.of(doublesArrInit)
                        .boxed().collect(Collectors.toList()));
                double lambda = BoxCoxTransformationHelper.lambdaSearch(doubleList);
                doubleList = BoxCoxTransformationHelper.transform(doubleList, lambda);
                doublesArr = doubleList.stream()
                        .mapToDouble(i -> i)
                        .toArray();
                if (adfTest(doublesArr).pValue() > 0.1) {
                    return new StationarySeries(doublesArr, lambda, StationingType.BOXED_COXED);
                }
            }
        }
        return new StationarySeries(doublesArrInit, d, d == 3 ? StationingType.NONE : StationingType.INTEGRATED);
    }
}