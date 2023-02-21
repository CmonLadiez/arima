package service;

import com.sun.tools.javac.Main;
import domain.Currency;
import domain.CurrencyRate;
import helper.FileHelper;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import static domain.Currency.findExceptRub;

public class CurrencyService {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final CentralBankCurrencyClient client = new CentralBankCurrencyClient();
    private final Map<LocalDate, List<CurrencyRate>> map = new HashMap<>();
    Logger log = Logger.getLogger(Main.class.getName());

    private static double toDouble(Element element) {
        return Double.parseDouble(
                element.getElementsByTagName("Value").item(0).getTextContent()
                        .replace(",", ".")
        );
    }

    public void updateCurrencyRates() {
        if (map.isEmpty()) {
            for (int i = 0; i < 100; i++) {
                LocalDate date = LocalDate.now().minusDays(i);
                List<CurrencyRate> currencyRates = downloadCurrency(date);
                if (currencyRates == null || currencyRates.isEmpty()) {
                    log.warning("updateCurrencyRates: currency rates are not exists on date " + date);
                }
                LocalDate fetchedDate = currencyRates.get(0).getDate();
                if (fetchedDate.isEqual(date)) {
                    map.put(date, currencyRates);
                    log.info("updateCurrencyRates: currency rates updated on date " + date);
                }
            }
        }
    }

    private @Nullable List<CurrencyRate> downloadCurrency(LocalDate demandDate) {
        byte[] bytes = client.downLoadCurrency(demandDate);
        if (bytes == null) return null;

        Document doc = parseDocument(bytes, FileHelper.prepareXmlParser());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("Valute");

        LocalDate fetchedDate = fetchDateFromXml(doc);
        List<CurrencyRate> currencies = new ArrayList<>(33); //https://www.cbr.ru/currency_base/P1/
        Set<Currency> lastCurrencies = EnumSet.allOf(Currency.class);
        lastCurrencies.remove(Currency.RUB);
        for (int i = 0; i < nodeList.getLength() && !lastCurrencies.isEmpty(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String charCode = element.getElementsByTagName("CharCode").item(0).getTextContent();
                Currency currency = findExceptRub(charCode);
                if (currency != null) {
                    currencies.add(new CurrencyRate(currency, toDouble(element), fetchedDate));
                    lastCurrencies.remove(currency);
                }
            }
        }

        if (!lastCurrencies.isEmpty()) {
            throw new RuntimeException("Some currencies haven't been downloaded: " + lastCurrencies);
        }

        return currencies;
    }

    private Document parseDocument(byte[] bytes, DocumentBuilder builder) {
        try {
            return builder.parse(new ByteArrayInputStream(bytes));
        } catch (IOException | SAXException e) {
            throw new RuntimeException("Can't parse or create xml currency document", e);
        }
    }

    private LocalDate fetchDateFromXml(Document doc) {
        String fetchedDateString = doc.getElementsByTagName("ValCurs").item(0)
                .getAttributes().getNamedItem("Date").getTextContent();
        return LocalDate.parse(fetchedDateString, dateFormatter);
    }

    public List<CurrencyRate> getAllCurrencyRatesByCurrency(Currency cur) {
        List<CurrencyRate> rates = new ArrayList<>();
        map.forEach((k, v) -> v.stream()
                .filter(currencyRate -> currencyRate.getType() == cur)
                .findFirst()
                .ifPresent(rates::add));
        return rates;
    }
}