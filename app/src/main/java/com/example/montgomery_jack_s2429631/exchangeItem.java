package com.example.montgomery_jack_s2429631;

import java.io.Serializable;

public class exchangeItem implements Serializable {
    private String country;

    private String countryCode;
    private float amount;

    private String date;

    public String getCountry() {
        return country;
    }

    public float getAmount() {
        return amount;
    }



    public String getDate() {
        return date;
    }

    private String flagCode;

    public String getCountryCode() {
        return countryCode;
    }
    public String getflagCode() {
        return flagCode;
    }

    public exchangeItem(String county, String countryCode, float amount, String date){
        this.date = date;
        this.country = county;
        this.countryCode = countryCode;
        this.amount = amount;
        this.flagCode = countryCode.substring(0, countryCode.length() - 1);

    }

    @Override
    public String toString(){
        return country;
    }

}
