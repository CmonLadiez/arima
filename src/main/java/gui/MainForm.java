package gui;

import domain.ArimaForecastWithAic;
import domain.Currency;
import domain.CurrencyRate;
import helper.ArimaHelper;
import lombok.Getter;
import lombok.Setter;
import service.CurrencyService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class MainForm extends JFrame {
    private JButton startButton;
    private JProgressBar progressBar;
    private JComboBox currencyBox;

    private JLabel label;
    private JPanel mainPanel;
    private JPanel panel;
    private final CurrencyService currencyService = new CurrencyService();
    private static final ArimaHelper ARIMA_HELPER = new ArimaHelper();

    public MainForm() {
        this.setContentPane(this.getMainPanel());
        this.setTitle("ARIMA");
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getProgressBar().setMaximum(31);
        this.pack();

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setValue(0);
                progressBar.update(progressBar.getGraphics());
                currencyService.updateCurrencyRates(progressBar);
                if (progressBar.getValue() < progressBar.getMaximum() - 1) {
                    progressBar.setValue((progressBar.getMaximum() - 1));
                    progressBar.update(progressBar.getGraphics());
                }
                List<CurrencyRate> rates = currencyService.getAllCurrencyRatesByCurrency(Currency.valueOf(
                        Objects.requireNonNull(currencyBox.getSelectedItem()).toString().substring(2)));
                ArimaForecastWithAic forecastResult = ARIMA_HELPER.autoForecastArima(
                        rates.stream().mapToDouble(CurrencyRate::getValue).toArray(), 5);
                progressBar.setValue(progressBar.getValue() + 1);
                progressBar.update(progressBar.getGraphics());
                Chart chart = new Chart();
                chart.createChartPanel(rates, forecastResult);
            }
        });
    }

}