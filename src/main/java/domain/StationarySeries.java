package domain;

import com.github.signaflo.timeseries.model.arima.Arima;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StationarySeries {
    final double[] doubles;
    final double d;
    @NonNull
    StationingType stationingType;
    Arima arima;
}