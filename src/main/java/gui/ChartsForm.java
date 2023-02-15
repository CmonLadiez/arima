package gui;

import com.github.signaflo.timeseries.forecast.Forecast;
import com.numericalmethod.suanshu.stats.test.distribution.normality.ShapiroWilk;
import domain.StationarySeries;
import domain.StationingType;
import helper.StatsHelper;
import lombok.Getter;
import service.StationaryService;
import smile.timeseries.BoxTest;

import javax.swing.*;
import java.awt.*;

@Getter
public class ChartsForm extends JFrame {
    private JPanel mainPanel;
    private JButton acfResidualsPlotButton;
    private JButton residualsPlotButton;
    private JLabel shapiroLabel;
    private JLabel adfLabel;
    private JLabel ljungLabel;
    private JLabel arimaLabel;
    private JLabel initialSeries;
    private JButton modelPlotButton;

    {
        $$$setupUI$$$();
    }

    public ChartsForm(StationarySeries series) {
        this.setContentPane(this.getMainPanel());
        this.setTitle("ARIMA-Charts");
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        double pValueShapiro = new ShapiroWilk(series.getArima().predictionErrors().asArray()).pValue();
        double pValueAdf = StationaryService.adfTest(series.getArima().predictionErrors().asArray()).pValue();
        double pValueLjung = BoxTest.ljung(series.getArima().predictionErrors().asArray(),
                series.getDoubles().length / 2).pvalue;
        String initial = series.getStationingType() == StationingType.NONE ?
                "Failed to stationary the original series" :
                String.format("Original series was stationary by: %s",
                        series.getStationingType());

        initialSeries.setText(initial);
        updateLabel(shapiroLabel, "Residuals Shapiro-Wilk test: %s", true,
                StatsHelper.hipoTest(pValueShapiro));
        updateLabel(adfLabel, "Residuals ADF test: %s", false,
                StatsHelper.hipoTest(pValueAdf));
        updateLabel(ljungLabel, "Residuals Ljung–Box test: %s", true,
                StatsHelper.hipoTest(pValueLjung));
        arimaLabel.setText(String.format("<html>Best model: %s <br/>AIC: %s</html>\n ", series.getArima().order(), series.getArima().aic()));
        this.pack();

        modelPlotButton.addActionListener(actionEvent -> {
            Forecast forecast = series.getArima().forecast(2);
            if (series.getStationingType() == StationingType.BOXED_COXED) {
                Chart.plot(series.getArima().observations().backTransform(series.getD()),
                        series.getArima().fittedSeries().backTransform(series.getD()),
                        forecast.pointEstimates().backTransform(series.getD()),
                        forecast.lowerPredictionInterval().backTransform(series.getD()),
                        forecast.upperPredictionInterval().backTransform(series.getD()));
            } else
                Chart.plot(series.getArima().observations(),
                        series.getArima().fittedSeries(), forecast.pointEstimates(),
                        forecast.lowerPredictionInterval(), forecast.upperPredictionInterval()
                );

        });

        acfResidualsPlotButton.addActionListener(actionEvent -> {
            if (series.getStationingType() == StationingType.BOXED_COXED) {
                Chart.plotAcf(series.getArima().predictionErrors().backTransform(series.getD()),
                        series.getArima().predictionErrors().size() / 2);
            } else
                Chart.plotAcf(series.getArima().predictionErrors(),
                        series.getArima().predictionErrors().size() / 2);

        });
        residualsPlotButton.addActionListener(actionEvent -> {
            if (series.getStationingType() == StationingType.BOXED_COXED) {
                Chart.plotLikeHistogram(series.getArima().predictionErrors().backTransform(series.getD()), "Residuals", "residuals");
            } else
                Chart.plotLikeHistogram(series.getArima().predictionErrors(), "123", "values");
        });
    }

    public void updateLabel(JLabel label, String text, boolean expectedFlag, boolean actualFlag) {
        String str = expectedFlag == actualFlag ? "Passed" : "Failed";
        label.setText(String.format(text, str));
        label.setForeground(str.equals("Passed") ? Color.GREEN : Color.RED);
    }

    {
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        shapiroLabel = new JLabel();
        shapiroLabel.setText("Label");
        mainPanel.add(shapiroLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        adfLabel = new JLabel();
        adfLabel.setText("Label");
        mainPanel.add(adfLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ljungLabel = new JLabel();
        ljungLabel.setText("Label");
        mainPanel.add(ljungLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        acfResidualsPlotButton = new JButton();
        acfResidualsPlotButton.setText("Acf residuals plot");
        mainPanel.add(acfResidualsPlotButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        residualsPlotButton = new JButton();
        residualsPlotButton.setText("Residuals plot");
        mainPanel.add(residualsPlotButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modelPlotButton = new JButton();
        modelPlotButton.setText("Model plot");
        mainPanel.add(modelPlotButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        initialSeries = new JLabel();
        initialSeries.setText("");
        mainPanel.add(initialSeries, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        arimaLabel = new JLabel();
        arimaLabel.setText("Label");
        mainPanel.add(arimaLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}