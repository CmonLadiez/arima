package service;

import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.model.arima.Arima;
import com.github.signaflo.timeseries.model.arima.ArimaOrder;
import domain.CurrencyRate;
import domain.StationarySeries;
import helper.ArimaHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArimaService {
    private final StationaryService stationaryService = new StationaryService();

    public StationarySeries calculateOptimalModel(List<CurrencyRate> rates) {
        StationarySeries series = stationaryService.stationingSeries(rates);
        TimeSeries timeSeries = TimeSeries.from(series.getDoubles());
        List<ArimaOrder> models;

        switch (series.getStationingType()) {
            case NONE:
                models = ArimaHelper.prepareArimaModels();
                break;
            case BOXED_COXED:
                models = ArimaHelper.prepareArimaStationaryModels();
                break;
            case INTEGRATED:
                models = ArimaHelper.prepareArimaModelsWithIntegrate(((int) series.getD()));
                break;
            default:
                throw new RuntimeException(String.format("Unexpected value while calculating optimal model: %s",
                        series.getStationingType()));
        }

        List<Arima> arimas = new ArrayList<>();
        for (ArimaOrder model : models) {
            Arima tmp;
            try {
                tmp = Arima.model(timeSeries, model);
            } catch (Exception e) {
                continue;
            }
            if (!Double.isNaN(tmp.aic()))
                arimas.add(tmp);
        }
        series.setArima(arimas.stream()
                .min(Comparator.comparing(Arima::aic))
                .orElseThrow(() -> new RuntimeException("There is no successful arima models build")));
        return series;
    }
}