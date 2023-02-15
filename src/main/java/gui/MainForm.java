package gui;

import domain.Currency;
import domain.CurrencyRate;
import domain.StationarySeries;
import lombok.Getter;
import service.ArimaService;
import service.CurrencyService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

@Getter
public class MainForm extends JFrame {
    private final CurrencyService currencyService = new CurrencyService();
    private final ArimaService arimaService = new ArimaService();
    private JButton startButton;
    private JComboBox currencyBox;
    private JLabel label;
    private JLabel loadingLabel;
    private JPanel mainPanel;

    public MainForm() {
        this.setContentPane(this.getMainPanel());
        this.setTitle("ARIMA");
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        startButton.addActionListener(actionEvent -> {
            try {
                buttonsSwitch(false);
                updateLoadingLabel("Status: Downloading currencies (1/2)", Color.BLACK);
                currencyService.updateCurrencyRates();
                List<CurrencyRate> rates = currencyService.getAllCurrencyRatesByCurrency(Currency.valueOf(
                        Objects.requireNonNull(currencyBox.getSelectedItem()).toString().substring(2)));

                updateLoadingLabel("Status: Building Arima models (2/2)", Color.BLACK);
                StationarySeries series = arimaService.calculateOptimalModel(rates);
                System.out.println(series.getArima().order());
                System.out.println(series.getArima().forecast(2));

                buttonsSwitch(true);
                updateLoadingLabel("Status: Finished", Color.GREEN);
                ChartsForm chartsForm = new ChartsForm(series);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null,
                        String.format("Error: %s", e.getMessage()));
            }
        });
    }

    public void updateLoadingLabel(String text, Color color) {
        loadingLabel.setText(text);
        loadingLabel.setForeground(color);
        super.update(this.getGraphics());
    }

    public void buttonsSwitch(boolean flag) {
        startButton.setEnabled(flag);
        currencyBox.setEnabled(flag);
    }
}