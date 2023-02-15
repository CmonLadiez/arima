package helper;

import com.github.signaflo.timeseries.model.arima.Arima;
import com.github.signaflo.timeseries.model.arima.ArimaOrder;

import java.util.ArrayList;
import java.util.List;

public class ArimaHelper {

    public static List<ArimaOrder> prepareArimaModelsWithIntegrate(int d) {
        List<ArimaOrder> models = new ArrayList<>();
        for (int p = 0; p < 10; p++)
            for (int q = 0; q < 10; q++) {
                if (p == 0 && q == 0)
                    continue;
                models.add(ArimaOrder.order(p, d, q));
                models.add(ArimaOrder.order(p, d, q, Arima.Drift.INCLUDE));
                models.add(ArimaOrder.order(p, d, q, Arima.Constant.INCLUDE));
            }
        return models;
    }

    public static List<ArimaOrder> prepareArimaModels() {
        List<ArimaOrder> models = new ArrayList<>();
        for (int p = 0; p < 10; p++)
            for (int d = 0; d < 2; d++)
                for (int q = 0; q < 10; q++) {
                    if (p == 0 && q == 0)
                        continue;
                    models.add(ArimaOrder.order(p, d, q));
                    models.add(ArimaOrder.order(p, d, q, Arima.Drift.INCLUDE));
                    models.add(ArimaOrder.order(p, d, q, Arima.Constant.INCLUDE));
                }
        return models;
    }

    public static List<ArimaOrder> prepareArimaStationaryModels() {
        List<ArimaOrder> models = new ArrayList<>();
        for (int p = 0; p < 10; p++)
            for (int q = 0; q < 10; q++) {
                if (p == 0 && q == 0)
                    continue;
                models.add(ArimaOrder.order(p, 0, q));
                models.add(ArimaOrder.order(p, 0, q, Arima.Drift.INCLUDE));
                models.add(ArimaOrder.order(p, 0, q, Arima.Constant.INCLUDE));
            }
        return models;
    }
}