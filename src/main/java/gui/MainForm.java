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

    {
        $$$setupUI$$$();
    }

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

    {
        $$$setupUI$$$();
    }
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        startButton = new JButton();
        startButton.setText("Download currency and build a chart");
        startButton.setToolTipText("Currencies are loaded in 100 calendar days");
        mainPanel.add(startButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Select a currency and click on the button");
        label1.setToolTipText("");
        mainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(266, 16), null, 0, false));
        currencyBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("$ USD");
        defaultComboBoxModel1.addElement("€ EUR");
        defaultComboBoxModel1.addElement("£ GBP");
        currencyBox.setModel(defaultComboBoxModel1);
        mainPanel.add(currencyBox, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(266, 30), null, 0, false));
        loadingLabel = new JLabel();
        loadingLabel.setEnabled(true);
        loadingLabel.setText("Status: Waiting for action");
        mainPanel.add(loadingLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        label = new JLabel();
        label.setEnabled(true);
        label.setText("Autoregressive integrated moving average CBR");
        mainPanel.add(label, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}