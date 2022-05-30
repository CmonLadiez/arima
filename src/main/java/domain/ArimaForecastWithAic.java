package domain;

import com.workday.insights.timeseries.arima.struct.ForecastResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArimaForecastWithAic {
    private ForecastResult forecastResult;
    private double aic;
    private int p;
    private int q;
}