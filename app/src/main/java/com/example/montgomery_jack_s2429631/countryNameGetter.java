package com.example.montgomery_jack_s2429631;

import android.os.Handler;
import android.os.Message;

import java.util.LinkedList;

public class countryNameGetter implements Runnable{
    private LinkedList<exchangeItem> items;
    private LinkedList<exchangeItem> matchingItems;
    private String letters;

    private final Handler handler;

    public countryNameGetter(LinkedList<exchangeItem> exchangeItems, String letters, Handler handler) {
        this.letters = letters.toUpperCase();
        this.items = exchangeItems;
        this.handler = handler;
        matchingItems = new LinkedList<exchangeItem>();
    }

    @Override
    public void run() {

        for(int i = 0; i < items.size(); i++){
            String code =  items.get(i).getCountry().toUpperCase();
            if(letters.length() == 1 && letters.charAt(0) == code.charAt(0)){
                matchingItems.add(items.get(i));
            }
            if(letters.length() == 2 &&
                    letters.charAt(0) == code.charAt(0) &&
                    letters.charAt(1) == code.charAt(1)){
                matchingItems.add(items.get(i));
            }
            if(letters.length() == 3 &&
                    letters.charAt(0) == code.charAt(0) &&
                    letters.charAt(1) == code.charAt(1) &&
                    letters.charAt(2) == code.charAt(2)){
                matchingItems.add(items.get(i));
            }
        }
        Message message = new Message();
        message.what = 3;
        message.obj = matchingItems;
        handler.sendMessage(message);

    }
}