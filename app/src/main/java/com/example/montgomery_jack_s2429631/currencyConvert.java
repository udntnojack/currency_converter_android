package com.example.montgomery_jack_s2429631;

public class currencyConvert {
    private float ratio;

    public float convert(float value, float ratio){
        return value * ratio;
    }

    public float reverseConvert(float value, float ratio){
        return value /ratio;
    }

}
