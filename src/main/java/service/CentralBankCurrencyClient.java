package service;

import com.sun.tools.javac.Main;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class CentralBankCurrencyClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final RestTemplate restTemplate = new RestTemplate();
    private final String urlPrefix = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=";
    Logger log = Logger.getLogger(Main.class.getName());

    public CentralBankCurrencyClient() {
    }

    public byte[] downLoadCurrency(LocalDate date) {
        String appendixDate = date.format(formatter);
        String path = urlPrefix + appendixDate;

        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<byte[]> response = restTemplate.exchange(path,
                HttpMethod.GET, entity, byte[].class);
        if (response.getStatusCodeValue() == 200) {
            return response.getBody();
        }
        log.warning("Failed to get currencies from central bank: " + response.getStatusCodeValue());
        return null;
    }
}