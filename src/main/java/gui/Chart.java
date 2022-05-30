package gui;

import domain.ArimaForecastWithAic;
import domain.CurrencyRate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class Chart extends JFrame {
    private JFreeChart createChart(XYDataset dataset, String currency) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Currency rate " + currency,
                "Date",
                "Rate",
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }
        return chart;
    }

    public void createChartPanel(List<CurrencyRate> rates, ArimaForecastWithAic forecastResult) {
        JFreeChart chart = createChart(createDataset(rates, forecastResult), rates.get(0).getType().toString());
        chart.setPadding(new RectangleInsets(4, 8, 2, 2));
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(800, 600));
        this.setContentPane(panel);
        this.setTitle("Chart");
        this.setVisible(true);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
    }

    private XYDataset createDataset(List<CurrencyRate> rates, ArimaForecastWithAic forecastResult) {
        CurrencyRate maxDateCurrencyRate = rates.stream()
                .max(Comparator.comparing(CurrencyRate::getDate))
                .get();
        LocalDate maxCurrencyDate = maxDateCurrencyRate.getDate();

        TimeSeries s1 = new TimeSeries("Currency " + rates.get(0).getType());
        for (CurrencyRate rate : rates) {
            s1.add(new Day(Date.valueOf(rate.getDate())), rate.getValue());
        }
        TimeSeries s2 = new TimeSeries("Arima(p = " + forecastResult.getP() + " q = " + forecastResult.getQ() + ")");
        s2.add(new Day(Date.valueOf(maxCurrencyDate)), maxDateCurrencyRate.getValue());
        for (double rate : forecastResult.getForecastResult().getForecast()) {
            maxCurrencyDate = prepareWorkDayAhead(maxCurrencyDate);
            s2.add(new Day(Date.valueOf(maxCurrencyDate)), rate);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;
    }

    private LocalDate prepareWorkDayAhead(LocalDate date) {
        LocalDate newDate = date.plusDays(1);
        while (date.getDayOfWeek() == DayOfWeek.SATURDAY && date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            newDate = date.plusDays(1);
        }
        return newDate;

    }
}