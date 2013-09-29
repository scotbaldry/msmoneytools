package com.scotbaldry.msmoneytools;

import java.util.Date;

public class SecurityPrice {
    private String _symbol;
    private String _securityName;
    private String _price;
    private String _currency;
    private Date _date;
    private String _memo;

    public SecurityPrice(String symbol, String securityName, String price, String currency, Date date, String memo) {
        _symbol = symbol;
        _securityName = securityName;
        _price = price;
        _currency = currency;
        _date = date;
        _memo = memo;
    }

    public String getSymbol() {
        return _symbol;
    }

    public String getSecurityName() {
        return _securityName;
    }

    public String getPrice() {
        return _price;
    }

    public Date getDate() {
        return _date;
    }

    public String getMemo() {
        return _memo;
    }

    public String getCurrency() {
        return _currency;
    }
}
