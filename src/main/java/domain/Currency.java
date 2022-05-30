package domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

@Getter
@RequiredArgsConstructor
public enum Currency {
    RUB("643"),
    USD("840"),
    EUR("978"),
    GBP("826"),
    CNY("156");

    private final String code;

    public static Currency find(String currency) {
        return EnumUtils.getEnum(Currency.class, currency);
    }

    public static Currency findExceptRub(String currency) {
        Currency currencyCase = find(currency);
        return currencyCase == RUB ? null : currencyCase;
    }
}