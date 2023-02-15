package gui;

import com.github.signaflo.timeseries.TimeSeries;
import com.google.common.primitives.Doubles;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.signaflo.math.operations.DoubleFunctions.round;

public class Chart {
    public static void plot(TimeSeries obs, TimeSeries fitted, TimeSeries forecast, TimeSeries low, TimeSeries high) {
        Thread plotThread = new Thread(() -> {
            final List<Date> xAxisInit = prepareOffsetDateTime(obs);
            final List<Date> xAxisForecast = prepareOffsetDateTime(fitted);
            for (OffsetDateTime dateTime : forecast.observationTimes()) {
                xAxisForecast.add(Date.from(dateTime.toInstant()));
            }

            List<Double> initialList = Doubles.asList(round(obs.asArray(), 2));
            double[] fittedAndForecastDoubles = Stream.concat(fitted.asList().stream(), forecast.asList().stream())
                    .mapToDouble(i -> i)
                    .toArray();
            List<Double> fittedAndForecastList = Doubles.asList(round(fittedAndForecastDoubles, 2));

            final XYChart chart = new XYChartBuilder()
                    .theme(Styler.ChartTheme.Matlab)
                    .height(600)
                    .width(800)
                    .title("Arima model")
                    .build();

            XYSeries initialSeries = chart.addSeries("Initial data", xAxisInit, initialList);
            initialSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            initialSeries.setLineColor(Color.BLACK);
            initialSeries.setMarker(new Circle()).setMarkerColor(Color.BLACK);
            chart.getStyler().setCursorEnabled(true);

            XYSeries fittedSeries = chart.addSeries("Fitted and forecast data", xAxisForecast, fittedAndForecastList);
            fittedSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            fittedSeries.setMarker(new Circle()).setMarkerColor(Color.RED);

            prepareInterval(low, high, xAxisForecast, chart, 1);
            prepareInterval(low, high, xAxisForecast, chart, 2);

            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Arima model");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }

    private static List<Date> prepareOffsetDateTime(TimeSeries series) {
        final List<Date> list = new ArrayList<>();
        for (OffsetDateTime dateTime : series.observationTimes()) {
            list.add(Date.from(dateTime.toInstant()));
        }
        return list;
    }

    private static void prepareInterval(TimeSeries low, TimeSeries high, List<Date> xAxisForecast, XYChart chart, int n) {
        List<Double> intervalList = Stream.concat(
                        low.asList().stream().skip(n - 1).limit(1),
                        high.asList().stream().skip(n - 1).limit(1))
                .mapToDouble(i -> round(i, 2))
                .boxed()
                .collect(Collectors.toList());
        List<Date> date = new ArrayList<>();
        date.add(xAxisForecast.get(xAxisForecast.size() - 3 + n));
        date.add(xAxisForecast.get(xAxisForecast.size() - 3 + n));
        XYSeries firstInterval = chart.addSeries("Confidence interval (95%) " + n, date, intervalList);
        firstInterval.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        firstInterval.setMarker(new Circle()).setMarkerColor(Color.BLUE);
        firstInterval.setMarker(new Circle()).setMarkerColor(Color.RED);
        firstInterval.setLineStyle(SeriesLines.DASH_DASH);
    }

    public static void plotLikeHistogram(TimeSeries timeSeries, final String title, final String seriesName) {
        Thread plotThread = new Thread(() -> {
            XYChart chart = new XYChartBuilder()
                    .width(800)
                    .height(600)
                    .title(title)
                    .xAxisTitle(seriesName)
                    .yAxisTitle("Count")
                    .build();

            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
            chart.getStyler().setZoomEnabled(true);
            chart.getStyler().setZoomResetByDoubleClick(true);
            chart.getStyler().setZoomResetByButton(true);
            chart.getStyler().setZoomSelectionColor(new Color(0, 0, 192, 128));
            Histogram histogram = new Histogram(timeSeries.asList(), timeSeries.size());
            List<Double> collect = histogram.getxAxisData().stream()
                    .map(i -> round(i, 2))
                    .collect(Collectors.toList());
            List<Integer> collect1 = histogram.getyAxisData().stream()
                    .mapToInt(Double::intValue)
                    .boxed()
                    .collect(Collectors.toList());

            chart.addSeries(seriesName, collect, collect1);

            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }

    public static void plotAcf(TimeSeries timeSeries, final int k) {
        final double[] acf = timeSeries.autoCorrelationUpToLag(k);
        final double[] lags = new double[k + 1];
        for (int i = 1; i < lags.length; i++) {
            lags[i] = i;
        }
        final double upper = (-1. / timeSeries.size()) + (2 / Math.sqrt(timeSeries.size()));
        final double lower = (-1. / timeSeries.size()) - (2 / Math.sqrt(timeSeries.size()));
        final double[] upperLine = new double[lags.length];
        final double[] lowerLine = new double[lags.length];
        for (int i = 0; i < lags.length; i++) {
            upperLine[i] = upper;
        }
        for (int i = 0; i < lags.length; i++) {
            lowerLine[i] = lower;
        }

        Thread plotThread = new Thread(() -> {
            XYChart chart = new XYChartBuilder().theme(Styler.ChartTheme.GGPlot2)
                    .height(800)
                    .width(1200)
                    .title("Autocorrelations By Lag")
                    .build();
            XYSeries series = chart.addSeries("Autocorrelation", lags, acf);
            XYSeries series2 = chart.addSeries("Upper Bound", lags, upperLine);
            XYSeries series3 = chart.addSeries("Lower Bound", lags, lowerLine);
            chart.getStyler()
                    .setChartFontColor(Color.BLACK)
                    .setSeriesColors(new Color[]{Color.BLACK, Color.BLUE, Color.BLUE});

            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            series2.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line)
                    .setMarker(SeriesMarkers.NONE)
                    .setLineStyle(SeriesLines.DASH_DASH);
            series3.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line)
                    .setMarker(SeriesMarkers.NONE)
                    .setLineStyle(SeriesLines.DASH_DASH);
            JPanel panel = new XChartPanel<>(chart);
            JFrame frame = new JFrame("Autocorrelation by Lag");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        });
        plotThread.start();
    }
}