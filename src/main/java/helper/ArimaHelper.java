package helper;

import com.workday.insights.timeseries.arima.ArimaSolver;
import com.workday.insights.timeseries.arima.struct.ArimaModel;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.timeseriesutil.ForecastUtil;
import domain.ArimaForecastWithAic;

import static com.workday.insights.timeseries.arima.ArimaSolver.estimateARIMA;

public class ArimaHelper {

    public ArimaForecastWithAic autoForecastArima(final double[] data, final int forecastSize) {
        try {
            ArimaForecastWithAic currentModel = null;
            for (int p = 0; p <= 3; p++) {
                for (int q = 0; q <= 3; q++) {
                    currentModel = forecast(currentModel, data, forecastSize, p, q);
                }
            }
            return currentModel;
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to build ARIMA forecast: " + ex.getMessage());
        }
    }

    private ArimaForecastWithAic forecast(ArimaForecastWithAic currentModel, final double[] data,
                                          final int forecastSize, int p, int q) {
        final ArimaParams paramsForecast = new ArimaParams(p, 0, q, 1, 0, 1, 1);
        final ArimaModel fittedModel = estimateARIMA(
                paramsForecast, data, data.length, data.length + 1);
        final double aicValue = computeAICValidation(data, ForecastUtil.testSetPercentage, paramsForecast);
        if (currentModel == null || aicValue < currentModel.getAic()) {
            fittedModel.setRMSE(ArimaSolver.computeRMSEValidation(data, ForecastUtil.testSetPercentage, paramsForecast));
            final ArimaForecastWithAic forecastResult = new ArimaForecastWithAic(fittedModel.forecast(forecastSize), aicValue, p, q);
            forecastResult.getForecastResult().setSigma2AndPredicationInterval(fittedModel.getParams());
            return forecastResult;
        }
        return currentModel;
    }

    public double computeAICValidation(final double[] data, final double testDataPercentage, ArimaParams params) {
        int testDataLength = (int) (data.length * testDataPercentage);
        int trainingDataEndIndex = data.length - testDataLength;
        final ArimaModel result = estimateARIMA(params, data, trainingDataEndIndex,
                data.length);
        final double[] forecast = result.forecast(testDataLength).getForecast();
        return computeAIC(data, forecast, trainingDataEndIndex, 0, forecast.length);
    }

    public double computeAIC(final double[] left, final double[] right,
                             final int leftIndexOffset,
                             final int startIndex, final int endIndex) {
        double error_sum = 0.0;
        for (int i = startIndex; i < endIndex; ++i) {
            final double error = left[i + leftIndexOffset] - right[i];
            error_sum += Math.abs(error);
        }
        if (error_sum == 0.0) {
            return 0;
        } else {
            return (endIndex - startIndex) * Math.log(error_sum) + 2;
        }
    }
}